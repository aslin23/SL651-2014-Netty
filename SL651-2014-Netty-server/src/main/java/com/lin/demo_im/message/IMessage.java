package com.lin.demo_im.message;

import com.lin.demo_im.domain.entity.MessageHeader;
import com.lin.demo_im.domain.entity.ResponseMessage;
import com.lin.demo_im.domain.enums.MessageType;

public interface IMessage<T> {


    public  ResponseMessage<?> makeResponseByObj(MessageHeader header, Object object);

    ResponseMessage<?> makeResponse(MessageHeader header, T message);

    default MessageType messageType() {
        return MessageType.REPORT;
    }
}
