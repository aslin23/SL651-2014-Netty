package com.lin.demo_im;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {
    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        // 配置服务端的 NIO 线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // 用来处理客户端连接请求
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 用来处理网络读写操作
        try {
            ServerBootstrap b = new ServerBootstrap(); // 启动 NIO 服务的辅助启动类
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 指定使用 NioServerSocketChannel 类来实例化通道对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {

                            //其中ChannelPipeline 代表的对应channel的责任链，里面包含了很多的Handler
                            // 添加编解码器
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            // 添加自定义业务处理类
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    })
                    // option用于NioserverSocketChannel他接受传入的连接
                    .option(ChannelOption.SO_BACKLOG, 128)          // 服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝
                    // 用于父ServerChannel接受的通道
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // 保持长连接

            // 绑定端口，开始接收进来的连接

            //ChannelFuture 相当于一个异步 需要等待异步操作完成并获取结果
            ChannelFuture f = b.bind(port).sync();

            // 等待服务器  socket 关闭 。
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new NettyServer(port).run();
    }
}