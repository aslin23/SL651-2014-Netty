package com.lin.demo_im.domain.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestReport {
    private byte[] serialNo;           // 流水号
    private byte[] sendTime;         // 发报时间
    private byte[] telemetryAddr;    // 遥测站地址
    private byte[] telemetryType;    // 遥测站分类码
    private byte[] observeTime;      // 观测时间
    
    // 降水量相关
    private byte precipitationFlag;  // 降水量标识符
    private byte[] precipitationAmount; // 当前降水量
    private byte totalPrecipitationFlag; // 降水量累计标识
    private byte[] totalPrecipitationAmount; // 降水量累计值
    
    // 水位相关
    private byte[] waterLevelFlag;     // 水位标识符
    private byte[] waterLevelAmount; // 水位值
    
    // 电压相关
    private byte[] voltageFlag;        // 电压标识符
    private byte[] voltageAmount;    // 电压值

    @Override
    public String toString() {
        return String.format(
            "TestReport{" +
            "serialNo=%s, " +
            "sendTime=%s, " +
            "telemetryAddr=%s, " +
            "telemetryType=%s, " +
            "observeTime=%s, " +
            "precipitationFlag=0x%02X, " +
            "precipitationAmount=%s, " +
            "totalPrecipitationFlag=0x%02X, " +
            "totalPrecipitationAmount=%s, " +
            "waterLevelFlag=%s, " +
            "waterLevelAmount=%s, " +
            "voltageFlag=%s, " +
            "voltageAmount=%s" +
            "}", bytesToHex(serialNo),

            bytesToHex(sendTime),
            bytesToHex(telemetryAddr),
            bytesToHex(telemetryType),
            bytesToHex(observeTime),
            precipitationFlag,
            bytesToHex(precipitationAmount),
            totalPrecipitationFlag,
            bytesToHex(totalPrecipitationAmount),
                bytesToHex(waterLevelFlag),
            bytesToHex(waterLevelAmount),
                bytesToHex(voltageFlag),
            bytesToHex(voltageAmount)
        );
    }

    private String bytesToHex(byte[] bytes) {
        if (bytes == null) return "null";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b & 0xFF));
        }
        return sb.toString();
    }
}