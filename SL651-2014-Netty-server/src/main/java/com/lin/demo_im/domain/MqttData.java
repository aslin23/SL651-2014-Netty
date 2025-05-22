package com.lin.demo_im.domain;

public class MqttData {
    //Mqtt 控制报文类型
    private int messageType;
    //标志位
    private Integer flags;

    //剩余长度
    private int remainingLength;
    //主题（PUBLISH）报文
    private String topic;
    //报文标识符
    private int packetId;
    //有效载荷（消息内容）
    private byte[] payload;

    // 构造方法
    public MqttData(int messageType, Integer flags, int remainingLength,
                       String topic, int packetId, byte[] payload) {
        this.messageType = messageType;
        this.flags = flags;
        this.remainingLength = remainingLength;
        this.topic = topic;
        this.packetId = packetId;
        this.payload = payload;
    }

    // Getter 和 Setter
    public int getMessageType() { return messageType; }
    public void setMessageType(int messageType) { this.messageType = messageType; }

    public int getFlags() { return flags; }
    public void setFlags(Integer flags) { this.flags = flags; }

    public int getRemainingLength() { return remainingLength; }
    public void setRemainingLength(int remainingLength) { this.remainingLength = remainingLength; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public int getPacketId() { return packetId; }
    public void setPacketId(int packetId) { this.packetId = packetId; }

    public byte[] getPayload() { return payload; }
    public void setPayload(byte[] payload) { this.payload = payload; }

    // 转换为字符串（调试用）
    @Override
    public String toString() {
        return "MqttMessage{" +
                "messageType=" + messageType +
                ", flags=" + flags +
                ", remainingLength=" + remainingLength +
                ", topic='" + topic + '\'' +
                ", packetId=" + packetId +
                ", payload=" + new String(payload) +
                '}';
    }
}

