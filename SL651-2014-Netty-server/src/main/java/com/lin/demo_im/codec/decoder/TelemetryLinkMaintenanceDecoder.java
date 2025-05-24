package com.lin.demo_im.codec.decoder;


import com.lin.demo_im.codec.IFuncCodeProvider;
import com.lin.demo_im.domain.enums.FunctionCode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TelemetryLinkMaintenanceDecoder extends ByteToMessageDecoder implements IFuncCodeProvider {
    @Override
    public int getFuncCode() {
        return FunctionCode.TELEMETRY_LINK_MAINTENANCE_CODE.getCode();
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

    }
}
