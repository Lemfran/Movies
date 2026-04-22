#!/bin/bash
# 影评系统微服务一键关闭脚本
# 用法: ./stop.sh

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

# 服务配置: 目录名:端口:服务名 (关闭顺序与启动相反)
SERVICES=(
  "gateway-service:8080:API网关"
  "recommendation-service:8083:推荐服务"
  "user-portal-service:8082:用户门户服务"
  "Movies:8081:电影核心服务"
  "Movies/config-server:8888:配置中心"
  "eureka-server:8761:Eureka注册中心"
)

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 等待端口关闭
wait_for_close() {
  local port=$1
  local timeout=${2:-30}
  local count=0
  while lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; do
    sleep 1
    count=$((count + 1))
    if [ $count -ge $timeout ]; then
      return 1
    fi
  done
  return 0
}

echo "========================================"
echo "      影评系统微服务关闭脚本"
echo "========================================"
echo ""

for svc in "${SERVICES[@]}"; do
  IFS=':' read -r dir port name <<< "$svc"

  PID=$(lsof -Pi :$port -sTCP:LISTEN -t 2>/dev/null | head -1)

  if [ -n "$PID" ]; then
    log_info "正在关闭 $name (端口: $port, PID: $PID) ..."
    kill -15 $PID 2>/dev/null

    if wait_for_close "$port" 15; then
      log_info "$name 已关闭 ✓"
    else
      log_warn "$name 未正常关闭，强制终止..."
      kill -9 $PID 2>/dev/null || true
      if wait_for_close "$port" 10; then
        log_info "$name 已强制关闭 ✓"
      else
        log_error "$name 无法关闭，请手动处理"
      fi
    fi
  else
    log_warn "$name 端口 $port 未在运行，跳过"
  fi
done

echo ""
echo "========================================"
log_info "所有服务已关闭"
echo "========================================"
