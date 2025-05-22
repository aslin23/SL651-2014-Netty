package com.lin.demo_im.SLEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



public enum  SLEnum {


    //region   报文起始符

    //传输正文起始
    STX(0x02,"STX"),

    //多包传输正文起始
    SYN(0x16,"SYN"),

    //endregion


    //region 报文结束符


    // 作为报文结束符，表示传输完成，等待退出通信
    ETX(0x03,"ETX"),


    // 在报文分包传输时作为报文结束符，表示传输未完成，不可退出通信
    ETB(0x16,"ETB");




    private final int code;
    private final String desc;



    SLEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    // 根据 16 进制 code 获取枚举
    public static SLEnum fromCode(int code) {
        for (SLEnum status : SLEnum.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效状态码: 0x" + Integer.toHexString(code).toUpperCase());
    }
}
