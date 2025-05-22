package com.lin.demo_im.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.ServerSocket;
import java.net.Socket;

@Component
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {
    public static final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    //接受和发送消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("收到客户端消息: " + msg);
        // 向客户端发送消息
        ctx.writeAndFlush("服务器已收到消息: " + msg);
    }

    //处理异常的信息
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
        ctx.channel().close();
        clients.remove(ctx.channel());
    }


    //当用户连接通道时
    /**
     * 监控浏览器上线
     *
     * @param ctx channel上下文
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().id().asLongText() + "连接");
        clients.add(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // 在规定时间内没有收到客户端的上行数据, 主动断开连接
                AttributeKey<Long> attr = AttributeKey.valueOf("USER_ID");
                Long userId = ctx.channel().attr(attr).get();
                log.info("心跳超时，即将断开连接,用户id:{}", userId);
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }

    //移除通讯通道
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        clients.remove(ctx.channel());
        new ServerSocket().accept();
    }




}