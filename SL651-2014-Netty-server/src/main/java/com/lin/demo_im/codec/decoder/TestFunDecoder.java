package com.lin.demo_im.codec.decoder;

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
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class TestFunDecoder implements IFuncDecoder {

    @Override
    public TestReport decode(ByteBuf byteBuf)  {

        try {

            TestReport testReport =null;
            //开始处理报文的正文 （测试报）

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



            ReferenceCountUtil.release(byteBuf);

            return testReport;

        }  catch (Exception e) {
            log.error("解码过程中发生异常: ", e);
            byteBuf.resetReaderIndex(); // 重置读指针
            throw e; // 重新抛出异常，让上层处理器处理
        }
    }




    @Override
    public int getFuncCode() {
        return FunctionCode.TEST_FUNCTION_CODE.getCode();
    }


}