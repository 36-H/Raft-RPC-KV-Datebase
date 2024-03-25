package cn.hiles.constants;

/**
 * @author Helios
 * Time：2024-03-12 16:32
 */
public class RpcConstants {
    /**
     * 魔数
     */
    public static final short MAGIC = 0x10;
    /**
     * 消息头长度 32个字节
     */
    public static final int HEADER_TOTAL_LENGTH = 32;
    /**
     * 采用JDK动态代理方式进行服务提供者的服务调用
     */
    public static final String REFLECT_TYPE_JDK = "jdk";

    /**
     * 采用CGLIB动态代理方式进行服务提供者的服务调用
     */
    public static final String REFLECT_TYPE_CGLIB = "cglib";
}
