package edu.cuit.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT 认证全局过滤器
 *
 * 功能（对应 Zuul 的 pre 类型过滤器）：
 * 1. 拦截所有经过网关的请求
 * 2. 对白名单路径直接放行
 * 3. 从请求头或 Cookie 中提取 JWT Token
 * 4. 验证 Token 的有效性
 * 5. 验证通过后，将用户信息注入请求头转发给下游服务
 * 6. 验证失败返回 401 Unauthorized
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    // JWT 密钥（与 movies-service 保持一致）
    private static final String SECRET = "cuit-movies-review-system-secret-key-2024";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // 白名单路径（不需要 JWT 验证）
    private static final List<String> WHITE_LIST = List.of(
            "/auth/login",
            "/auth/register",
            "/eureka",
            "/actuator",
            "/api/info",
            "/rpc/movie/info",
            "/user/login",
            "/user/register",
            "/admin/login"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 白名单路径直接放行
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }

        // 从请求头获取 Token
        String token = extractToken(request);

        if (token == null) {
            return unauthorized(exchange.getResponse(), "缺少认证令牌");
        }

        try {
            // 验证 JWT
            Claims claims = Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.get("userId").toString();
            String username = claims.get("username", String.class);

            // 将用户信息注入请求头，转发给下游服务
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-Username", username)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            return unauthorized(exchange.getResponse(), "令牌无效或已过期: " + e.getMessage());
        }
    }

    /**
     * 从请求头或 Cookie 中提取 JWT Token
     */
    private String extractToken(ServerHttpRequest request) {
        // 优先从 Authorization 头提取
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 其次从 Cookie 提取
        if (request.getCookies().getFirst("jwt_token") != null) {
            return request.getCookies().getFirst("jwt_token").getValue();
        }

        return null;
    }

    /**
     * 检查路径是否在白名单中
     *
     * 页面端点（/user/, /admin/）使用 Session 认证，不需要 JWT
     * API 端点（/api/, /rpc/, /recommend/）需要 JWT
     */
    private boolean isWhiteList(String path) {
        if (path.equals("/") || path.startsWith("/user/") || path.startsWith("/admin/") || path.startsWith("/recommend/") || path.startsWith("/uploads/")) {
            return true;
        }
        return WHITE_LIST.stream().anyMatch(path::startsWith);
    }

    /**
     * 返回 401 未授权响应
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = "{\"code\":401,\"message\":\"" + message + "\"}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }

    @Override
    public int getOrder() {
        // 优先级：在日志过滤器之后执行
        return 1;
    }
}
