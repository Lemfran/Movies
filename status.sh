#!/bin/bash
# 影评系统微服务状态检查脚本（支持多实例）
# 用法: ./status.sh

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

INSTANCES=(
  "6379:Redis缓存"
  "8761:Eureka注册中心"
  "8888:配置中心"
  "8081:电影核心服务-1"
  "8084:电影核心服务-2"
  "8082:用户门户服务-1"
  "8085:用户门户服务-2"
  "8083:推荐服务"
  "8080:API网关"
)

echo "========================================"
echo "      影评系统微服务运行状态"
echo "========================================"
echo ""
printf "%-6s %-22s %-10s %s\n" "端口" "服务名称" "状态" "进程ID"
echo "------ ---------------------- ---------- --------"

running_count=0
stopped_count=0

for svc in "${INSTANCES[@]}"; do
  IFS=':' read -r port name <<< "$svc"
  PID=$(lsof -Pi :$port -sTCP:LISTEN -t 2>/dev/null | head -1)

  if [ -n "$PID" ]; then
    echo -e "${port}   ${name}       ${GREEN}运行中${NC}   PID:${PID}"
    running_count=$((running_count + 1))
  else
    echo -e "${port}   ${name}       ${RED}未运行${NC}   -"
    stopped_count=$((stopped_count + 1))
  fi
done

echo ""
echo "========================================"
echo "  运行中: ${running_count}  |  未运行: ${stopped_count}"
echo "========================================"
echo ""
