package com.isazariveralawyers.api.utils;

public final class PhoneUtils {
    private PhoneUtils() {}

    public static String toE164(String rawCoNumber) {
    // Simplista: limpiar y forzar indicativo +57
    String digits = rawCoNumber.replaceAll("\\D", "");
    if (digits.startsWith("57")) return "+" + digits;
    if (digits.startsWith("0")) digits = digits.substring(1);
    return "+57" + digits;
    }
}
