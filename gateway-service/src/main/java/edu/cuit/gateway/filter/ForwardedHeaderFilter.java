package edu.cuit.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 添加 X-Forwarded-Host 和 X-Forwarded-Proto 请求头，
 * 让后端服务能正确识别 Gateway 地址，但**不**添加 X-Forwarded-Prefix
 * （路径前缀由模板和 Controller 手动管理）。
 */
@Component
public class ForwardedHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String host = request.getHeaders().getFirst("Host");
        String scheme = request.getURI().getScheme();

        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-Forwarded-Host", host)
                .header("X-Forwarded-Proto", scheme)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
