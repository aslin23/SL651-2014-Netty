package com.lin.demo_im.netty.Initializer;

import com.lin.demo_im.netty.handler.NettyServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.stereotype.Component;

/**
 * Socket 初始化器，每一个Channel进来都会调用这里的 InitChannel 方法
 * @author Gjing
 **/
@Component
public class SocketInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) throws Exception {

        //其中ChannelPipeline 代表的对应channel的责任链，里面包含了很多的Handler
        // 添加编解码器
        ch.pipeline().addLast(new StringDecoder());
        ch.pipeline().addLast(new StringEncoder());
        // 添加自定义业务处理类
        ch.pipeline().addLast(new NettyServerHandler());
    }
}