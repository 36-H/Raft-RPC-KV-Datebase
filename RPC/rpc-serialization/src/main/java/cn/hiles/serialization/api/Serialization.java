package cn.hiles.serialization.api;

/**
 * @author Helios
 * Time：2024-03-12 17:14
 */
public interface Serialization {
    /**
     * 序列化
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     */
    <T> T deserialize(byte[] data, Class<T> clz);
}
