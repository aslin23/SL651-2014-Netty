package com.lin.demo_im.netty.Initializer;

import com.lin.demo_im.decoder.SL651Decoder;
import com.lin.demo_im.netty.handler.NettyServerHandler;
import com.lin.demo_im.netty.handler.SL651Handler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SL651Initializer  extends ChannelInitializer<SocketChannel> {



    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        log.info("Initializing channel");
        // 添加编解码器

        ch.pipeline().addLast(new SL651Decoder());
        log.info("添加成功  SL651Decoder");
        ch.pipeline().addLast(new SL651Handler());
        log.info("添加成功 SL651Handler");
        ch.pipeline().addLast(new StringEncoder());

    }
}
