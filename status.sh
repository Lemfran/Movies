#!/bin/bash
# 影评系统微服务状态检查脚本
# 用法: ./status.sh

# 颜色输出
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 服务配置: 端口:服务名:URL
SERVICES=(
  "8761:Eureka注册中心:http://localhost:8761"
  "8888:配置中心:http://localhost:8888/movies-service/dev"
  "8081:电影核心服务:http://localhost:8081"
  "8082:用户门户服务:http://localhost:8082"
  "8083:推荐服务:http://localhost:8083"
  "8080:API网关:http://localhost:8080"
)

echo "========================================"
echo "      影评系统微服务运行状态"
echo "========================================"
echo ""
printf "%-6s %-20s %-10s %s\n" "端口" "服务名称" "状态" "进程ID"
echo "------ -------------------- ---------- --------"

for svc in "${SERVICES[@]}"; do
  IFS=':' read -r port name url <<< "$svc"
  PID=$(lsof -Pi :$port -sTCP:LISTEN -t 2>/dev/null | head -1)

  if [ -n "$PID" ]; then
    echo -e "${port}   ${name}     ${GREEN}运行中${NC}   PID:${PID}"
  else
    echo -e "${port}   ${name}     ${RED}未运行${NC}   -"
  fi
done

echo ""
echo "========================================"
echo ""
