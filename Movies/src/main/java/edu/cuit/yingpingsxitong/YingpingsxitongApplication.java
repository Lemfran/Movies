package edu.cuit.yingpingsxitong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "edu.cuit.yingpingsxitong")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableDiscoveryClient  // 开启服务发现，向 Eureka Server 注册
@EnableFeignClients(basePackages = "edu.cuit.yingpingsxitong.client")  // 开启Feign客户端

public class YingpingsxitongApplication {

	public static void main(String[] args) {
		SpringApplication.run(YingpingsxitongApplication.class, args);
	}

}
