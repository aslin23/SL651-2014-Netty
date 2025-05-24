package com.lin.demo_im.domain.enums;

public enum FunctionCode {

    TEST_FUNCTION_CODE("测试报功能码",0x30),
    TELEMETRY_LINK_MAINTENANCE_CODE("遥测站链路维持报",0x2F);



    private final int code;
    private final String desc;



    private FunctionCode(String desc,int code) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
