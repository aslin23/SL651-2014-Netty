package com.lin.demo_im.domain.entity;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ResponseMessage<T> {
    private MessageHeader header;
    private T body;
    private int responsePacketEndCode;
}