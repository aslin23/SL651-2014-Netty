package com.lin.demo_im.message;

import com.lin.demo_im.codec.IFuncCodeProvider;
import com.lin.demo_im.domain.entity.BaseMessage;
import com.lin.demo_im.domain.entity.TestMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class FuncMessageBuilder{


    private static final Map<Integer, IFuncMessage<?>> FUNCMESSAGE_MAP = new ConcurrentHashMap<>();

    static {
        autoRegister(new TestMessage());
    }


    public static IFuncMessage<?> getFuncResponseMessage(int funcCode) {
        return FUNCMESSAGE_MAP.get(funcCode);
    }

    private static void autoRegister(IFuncCodeProvider codec) {
        if (codec instanceof IFuncMessage<?>) {
            IFuncMessage<?> message = (IFuncMessage<?>) codec;
            registerDecoder(message.getFuncCode(), message);
        }
    }

    private static void registerDecoder(int code, IFuncMessage<?> decoder) {
        FUNCMESSAGE_MAP.put(code, decoder);
    }

}
