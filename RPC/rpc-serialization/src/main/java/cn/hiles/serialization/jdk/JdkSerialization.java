package cn.hiles.serialization.jdk;

import cn.hiles.common.exception.SerializerException;
import cn.hiles.serialization.api.Serialization;

import java.io.*;

/**
 * @author Helios
 * Time：2024-03-12 20:22
 */
public class JdkSerialization implements Serialization {
    @Override
    public <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new SerializerException("serialize object is null");
        }
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(os);
            out.writeObject(obj);
            return os.toByteArray();
        } catch (IOException e) {
            throw new SerializerException("jdk serialize error" + e.getMessage());
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) {
        if (data == null) {
            throw new SerializerException("deserialize data is null");
        }
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            ObjectInputStream in = new ObjectInputStream(is);
            return (T) in.readObject();
        } catch (Exception e) {
            throw new SerializerException("jdk deserialize error" + e.getMessage());
        }
    }
}
