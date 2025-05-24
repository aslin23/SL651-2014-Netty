package com.lin.demo_im.codec.decoder;

import com.lin.demo_im.codec.IFuncCodeProvider;
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
            byte[] functionCode =new byte[1];
            byteBuf.readBytes(functionCode);

            //报文上行标识以及长度
            byte[] MessageStartCharacter = new byte[2];
            byteBuf.readBytes(MessageStartCharacter);

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
                .funcCode(functionCode[0])  // 功能码
                .isM3Mode(isM3Mode)  // 报文上行标识
                .packetStartCode(MessageStartCharacter[0] & 0xFF)  // 报文起始符
                .build();

            TestReport testReport =null;
            //开始处理报文的正文 （测试报）
            if ((functionCode[0] & 0xFF) == 0x30) {
                log.info("收到测试报，功能码: 0x30");
                // 处理测试报的逻辑

                // 1. 流水号
                byte[] serialNo= new byte[2];
                byteBuf.readBytes(serialNo);

                //2. 发报时间
                byte[] sendTime = new byte[6];
                byteBuf.readBytes(sendTime);

                //遥测站地址标识符
                byte[] telemetryA = new byte[2];
                byteBuf.readBytes(telemetryA);

                //遥测站地址
                byte[] telemetryAddr = new byte[5];
                byteBuf.readBytes(telemetryAddr);

                //遥测站分类码
                byte[] telemetrType = new byte[1];
                byteBuf.readBytes(telemetrType);

                //观测时间
                byte[] observeTime = new byte[7];
                byteBuf.readBytes(observeTime);

                //降水量
                //1获取 降水量标识符
                byte precpiti= byteBuf.readByte();

                //2对于的降水量类型（例如0x20）是当前降水量
                byte [] precpitiAmount= new byte[4];
                byteBuf.readBytes(precpitiAmount);

                //1降水量累计标识
                byte totalPrecpiti= byteBuf.readByte();

                // 2降水量累计标识对于类型 降水量累计值

                byte[] totalPrecpitiAmount= new byte[4];
                byteBuf.readBytes(totalPrecpitiAmount);

                //瞬时水位
                //1.标识符
                 byte[] waterLevel= new byte[1];
                 byteBuf.readBytes(waterLevel);

                 //对于的水位类型 39
                byte[] waterLevelAmount= new byte[5];
                byteBuf.readBytes(waterLevelAmount);

                //其他要素


                //电压
                //电压标识符
                byte[] voltage= new byte[1];
                byteBuf.readBytes(voltage);
                //电压数据
                byte[] voltageAmount= new byte[3];

                //电压值
                byteBuf.readBytes(voltageAmount);



                // 创建测试报实体
                testReport = TestReport.builder()
                        .serialNo(serialNo)  // 流水号
                        .sendTime(sendTime)  // 发报时间
                        .telemetryAddr(telemetryAddr)  // 遥测站地址
                        .telemetryType(telemetrType)  // 遥测站分类码
                        .observeTime(observeTime)  // 观测时间
                        .precipitationFlag(precpiti)  // 降水量标识符
                        .precipitationAmount(precpitiAmount)  // 当前降水量
                        .totalPrecipitationFlag(totalPrecpiti)  // 降水量累计标识
                        .totalPrecipitationAmount(totalPrecpitiAmount)  // 降水量累计值
                        .waterLevelFlag(waterLevel)  // 水位标识符
                        .waterLevelAmount(waterLevelAmount)  // 水位值
                        .voltageFlag(voltage)  // 电压标识符
                        .voltageAmount(voltageAmount)  // 电压值
                        .build();







            } else {
                log.info("收到其他类型报文，功能码: 0x{}", String.format("%02X", functionCode[0] & 0xFF));
                // 处理其他类型报文的逻辑
            }






            //对其在封装

//            //报文结束符
//            byte endFlag = byteBuf.readByte();
//            log.info("结束符: 0x{}", String.format("%02X", endFlag & 0xFF));
//
//            //校验码
//            byte[] verifyCode = new byte[2];
//            byteBuf.readBytes(verifyCode);
//
//            log.info("校验码: 0x{}", bytesToHex(verifyCode));



            //将报文其他的结构添加到任务链条中
//            list.add(messageHeader);
            // 将测试报实体添加到输出列表
            list.add(testReport);

            ReferenceCountUtil.release(byteBuf);
            ReferenceCountUtil.release(in);


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


//    private void attachChannel(ChannelHandlerContext ctx, String deviceId) {
//
//        Attribute<String> attr = ctx.channel().attr(TcpSessionManager.CHANNEL_GROUP);
//        var oldDeviceId = attr.setIfAbsent(deviceId);
//        TcpSessionManager.TCP_CHANNEL_GROUP.add(ctx.channel());
//
//        if (replaceIdleHandler && oldDeviceId == null) {
//            // first receive data
//            ctx.pipeline().replace(
//                    HexSL651TcpServer.INIT_IDLE_HANDLER_NAME,
//                    HexSL651TcpServer.IDLE_HANDLER_NAME,
//                    new IdleStateHandler(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds));
//        }
//
//        ChannelId currentId = ctx.channel().id();
//        ChannelId lastResultChannelId = TcpSessionManager.put(deviceId, currentId);
//        if (lastResultChannelId != null && currentId != lastResultChannelId) {
//            Channel findChannel = TcpSessionManager.TCP_CHANNEL_GROUP.find(lastResultChannelId);
//            if (findChannel != null) {
//                logger.warn("ConnectionReset. deviceId = {}", deviceId);
//                findChannel.close();
//            }
//        }
//    }



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