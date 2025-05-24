package com.lin.demo_im.codec.decoder;

import com.lin.demo_im.codec.Decoders;
import com.lin.demo_im.codec.IFuncCodeProvider;
import com.lin.demo_im.codec.IFuncDecoder;
import com.lin.demo_im.domain.entity.MessageHeader;
import com.lin.demo_im.domain.entity.TestReport;
import com.lin.demo_im.domain.enums.ControlChar;
import com.lin.demo_im.domain.enums.FunctionCode;
import com.lin.demo_im.utils.CrcUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SL651Decoder extends ByteToMessageDecoder implements IFuncCodeProvider  {


    /**
     * CRC出错时,是否要求重发
     * false: 表示CRC不正确,也会处理数据
     * true: CRC不正确,拒绝处理数据,回复NAK,要求客户端重新发送
     */
    private boolean crcErrorReSend = false;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        try {
            // 输出接收到的原始数据（十六进制格式）
            log.info("接收到的原始数据: {}", ByteBufUtil.hexDump(byteBuf));


            if (byteBuf.readableBytes() < 10) return; // 确保有足够的数据


            ByteBuf in =byteBuf;
            byteBuf = in.readBytes(in.readableBytes() - 2);
            int crc16 = in.readShort() & 0xFFFF;
            boolean correctCrc = crc16 == CrcUtil.calcCrc16(byteBuf);


            if (crcErrorReSend && !correctCrc) {
                // TODO reject message and response NAK

                log.info("校验数据，数据错误");
                return;
            }



            byteBuf.markReaderIndex(); //标记当前位置


            byte firstByte = byteBuf.readByte();
            byte secondByte = byteBuf.readByte();



            if (firstByte != 0x7E && secondByte != 0x7E) {
                System.out.println("无效的帧头: " + String.format("0x%04X-0x%04X", firstByte, secondByte));
                // 不是合法的 SL 651-2024 帧头，丢弃数据
                byteBuf.resetReaderIndex(); // 复位读取位置，丢弃无效数据
                return;
            }

            //读取中心站地址
            byte keyStation = byteBuf.readByte();

            if (keyStation < 0x01 || keyStation > 0xff) {
                System.out.println("无效的帧头: " + String.format("0x%04X-0x%04X", firstByte, secondByte));
                // 不是合法的 SL 651-2024 帧头，丢弃数据
                byteBuf.resetReaderIndex(); // 复位读取位置，丢弃无效数据
                return;
            }

            //读取遥测站
            //读取5个字节
            byte[] telemetric= new byte[5];
            byteBuf.readBytes(telemetric);

            byte telemetricType=telemetric[0];

            //
            byte watershed =telemetric[1];
            byte riverSystem =telemetric[2];
            byte stationType =telemetric[3];
            byte stationNum =telemetric[4];

            //密码为两字节
            byte[] password=new byte[2];
            byteBuf.readBytes(password);

            //功能码
            byte functionCode =byteBuf.readByte();


            //报文上行标识以及长度
            short dirFlagAndBodyLen = byteBuf.readShort();
            // 高 4 bit
            int dirFlag = (dirFlagAndBodyLen >> 12) & 0xF;   // 总是 0b0000
            int bodyLen = dirFlagAndBodyLen & 0xFFF;

            log.info("长度: {}", bodyLen);

            //报文起始符
            byte packetStartCode = byteBuf.readByte();



             boolean isM3Mode = isM3MultiPacket(packetStartCode);
             // 创建 MessageHeader 对象
                MessageHeader messageHeader = MessageHeader.builder()
                .startFrame(new byte[]{firstByte, secondByte})  // 帧头
                .stationId(keyStation & 0xFF)  // 中心站地址
                .telemetryId(String.format("%02X%02X%02X%02X%02X", 
                    telemetricType, watershed, riverSystem, stationType, stationNum))  // 遥测站ID
                .password(bytesToHex(password))  // 密码
                .funcCode(functionCode)  // 功能码
                .isM3Mode(isM3Mode)  // 报文上行标识
                .packetStartCode(packetStartCode & 0xFF)  // 报文起始符
                .build();


             //更具功能码获取对于的 解码器
            IFuncDecoder<?> decoder = Decoders.getDecoder(functionCode);
            ByteBuf bodyBuf = byteBuf.readBytes(bodyLen);

            //解析
            Object decode = decoder.decode(bodyBuf);
            list.add(decode);


        }  catch (Exception e) {
            log.error("解码过程中发生异常: ", e);
            byteBuf.resetReaderIndex(); // 重置读指针
            throw e; // 重新抛出异常，让上层处理器处理
        }
    }

    private String bytesToHex(byte[] bytes) {
        return ByteBufUtil.hexDump(bytes);  // 使用 Netty 内置的工具类
    }

    private boolean isM3MultiPacket(int startCode) {
        return startCode == ControlChar.SYN;
    }






    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("解码器发生异常: ", cause);
        ctx.close(); // 关闭连接
    }


    @Override
    public int getFuncCode() {
        return FunctionCode.TEST_FUNCTION_CODE.getCode();
    }
}