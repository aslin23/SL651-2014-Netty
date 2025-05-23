package com.lin.demo_im.netty.Initializer;

import com.lin.demo_im.codec.decoder.SL651Decoder;
import com.lin.demo_im.netty.handler.SL651Handler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SL651Initializer  extends ChannelInitializer<SocketChannel> {



    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        log.info("Initializing channel");


//        //协议定义为最大为 4212
//        pipeline.addLast(new LengthFieldBasedFrameDecoder(8192, 11, 2, 3 + 1, 0));
//
//        if (enableConnectNoDataTimeout) {
//            //空闲设置
//            pipeline.addLast(INIT_IDLE_HANDLER_NAME, new IdleStateHandler(0, 0, connectNoDataTimeoutSeconds));
//        } else {
//            pipeline.addLast(IDLE_HANDLER_NAME, new IdleStateHandler(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds));
//        }
        // 添加编解码器

        log.info("SL651Decoder");
        ch.pipeline().addLast(new SL651Decoder());
        log.info("添加成功  SL651Decoder");
        ch.pipeline().addLast(new SL651Handler());
        log.info("添加成功 SL651Handler");
        ch.pipeline().addLast(new StringEncoder());

    }
}
