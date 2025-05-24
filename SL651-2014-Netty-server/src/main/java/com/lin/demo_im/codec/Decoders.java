package com.lin.demo_im.codec;

import com.lin.demo_im.codec.decoder.SL651Decoder;
import com.lin.demo_im.codec.decoder.TelemetryLinkMaintenanceDecoder;
import com.lin.demo_im.codec.decoder.TestFunDecoder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class Decoders {

    private static final Map<Integer, IFuncDecoder<?>> DECODER_MAP = new ConcurrentHashMap<>();

    static {
        autoRegister(new TestFunDecoder());
        autoRegister(new TelemetryLinkMaintenanceDecoder());
    }


    public static IFuncDecoder<?> getDecoder(int funcCode) {
        return DECODER_MAP.get(funcCode);
    }

    private static void autoRegister(IFuncCodeProvider codec) {
        if (codec instanceof IFuncDecoder<?>) {
            IFuncDecoder<?> decoder = (IFuncDecoder<?>) codec;
            registerDecoder(decoder.getFuncCode(), decoder);
        }
    }

    private static void registerDecoder(int code, IFuncDecoder<?> decoder) {
        DECODER_MAP.put(code, decoder);
    }



}