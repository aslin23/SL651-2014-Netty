package com.lin.demo_im.message;

import com.lin.demo_im.codec.IFuncCodeProvider;
import io.netty.buffer.ByteBuf;

public interface IFuncMessage<T> extends IFuncCodeProvider {

    public T response();
}
