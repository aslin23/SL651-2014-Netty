package com.lin.demo_im.netty.Initializer;

import com.lin.demo_im.codec.decoder.SL651Decoder;
import com.lin.demo_im.netty.handler.SL651InboundHandler;
import com.lin.demo_im.netty.handler.SL651OutboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class SL651Initializer  extends ChannelInitializer<SocketChannel> {

    public static final String INIT_IDLE_HANDLER_NAME = "InitIdleStateHandler";
    public static final String IDLE_HANDLER_NAME = "IdleStateHandler";


    /**
     * 这两个配置作用: enableConnectNoDataTimeout = true: 当设备连接上来后, connectNoDataTimeoutSeconds(10) 秒内没有数据就断开连接
     * 有一些非设备TCP连接上来, 避免占用服务器太久.
     */
    @Setter
    protected boolean enableConnectNoDataTimeout = false;
    @Setter
    protected int connectNoDataTimeoutSeconds = 10;


    /**
     * 当 enableConnectNoDataTimeout = true 时, 会在第一次数据到太时替换 IDLE 事件.避免还会关闭连接
     */
    @Setter
    protected int readerIdleTimeSeconds = 0;
    @Setter
    protected int writerIdleTimeSeconds = 0;
    @Setter
    protected int allIdleTimeSeconds = 180;


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        log.info("Initializing channel");

        ChannelPipeline pipeline = ch.pipeline();
        //协议定义为最大为 4212
        pipeline.addLast(new LengthFieldBasedFrameDecoder(8192, 11, 2, 3 + 1, 0));

        if (enableConnectNoDataTimeout) {
            //空闲设置
            pipeline.addLast(INIT_IDLE_HANDLER_NAME, new IdleStateHandler(0, 0, connectNoDataTimeoutSeconds));
        } else {
            pipeline.addLast(IDLE_HANDLER_NAME, new IdleStateHandler(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds));
        }


        pipeline.addLast(new StringEncoder());

        // 添加您的自定义出站处理器
        pipeline.addLast(new SL651OutboundHandler());


        // 添加编解码器

        pipeline.addLast(new SL651Decoder());

        pipeline.addLast(new SL651InboundHandler());
    }
}
