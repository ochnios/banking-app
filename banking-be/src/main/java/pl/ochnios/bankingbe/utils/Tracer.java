package pl.ochnios.bankingbe.utils;

import java.util.UUID;

public class Tracer {

    public static String simpleTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
