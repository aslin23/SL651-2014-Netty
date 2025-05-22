package com.lin.demo_im.codec;


import com.lin.demo_im.domain.MqttData;
import io.netty.handler.codec.mqtt.MqttMessage;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MqttMessageEncoder {

    public  byte[] encodeMqttMessage(MqttData mqttData, MqttMessage msg) {

        byte[] topicBytes = mqttData.getTopic().getBytes(StandardCharsets.UTF_8);

        byte[] payload = mqttData.getPayload();


        // 如果 QoS > 0，报文标识符需要额外 2 字节

        //计算剩余长度
        int remainingLength = 2+topicBytes.length+payload.length;

        if (mqttData.getFlags() > 0) {
            remainingLength += 2;
        }
        //创建 ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(2+remainingLength);

        // 1.写入固定报头
        buffer.put((byte) (((mqttData.getMessageType() << 4)) | mqttData.getFlags()));
        buffer.put(mqttData.getPayload());
        buffer.put((byte) remainingLength);

        //2. 写入可变报头
        buffer.putShort((short) topicBytes.length);
        buffer.put(topicBytes);

        // 3. QoS 1 / QoS 2 需要报文标识符
        if (mqttData.getFlags() > 0) {
            buffer.putShort((short) mqttData.getPacketId());
        }


        //3.写入有效载荷
        buffer.put(payload);

        return buffer.array();
     }
}
