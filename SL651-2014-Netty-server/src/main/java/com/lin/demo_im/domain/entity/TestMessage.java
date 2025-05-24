package com.lin.demo_im.domain.entity;

import com.lin.demo_im.codec.IFuncCodeProvider;
import com.lin.demo_im.domain.enums.FunctionCode;
import com.lin.demo_im.message.IFuncMessage;

public class TestMessage extends BaseMessage implements IFuncCodeProvider {


    @Override
    public int getFuncCode() {
        return FunctionCode.TEST_FUNCTION_CODE.getCode();
    }
}
