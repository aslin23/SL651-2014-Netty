package com.lin.demo_im.domain.entity;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ByteBufResponseMessage extends ResponseMessage<ByteBuf> {
}
