package com.lin.demo_im.netty.handler;

import com.lin.demo_im.domain.entity.TestReport;
import com.lin.demo_im.message.FuncMessageBuilder;
import com.lin.demo_im.message.IFuncMessage;
import com.lin.demo_im.utils.HexUtil;
import com.lin.demo_im.utils.ReflectUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class SL651InboundHandler extends ChannelInboundHandlerAdapter {

    public static final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);




    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {


        //开始构建下行报文 根据功能码构建下行报文
        //        log.info("解析数据：: {}", msg);
//        log.info("解析数据：: {}", msg.getClass());


        //将  Object 转化为确定的对象
        int getFuncCode = (int)ReflectUtils.callMethod(msg, "getFuncCode",new Class[]{});

        //根据功能构建对于的下行报文

        log.info("返回的功能码：: {}", getFuncCode);

        IFuncMessage<?> funcResponseMessage = FuncMessageBuilder.getFuncResponseMessage(getFuncCode);

        funcResponseMessage.response();


    }



    //处理异常的信息
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("11111"+cause.getMessage());
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
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE :
                    System.out.println("读空闲超时，关闭连接");
                    ctx.close(); // 客户端没发送心跳或断线
                break;
                case WRITER_IDLE :
                    System.out.println("写空闲超时，发送心跳包");
                    ctx.writeAndFlush(buildHeartbeatMsg()); // 主动发心跳
                break;
                case ALL_IDLE :
                    System.out.println("读写都空闲");
                break;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }


    private ByteBuf buildHeartbeatMsg() {
        return Unpooled.copiedBuffer("HEARTBEAT", CharsetUtil.UTF_8);
    }

    //移除通讯通道
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().id().asLongText() + "断开连接");
        clients.remove(ctx.channel());
    }

}
