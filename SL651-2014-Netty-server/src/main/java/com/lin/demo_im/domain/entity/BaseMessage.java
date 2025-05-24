package com.lin.demo_im.domain.entity;

import com.lin.demo_im.message.IMessage;
import com.lin.demo_im.utils.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseMessage{

    private int serialNo;
    private String sendTime;


}