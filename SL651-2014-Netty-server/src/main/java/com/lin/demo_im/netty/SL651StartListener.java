package com.lin.demo_im.netty;

import com.lin.demo_im.netty.server.ImServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SL651StartListener implements ApplicationRunner {
    @Resource
    private ImServer socketServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.socketServer.run();
    }
}