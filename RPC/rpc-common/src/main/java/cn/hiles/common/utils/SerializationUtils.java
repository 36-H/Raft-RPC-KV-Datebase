package cn.hiles.common.utils;

import java.util.stream.IntStream;

/**
 * @author Helios
 * Time：2024-03-12 17:00
 */
public class SerializationUtils {
    /**
     * 约定序列化类型最大长度为16
     */
    public static final int MAX_SERIALIZATION_TYPE_LENGTH = 16;
    private static final String PADDING_STRING = "0";

    /**
     * 为长度不足的序列化类型补0
     *
     * @param str 原始序列化类型字符串
     * @return 补0后的序列化类型字符串
     */
    public static String paddingString(String str) {
        str = transNullToEmpty(str);
        if (str.length() >= MAX_SERIALIZATION_TYPE_LENGTH) {
            return str;
        }
        int paddingLength = MAX_SERIALIZATION_TYPE_LENGTH - str.length();
        StringBuilder stringBuilder = new StringBuilder(str);
        IntStream.range(0, paddingLength).forEach(i -> stringBuilder.append(PADDING_STRING));
        return stringBuilder.toString();
    }

    /**
     * 字符串去0
     *
     * @param str 原始序列化类型字符串
     * @return 去0后的序列化类型字符串
     */
    public static String trimString(String str) {
        str = transNullToEmpty(str);
        return str.replaceAll(PADDING_STRING, "");
    }

    public static String transNullToEmpty(String str) {
        return str == null ? "" : str;
    }

}
