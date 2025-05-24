package com.lin.sl6512014nettyclient.netty;

import com.lin.sl6512014nettyclient.utils.HexUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {
    private final String host;
    private final int port;

    private ChannelFuture f;

    public NettyClient(String host , int port) {
        this.host = "127.0.0.1";
        this.port = 9000;
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); // 客户端辅助启动类
            b.group(group)
              .channel(NioSocketChannel.class) // 指定使用 NioSocketChannel 类来实例化通道对象
              .handler(new ChannelInitializer<SocketChannel>() {
                  @Override
                  public void initChannel(SocketChannel ch) throws Exception {
                      // 添加编解码器
                      ch.pipeline().addLast(new StringDecoder());
                      ch.pipeline().addLast(new StringEncoder());
                      // 添加自定义业务处理类
                      ch.pipeline().addLast(new NettyClientHandler());
                  }
              });

            // 启动客户端去连接服务器
            f = b.connect(host, port).sync();
            System.out.println("Connected to " + host + ":" + port);

            byte[] testMessage = HexUtil.hexStringToByteArray("7E7E010012345678123430002B020003591011154947F1F1001234567848F0F0591011154920190000052619000005392300000127381211150320FA");

            ByteBuf buffer = Unpooled.wrappedBuffer(testMessage);
            f.channel().writeAndFlush(buffer).addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("✅ 发送成功");
                } else {
                    System.out.println("❌ 发送失败：" + future.cause().getMessage());
                }
            }).sync();


            // 等待连接关闭
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }




}