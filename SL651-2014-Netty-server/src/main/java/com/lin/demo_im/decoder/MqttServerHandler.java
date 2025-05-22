package com.lin.demo_im.decoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttServerHandler extends SimpleChannelInboundHandler<MqttMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage mqttMessage) throws Exception {
        // 解析MQQTT 报文类型 从固定报头中的控制码中 对应出 报文的类型
        MqttMessageType messageType = mqttMessage.fixedHeader().messageType();
        log.info("收到MQTT消息类型{}", messageType);
        
        switch (messageType) {
            case CONNECT:
                hanldeConnect(ctx,(MqttConnectMessage) mqttMessage);
                break;
            case PUBLISH:
                handlePublish(ctx, (MqttPublishMessage) mqttMessage);
                break;
            case SUBSCRIBE:
                handleSubscribe(ctx, (MqttSubscribeMessage) mqttMessage);
                break;
            case PINGREQ:
                handlePing(ctx);
                break;
            default:
                System.out.println("未处理的 MQTT 报文类型: " + messageType);


        }
    }

    private void hanldeConnect(ChannelHandlerContext ctx, MqttConnectMessage mqttConnectMessage) {
        //打印客户端标识
        log.info("MQTT 客户端连接：ClientID={}", mqttConnectMessage.payload().clientIdentifier());
        MqttConnAckMessage connAckMessage= MqttMessageBuilders.connAck()
                .returnCode(MqttConnectReturnCode.CONNECTION_ACCEPTED)
                .build();
        ctx.writeAndFlush(connAckMessage);
    }

    //处理 PUBLISH 消息
    private void handlePublish(ChannelHandlerContext ctx, MqttPublishMessage msg) {
        // 主题名 用户建立消息订阅 来通讯
        String topic =msg.variableHeader().topicName();
        String payload =msg.payload().toString(io.netty.util.CharsetUtil.UTF_8);
        log.info("收到 PUBLISH 消息: Topic=" + topic + ", Payload=" + payload);
    }

    //处理订阅要求
    private void handleSubscribe(ChannelHandlerContext ctx, MqttSubscribeMessage msg) {
        log.info("接收到订阅请求："+msg.payload().topicSubscriptions());
    }

    private void handlePing(ChannelHandlerContext ctx){
        log.info("接收到 PINGREQ");
        ctx.writeAndFlush(MqttMessage.PINGRESP);
    }
}
