#!/bin/bash
# 影评系统微服务一键启动脚本
# 用法: ./start.sh

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$PROJECT_DIR/logs"
mkdir -p "$LOG_DIR"

# 服务配置: 目录名:端口:服务名
SERVICES=(
  "eureka-server:8761:Eureka注册中心"
  "Movies/config-server:8888:配置中心"
  "Movies:8081:电影核心服务"
  "user-portal-service:8082:用户门户服务"
  "recommendation-service:8083:推荐服务"
  "gateway-service:8080:API网关"
)

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 等待端口就绪
wait_for_port() {
  local port=$1
  local timeout=${2:-60}
  local count=0
  while ! lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; do
    sleep 1
    count=$((count + 1))
    if [ $count -ge $timeout ]; then
      return 1
    fi
  done
  return 0
}

# 检查端口是否已被占用
check_port() {
  local port=$1
  if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
    return 0
  fi
  return 1
}

echo "========================================"
echo "      影评系统微服务启动脚本"
echo "========================================"
echo ""

# 检查 Java
if ! command -v java >/dev/null 2>&1; then
  log_error "未找到 Java，请确保 JDK 17+ 已安装"
  exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ] 2>/dev/null; then
  log_warn "Java 版本可能过低，建议使用 Java 17+"
fi

# 检查 Maven
if ! command -v mvn >/dev/null 2>&1; then
  log_error "未找到 Maven，请确保 Maven 已安装"
  exit 1
fi

# 先打包所有服务（跳过测试）
log_info "正在编译打包所有服务..."
for svc in "${SERVICES[@]}"; do
  IFS=':' read -r dir port name <<< "$svc"
  log_info "打包 $name ($dir) ..."
  mvn -f "$PROJECT_DIR/$dir/pom.xml" package -DskipTests -q
  log_info "$name 打包完成"
done
echo ""

# 启动各服务
for svc in "${SERVICES[@]}"; do
  IFS=':' read -r dir port name <<< "$svc"

  if check_port "$port"; then
    log_warn "$name 端口 $port 已被占用，可能已在运行，跳过启动"
    continue
  fi

  log_info "正在启动 $name (端口: $port) ..."
  nohup java -jar "$PROJECT_DIR/$dir/target/$(ls "$PROJECT_DIR/$dir/target/" | grep -v 'original' | grep '.jar$' | head -1)" \
    > "$LOG_DIR/$dir.log" 2>&1 &

  if wait_for_port "$port" 60; then
    log_info "$name 启动成功 ✓"
  else
    log_error "$name 启动超时，请查看日志: $LOG_DIR/$dir.log"
  fi
  sleep 2
done

echo ""
echo "========================================"
log_info "所有服务启动完成"
echo ""
echo "服务访问地址:"
echo "  Eureka 控制台: http://localhost:8761"
echo "  电影核心服务:  http://localhost:8081"
echo "  用户门户服务:  http://localhost:8082"
echo "  推荐服务:      http://localhost:8083"
echo "  配置中心:      http://localhost:8888"
echo ""
echo "日志目录: $LOG_DIR"
echo "========================================"
