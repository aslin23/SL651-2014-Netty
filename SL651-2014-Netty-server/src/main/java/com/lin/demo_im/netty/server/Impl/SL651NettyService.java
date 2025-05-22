package com.lin.demo_im.netty.server.Impl;

import com.lin.demo_im.netty.Initializer.SL651Initializer;
import com.lin.demo_im.netty.server.ImServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;


@Slf4j
@Component
public class SL651NettyService implements ImServer {
    /**
     * netty服务监听端口
     */
    @Value("${netty.port:8088}")
    private int port;
    /**
     * 主线程组数量
     */
    @Value("${netty.bossThread:1}")
    private int bossThread;


    @Resource
    private SL651Initializer sl651Initializer;


    private ServerBootstrap serverBootstrap;


    private NioEventLoopGroup bossGroup ;
    // 实际工作的线程组
    private NioEventLoopGroup workerGroup ;


    @Override
    public void run() throws Exception {
        this.init();
        this.serverBootstrap.bind(this.port);
        log.info("Netty started on port: {} (TCP) with boss thread {}", this.port, this.bossThread);


    }

    @PreDestroy
    @Override
    public void close() {
        log.info("关闭Netty");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    private void init() {
        // 创建两个线程组，bossGroup为接收请求的线程组，一般1-2个就行
        bossGroup = new NioEventLoopGroup(this.bossThread);
        // 实际工作的线程组
        workerGroup = new NioEventLoopGroup(2);
        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class) // 指定使用 NioServerSocketChannel 类来实例化通道对象
                .childHandler(sl651Initializer)
                // option用于NioserverSocketChannel他接受传入的连接
                .option(ChannelOption.SO_BACKLOG, 128)          // 服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝
                // 用于父ServerChannel接受的通道
                .childOption(ChannelOption.SO_KEEPALIVE, true); // 保持长连接

    }


}
