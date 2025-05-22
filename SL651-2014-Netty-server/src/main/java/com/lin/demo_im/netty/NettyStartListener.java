package com.lin.demo_im.netty;

import com.lin.demo_im.netty.server.ImServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 监听Spring容器启动完成，完成后启动Netty服务器
 * @author Gjing
 **/

public class NettyStartListener  {

    private ImServer socketServer;

//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        this.socketServer.run();
//    }
}