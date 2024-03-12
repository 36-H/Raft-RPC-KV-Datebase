package cn.hiles.codec;

import cn.hiles.serialization.api.Serialization;
import cn.hiles.serialization.jdk.JdkSerialization;


/**
 * @author Helios
 * Time：2024-03-12 20:36
 */
public interface RpcCodec {
    default Serialization getJdkSerialization() {
        return new JdkSerialization();
    }
}
