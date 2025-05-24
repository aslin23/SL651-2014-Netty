package com.lin.demo_im.netty.handler;

import com.lin.demo_im.domain.entity.MessageHeader;
import com.lin.demo_im.domain.entity.TestReport;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class SL651OutboundHandler extends ChannelOutboundHandlerAdapter {



    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        log.info("发送给客户端");

        super.write(ctx, msg, promise);

    }
}