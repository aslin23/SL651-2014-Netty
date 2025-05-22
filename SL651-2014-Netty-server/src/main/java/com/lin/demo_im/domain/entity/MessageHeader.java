package com.lin.demo_im.domain.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageHeader {
    private byte[] startFrame;
    private int stationId;
    private String telemetryId;
    private String password;
    private int funcCode;
    private boolean isM3Mode;
    private int packetStartCode;
    private int packetEndCode;
}
