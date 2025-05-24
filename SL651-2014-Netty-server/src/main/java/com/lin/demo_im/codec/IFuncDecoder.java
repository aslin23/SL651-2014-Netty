package com.lin.demo_im.codec;

import com.lin.demo_im.domain.entity.MessageHeader;
import com.lin.demo_im.domain.entity.ResponseMessage;
import com.lin.demo_im.domain.enums.MessageType;
import io.netty.buffer.ByteBuf;

public interface IFuncDecoder<T> extends IFuncCodeProvider {


    public T decode(ByteBuf buf);

}