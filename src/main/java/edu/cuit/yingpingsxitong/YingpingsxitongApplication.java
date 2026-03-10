package edu.cuit.yingpingsxitong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "edu.cuit.yingpingsxitong")
@EnableAspectJAutoProxy(proxyTargetClass = true)

public class YingpingsxitongApplication {

	public static void main(String[] args) {
		SpringApplication.run(YingpingsxitongApplication.class, args);
	}

}
