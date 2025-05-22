package com.lin.demo_im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.lin.demo_im.netty")
public class DemoImApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoImApplication.class, args);
    }

}
