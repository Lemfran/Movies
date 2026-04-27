# CUIT 影评系统 — 微服务架构

> 成都信息工程大学软件工程实践项目：基于 Spring Cloud 的影评系统微服务改造。

---

## 一、项目简介

本项目是一个完整的电影评论 Web 系统，采用微服务架构，包含以下核心能力：

- **电影管理**：管理员可添加、编辑、删除电影，上传海报
- **用户系统**：用户注册/登录、个人信息管理
- **影评系统**：用户可对电影评分、发表评论、回复评论、点赞
- **智能推荐**：基于用户评分历史的个性化推荐
- **统一认证**：JWT + Gateway 过滤器实现 API 统一认证
- **负载均衡**：核心服务多实例部署，Gateway 轮询分发
- **熔断降级**：Resilience4j 实现服务容错
- **Session 共享**：Spring Session Redis 实现多实例登录状态同步

---

## 二、技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | JDK 版本 |
| Spring Boot | 3.4.1 | 基础框架 |
| Spring Cloud | 2024.0.0 | 微服务套件 |
| Spring Cloud Gateway | 2024.0.0 | API 网关（替代 Zuul） |
| Spring Cloud LoadBalancer | 2024.0.0 | 负载均衡（替代 Ribbon） |
| Resilience4j | 2.x | 熔断降级（替代 Hystrix） |
| OpenFeign | 4.x | RPC 声明式调用 |
| Eureka | 4.x | 服务注册中心 |
| Spring Cloud Config | 4.x | 分布式配置中心 |
| MyBatis | 3.0.4 | ORM 框架 |
| MySQL | 8.x | 关系型数据库 |
| Redis | 7.x | Session 共享存储 |
| Thymeleaf | 3.x | 服务端模板引擎 |
| JWT (jjwt) | 0.12.6 | Token 认证 |
| Spring Security | 6.x | 安全框架 |

---

## 三、系统架构

```
                          用户请求
                             |
                             v
                   +---------------------+
                   |  Gateway (端口8080) |   <-- 统一入口
                   |   Spring Cloud      |       JWT认证过滤器
                   |   Gateway           |       请求日志过滤器
                   +----------+----------+
                              |
           +------------------+------------------+------------------+
           |                  |                  |                  |
           v                  v                  v                  v
   +---------------+  +-------------------+  +-------------------+  +---------------+
   | movies-service|  | user-portal-service|  | recommendation-svc|  | config-server |
   |   (端口8081)  |  |   (端口8082)       |  |    (端口8083)     |  |  (端口8888)   |
   |   (端口8084)  |  |   (端口8085)       |  |                   |  |               |
   |               |  |                    |  |                   |  | 外部化配置     |
   | MyBatis+MySQL |  | OpenFeign客户端    |  |  Resilience4j     |  |               |
   | OpenFeign RPC |  | Thymeleaf页面      |  |  熔断/重试/降级   |  |               |
   | 管理员后台     |  | 用户端门户         |  |                   |  |               |
   +---------------+  +-------------------+  +-------------------+  +---------------+
           |                  |                  |
           +------------------+------------------+
                              |
                              v
                    +-----------------+
                    | Eureka Server   |   <-- 服务注册中心
                    |   (端口8761)    |
                    +-----------------+
                              |
                              v
                    +-----------------+
                    |   Redis Server  |   <-- Session共享存储
                    |   (端口6379)    |
                    +-----------------+
```

### 服务职责

| 服务 | 职责 |
|------|------|
| **movies-service** (核心服务) | 数据库访问、管理员后台页面、REST API、RPC接口、JWT认证、日志记录 |
| **user-portal-service** (用户门户) | 用户端 Thymeleaf 页面、通过 OpenFeign 调用 movies-service |
| **recommendation-service** (推荐服务) | 热门推荐、高分推荐、个性化推荐、Resilience4j 熔断降级演示 |
| **gateway-service** (API网关) | 统一入口、JWT认证、路由转发、CORS处理 |
| **eureka-server** (注册中心) | 服务注册与发现 |
| **config-server** (配置中心) | 外部化配置管理（native模式） |

### 端口一览

| 服务 | 端口 | 说明 |
|------|------|------|
| Redis | 6379 | Session 共享存储 |
| eureka-server | 8761 | 服务注册中心控制台 |
| config-server | 8888 | 分布式配置中心 |
| movies-service | 8081 / 8084 | 核心业务服务（双实例负载均衡） |
| user-portal-service | 8082 / 8085 | 用户门户服务（双实例负载均衡） |
| recommendation-service | 8083 | 推荐服务 |
| gateway-service | 8080 | API 网关（统一入口） |

---

## 四、环境要求

### 必需软件

| 软件 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 17+ | Java 开发环境 |
| Maven | 3.8+ | 项目构建工具 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | Session 共享 |

### 开发环境验证

```bash
# 检查 Java 版本
java -version
# 应为 17 或更高

# 检查 Maven
mvn -version

# 检查 MySQL
mysql --version

# 检查 Redis
redis-cli ping
# 应返回 PONG
```

---

## 五、数据库配置与初始化

### 5.1 数据库连接配置

movies-service 的数据库配置位于：
`Movies/src/main/resources/application.properties`

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/yingpingxitong?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=123456
```

**请根据本地 MySQL 配置修改用户名和密码。**

### 5.2 执行 SQL 脚本初始化

项目 SQL 文件存放在 `sql/` 目录下：

| 文件 | 说明 |
|------|------|
| `sql/create_tables.sql` | 创建数据库、建表语句（movies / users / reviews / review_likes / logs） |
| `sql/init_data.sql` | 初始数据插入（12部电影、3个用户、11条影评、3条点赞） |

执行方式：

```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 执行建表脚本
source sql/create_tables.sql

# 3. 执行数据插入脚本
source sql/init_data.sql
```

或命令行直接执行：

```bash
mysql -u root -p < sql/create_tables.sql
mysql -u root -p < sql/init_data.sql
```

> **注意**：`users` 表中的密码为明文 `"123456"`，仅用于本地开发快速验证。系统使用 Spring Security 的 `BCryptPasswordEncoder` 对密码加密。建议通过前端注册页面创建用户，或手动将密码替换为 BCrypt 加密后的值（如 `$2a$10$...`）。
>
> `logs` 表无需初始数据，系统运行过程中由 Gateway 过滤器自动写入。

---

## 六、项目配置说明

### 6.1 movies-service

配置文件：`Movies/src/main/resources/application.properties`

```properties
spring.application.name=movies-service
server.port=8081

# 数据库配置（请根据本地环境修改）
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/yingpingxitong?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=123456

# Thymeleaf 模板
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# MyBatis
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=edu.cuit.yingpingsxitong.Dao
mybatis.configuration.map-underscore-to-camel-case=true

# Eureka 注册中心
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true

# Actuator 监控
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always

# Spring Session Redis（多实例共享 Session）
spring.session.store-type=redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=1
server.servlet.session.cookie.name=ADMIN_SESSION
```

### 6.2 user-portal-service

配置文件：`user-portal-service/src/main/resources/application.properties`

```properties
spring.application.name=user-portal-service
server.port=8082

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Thymeleaf
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.cache=false

# Spring Session Redis（与用户端隔离，使用 database=0）
spring.session.store-type=redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0
server.servlet.session.cookie.name=USER_SESSION
```

### 6.3 recommendation-service

配置文件：`recommendation-service/src/main/resources/application.properties`

```properties
spring.application.name=recommendation-service
server.port=8083

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Thymeleaf
spring.thymeleaf.cache=false

# Resilience4j 熔断配置
resilience4j.circuitbreaker.instances.movies-service.registerHealthIndicator=true
resilience4j.circuitbreaker.instances.movies-service.slidingWindowSize=5
resilience4j.circuitbreaker.instances.movies-service.failureRateThreshold=50
resilience4j.circuitbreaker.instances.movies-service.waitDurationInOpenState=10s

# Resilience4j 重试配置
resilience4j.retry.instances.movies-service.maxAttempts=3
resilience4j.retry.instances.movies-service.waitDuration=1s
```

### 6.4 gateway-service

配置文件：`gateway-service/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: gateway-service
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      routes:
        # 根路径重定向到用户登录页
        - id: root-redirect
          uri: no://op
          predicates:
            - Path=/
          filters:
            - RedirectTo=302, /user/login

        # movies-service API & RPC & 认证接口
        - id: movies-service-api
          uri: lb://movies-service
          predicates:
            - Path=/api/**, /rpc/**, /auth/**

        # 管理员后台（StripPrefix=1 去掉 /admin）
        - id: movies-service-admin
          uri: lb://movies-service
          predicates:
            - Path=/admin/**
          filters:
            - StripPrefix=1

        # 用户门户（StripPrefix=1 去掉 /user）
        - id: user-portal-service
          uri: lb://user-portal-service
          predicates:
            - Path=/user/**
          filters:
            - StripPrefix=1

        # 推荐服务（StripPrefix=1 去掉 /recommend）
        - id: recommendation-service
          uri: lb://recommendation-service
          predicates:
            - Path=/recommend/**
          filters:
            - StripPrefix=1

        # 文件上传访问
        - id: user-uploads
          uri: lb://user-portal-service
          predicates:
            - Path=/uploads/**

      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
            allowCredentials: false

server:
  port: 8080

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### 6.5 eureka-server

配置文件：`eureka-server/src/main/resources/application.properties`

```properties
spring.application.name=eureka-server
server.port=8761

# Eureka Server 不注册自己
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### 6.6 config-server

配置文件：`config-server/src/main/resources/application.properties`

```properties
spring.application.name=config-server
server.port=8888

# native 模式：从本地文件系统加载配置（避免中文路径编码问题）
spring.profiles.active=native
spring.cloud.config.server.native.search-locations=file:///Users/fanjinchen/工程实践/Movies/Movies/config-repo
```

> **注意**：`search-locations` 中的路径是**绝对路径**，请根据项目实际存放位置修改。配置仓库目录位于 `Movies/Movies/config-repo/` 下。

---

## 七、启动方式

### 7.1 一键启动（推荐）

```bash
./start.sh
```

启动顺序：Redis → Eureka (8761) → Config Server (8888) → movies-service (8081, 8084) → user-portal-service (8082, 8085) → recommendation-service (8083) → Gateway (8080)

脚本会自动：
1. 检查并启动 Redis
2. 编译打包所有服务（`mvn package -DskipTests`）
3. 按依赖顺序启动各服务
4. 等待端口就绪后提示启动成功

### 7.2 状态检查

```bash
./status.sh
```

### 7.3 一键关闭

```bash
./stop.sh
```

### 7.4 手动启动（IDEA 开发调试）

按以下顺序启动各服务的 `main` 方法：

1. `eureka-server`: `EurekaServerApplication.java`
2. `config-server`: `ConfigServerApplication.java`
3. `movies-service`: `YingpingsxitongApplication.java`（启动两次，分别加 VM 参数 `-Dserver.port=8081` 和 `-Dserver.port=8084`）
4. `user-portal-service`: `UserPortalApplication.java`（启动两次，`-Dserver.port=8082` 和 `-Dserver.port=8085`）
5. `recommendation-service`: `RecommendationApplication.java`
6. `gateway-service`: `GatewayApplication.java`

---

## 八、访问地址

启动成功后，通过浏览器访问：

| 入口 | 地址 | 说明 |
|------|------|------|
| 统一入口 | http://localhost:8080 | Gateway 网关，自动路由 |
| 用户端登录 | http://localhost:8080/user/login | 普通用户入口 |
| 管理后台 | http://localhost:8080/admin/login | 管理员入口 |
| Eureka 控制台 | http://localhost:8761 | 查看服务注册情况 |
| 配置中心 | http://localhost:8888/movies-service/default | 查看外部化配置 |

---

## 九、项目结构

```
Movies/
|
├── eureka-server/              # 服务注册中心
│   └── src/main/java/edu/cuit/eurekaserver/
│       └── EurekaServerApplication.java
│
├── config-server/              # 配置中心
│   └── src/main/resources/
│       └── application.properties
│
├── Movies/                     # 核心服务 (movies-service)
│   ├── config-repo/            # 配置文件仓库（Config Server 读取）
│   │   ├── movies-service.yml
│   │   ├── movies-service-dev.yml
│   │   └── movies-service-test.yml
│   ├── src/main/java/edu/cuit/yingpingsxitong/
│   │   ├── Controller/
│   │   │   ├── AuthController.java           # JWT 登录/注册
│   │   │   ├── ManagerController.java        # 管理员后台页面
│   │   │   ├── MovieRestController.java      # REST API
│   │   │   ├── MovieRpcController.java       # RPC 接口（供 Feign 调用）
│   │   │   ├── UserRpcController.java
│   │   │   ├── ReviewRpcController.java
│   │   │   └── LogRpcController.java
│   │   ├── Service/
│   │   ├── Dao/                 # MyBatis Mapper 接口
│   │   ├── Entity/              # 实体类
│   │   ├── util/
│   │   │   └── JwtUtil.java     # JWT 工具类
│   │   └── config/
│   │       └── SecurityConfig.java
│   └── src/main/resources/mapper/
│       ├── MovieMapper.xml
│       ├── UserMapper.xml
│       ├── ReviewMapper.xml
│       └── LogMapper.xml
│
├── user-portal-service/        # 用户门户服务
│   └── src/main/java/edu/cuit/userportal/
│       ├── UserPortalApplication.java
│       ├── client/              # OpenFeign 客户端
│       │   ├── MovieClient.java
│       │   ├── UserClient.java
│       │   └── ReviewClient.java
│       ├── controller/          # 页面 Controller
│       │   ├── PortalIndexController.java
│       │   ├── PortalMovieListController.java
│       │   ├── PortalReviewController.java
│       │   ├── PortalUserController.java
│       │   └── PortalRecommendationController.java
│       └── entity/              # DTO 实体类
│
├── recommendation-service/     # 推荐服务
│   └── src/main/java/edu/cuit/recommendation/
│       ├── config/
│       │   └── RestTemplateConfig.java
│       ├── controller/
│       │   └── RecommendationController.java
│       └── service/
│           ├── MovieClientService.java    # 带熔断的 HTTP 客户端
│           └── RecommendationService.java
│
├── gateway-service/            # API 网关
│   └── src/main/java/edu/cuit/gateway/
│       ├── GatewayApplication.java
│       └── filter/
│           ├── AuthFilter.java     # JWT 认证过滤器
│           └── LoggingFilter.java  # 请求日志过滤器
│
├── uploads/posters/            # 上传的海报文件
│
├── README.md                   # 本文档
├── init_movie_data.sql         # 电影数据初始化脚本
├── update_posters.sql          # 海报更新脚本
├── start.sh                    # 一键启动脚本
├── stop.sh                     # 一键关闭脚本
├── status.sh                   # 状态检查脚本
└── 微服务架构改造说明.md        # 详细技术文档
```

---

## 十、核心功能说明

### 10.1 评论回复与点赞（已实现）

- **评论回复**：在影评下方可发表回复（`parent_id` 指向原评论）
- **点赞**：每条评论可点赞/取消点赞（`review_likes` 表记录，联合主键防重）
- **级联删除**：管理员删除评论时，自动删除其所有回复和点赞记录

### 10.2 智能推荐

- **热门推荐**：按平均分降序排列
- **高分推荐**：筛选评分 >= 4.0 的电影
- **个性化推荐**：基于用户历史评分计算偏好分数，推荐平均分最接近且未看过的电影

### 10.3 统一认证

- 页面端（`/user/**`、`/admin/**`）使用 **Session** 认证
- API 端（`/api/**`、`/rpc/**`）使用 **JWT Token** 认证
- Gateway 全局过滤器统一拦截并验证 JWT

---

## 十一、同组同学快速上手

1. **克隆项目**到本地
2. **安装环境**：JDK 17、Maven、MySQL 8、Redis
3. **初始化数据库**：执行本文档第 5 节的 SQL 语句
4. **修改配置**：将 `config-server` 中的 `search-locations` 改为你的本地绝对路径
5. **启动服务**：运行 `./start.sh`，或按顺序在 IDEA 中启动
6. **访问系统**：打开 http://localhost:8080

如遇问题，请查看 `logs/` 目录下的各服务日志文件。

---

## 十二、技术替代说明

本项目使用 **Spring Cloud 2024.0.0**，以下组件已被官方移除：

| 原组件 | 状态 | 替代方案 |
|--------|------|---------|
| Zuul | 已弃用 | Spring Cloud Gateway |
| Ribbon | 已弃用 | Spring Cloud LoadBalancer |
| Hystrix | 已停止维护 | Resilience4j |

---

## 十三、改造中遇到的关键问题

| 问题 | 原因 | 解决方案 |
|------|------|---------|
| Config Server 中文路径报错 | Git URI 编码问题 | 改用 `native` 模式 |
| movies-service 数据库配置获取失败 | Config Server 未正确启动 | `application.properties` 中添加本地 fallback |
| movies-service 启动失败 | Security 依赖引入后缺少配置 | 添加 `SecurityConfig` 关闭 CSRF 并放行所有请求 |
| Resilience4j 熔断不生效 | `@CircuitBreaker` 在同类方法自调用上，AOP 无法拦截 | 将 HTTP 调用抽离到独立的 `MovieClientService` |
| Gateway CORS 报错 | `allowCredentials: true` 不能与 `allowedOrigins: "*"` 同时使用 | 改为 `allowCredentials: false` |
| JWT `userId` 类型不匹配 | Gateway 用 `String.class` 读取 Integer 类型的 claim | 改为 `claims.get("userId").toString()` |
| 新注册用户无法登录 | 默认 `permission=false` | 数据库中手动 `UPDATE users SET permission=1` |
| 多实例登录状态丢失 | Session 存储在单个 JVM 内存中 | Spring Session Redis 实现跨实例共享 |
| 管理员端电影编辑日期未预填充 | HTML5 date 输入需要 `yyyy-MM-dd` 格式 | 使用 `th:value="${#dates.format(...)}"` |
| 电影海报外链失效 | 外部图片地址可能失效 | 添加 `onerror` 回退到占位图 + 支持本地上传 |

---

> 项目作者：CUIT 软件工程实践小组
> 如有疑问，请在项目 Issues 中提出。
