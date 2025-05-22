package com.lin.demo_im.decoder;

import com.lin.demo_im.domain.entity.MessageHeader;
import com.lin.demo_im.domain.entity.TestReport;
import com.lin.demo_im.domain.enums.ControlChar;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SL651Decoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {


        try {
            // 输出接收到的原始数据（十六进制格式）
            log.info("接收到的原始数据: {}", ByteBufUtil.hexDump(byteBuf));

            // 输出接收到的原始数据（字符串格式）
            log.info("接收到的原始数据(字符串): {}", byteBuf.toString(CharsetUtil.UTF_8));

            if (byteBuf.readableBytes() < 10) return; // 确保有足够的数据

            byteBuf.markReaderIndex(); //标记当前位置


            byte firstByte = byteBuf.readByte();
            byte secondByte = byteBuf.readByte();

            // 以十六进制字符串形式输出
            log.info("帧头字节: firstByte=0x{}, secondByte=0x{}",
                String.format("%02X", firstByte & 0xFF),
                String.format("%02X", secondByte & 0xFF));

            // 以字符形式输出
            log.info("帧头字符: firstByte='{}', secondByte='{}'",
                (char)(firstByte & 0xFF),
                (char)(secondByte & 0xFF));


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

            log.info("报文起始符：{}", String.format("0x%04X", packetStartCode));




            //计算出报文正文的长度
            // 获取 ByteBuf 可读字节数
            int length = byteBuf.readableBytes();
            log.info("数据的长度{}", length);

            // 计算消息体长度（总长度减去固定字段长度）
            int messageLength = length - 4; // 减去结束符(1字节)和校验码(2字节)的长度，预留1字节安全边界

            // 添加长度检查
            if (messageLength <= 0) {
                log.error("计算得到的消息长度无效: {}", messageLength);
                byteBuf.resetReaderIndex();
                return;
            }

            // 确保有足够的数据可读
            if (byteBuf.readableBytes() < messageLength) {
                log.error("可读数据长度不足，需要: {}, 实际: {}", messageLength, byteBuf.readableBytes());
                byteBuf.resetReaderIndex();
                return;
            }


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
                TestReport testReport = TestReport.builder()
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

                log.info("解析的测试报数据");
                log.info("解析的测试报数据: {}", testReport);

                // 将测试报实体添加到输出列表
                list.add(testReport);


            } else {
                log.info("收到其他类型报文，功能码: 0x{}", String.format("%02X", functionCode[0] & 0xFF));
                // 处理其他类型报文的逻辑
            }


            // // 读取消息体
            // byte[] messageText = new byte[messageLength];
            // byteBuf.readBytes(messageText);

            // // 解析数据
            // String hexData = bytesToHex(messageText);
            // log.info("消息体长度: {}, 内容: {}", messageLength, hexData);

            // 检查是否还有足够的数据读取结束符和校验码
            if (byteBuf.readableBytes() < 3) { // 1字节结束符 + 2字节校验码
                log.error("剩余数据不足以读取结束符和校验码");
                byteBuf.resetReaderIndex();
                return;
            }

            //报文结束符
            byte endFlag = byteBuf.readByte();
            log.info("结束符: 0x{}", String.format("%02X", endFlag & 0xFF));

            //校验码
            byte[] verifyCode = new byte[2];
            byteBuf.readBytes(verifyCode);
            log.info("校验码: 0x{}", bytesToHex(verifyCode));

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



}