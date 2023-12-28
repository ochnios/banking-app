package pl.ochnios.bankingbe.utils;

import java.util.UUID;

public class TraceGenerator {

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
