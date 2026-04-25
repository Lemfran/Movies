#!/bin/bash
# 影评系统微服务一键启动脚本（支持多实例负载均衡）
# 用法: ./start.sh

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$PROJECT_DIR/logs"
mkdir -p "$LOG_DIR"

# 要编译的项目（目录名，去重）
BUILD_PROJECTS=(
  "eureka-server"
  "Movies/config-server"
  "Movies"
  "user-portal-service"
  "recommendation-service"
  "gateway-service"
)

# 启动实例: 目录:端口:服务名
# movies-service 和 user-portal-service 各启动2个实例体现负载均衡
INSTANCES=(
  "eureka-server:8761:Eureka注册中心"
  "Movies/config-server:8888:配置中心"
  "Movies:8081:电影核心服务-1"
  "Movies:8084:电影核心服务-2"
  "user-portal-service:8082:用户门户服务-1"
  "user-portal-service:8085:用户门户服务-2"
  "recommendation-service:8083:推荐服务"
  "gateway-service:8080:API网关"
)

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

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

# 查找 jar 包
find_jar() {
  local dir=$1
  ls "$PROJECT_DIR/$dir/target/" 2>/dev/null | grep -v 'original' | grep '.jar$' | head -1
}

echo "========================================"
echo "      影评系统微服务启动脚本"
echo "      支持多实例负载均衡"
echo "========================================"
echo ""

# 检查 Redis
if ! redis-cli ping >/dev/null 2>&1; then
  log_info "Redis 未运行，正在启动..."
  redis-server --daemonize yes
  sleep 1
  if redis-cli ping >/dev/null 2>&1; then
    log_info "Redis 启动成功 ✓"
  else
    log_error "Redis 启动失败，请先安装 Redis"
    exit 1
  fi
else
  log_info "Redis 已在运行"
fi
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
for dir in "${BUILD_PROJECTS[@]}"; do
  name=$(basename "$dir")
  log_info "打包 $name ($dir) ..."
  mvn -f "$PROJECT_DIR/$dir/pom.xml" package -DskipTests -q
  log_info "$name 打包完成"
done
echo ""

# 启动各实例
for instance in "${INSTANCES[@]}"; do
  IFS=':' read -r dir port name <<< "$instance"

  if check_port "$port"; then
    log_warn "$name 端口 $port 已被占用，可能已在运行，跳过启动"
    continue
  fi

  JAR=$(find_jar "$dir")
  if [ -z "$JAR" ]; then
    log_error "$name 未找到 jar 包，请检查 $dir/target/"
    continue
  fi

  LOG_NAME=$(echo "$dir" | tr '/' '-')-$port
  log_info "正在启动 $name (端口: $port) ..."
  nohup java -Dserver.port=$port -jar "$PROJECT_DIR/$dir/target/$JAR" \
    > "$LOG_DIR/$LOG_NAME.log" 2>&1 &

  if wait_for_port "$port" 60; then
    log_info "$name 启动成功 ✓"
  else
    log_error "$name 启动超时，请查看日志: $LOG_DIR/$LOG_NAME.log"
  fi
  sleep 2
done

echo ""
echo "========================================"
log_info "所有服务启动完成"
echo ""
echo "单入口访问地址: http://localhost:8080"
echo ""
echo "已启动的多实例服务（负载均衡）:"
echo "  电影核心服务:  http://localhost:8081, http://localhost:8084"
echo "  用户门户服务:  http://localhost:8082, http://localhost:8085"
echo ""
echo "其他服务:"
echo "  Eureka 控制台: http://localhost:8761"
echo "  配置中心:      http://localhost:8888"
echo "  推荐服务:      http://localhost:8083"
echo ""
echo "日志目录: $LOG_DIR"
echo "========================================"
