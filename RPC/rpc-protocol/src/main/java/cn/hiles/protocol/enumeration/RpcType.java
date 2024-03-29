package cn.hiles.protocol.enumeration;

/**
 * @author Helios
 * Time：2024-03-12 16:08
 */
public enum RpcType {
    /**
     * 请求
     */
    REQUEST(1),
    /**
     * 响应
     */
    RESPONSE(2),
    /**
     * 心跳
     */
    HEARTBEAT(3);

    private final int type;

    RpcType(int type) {
        this.type = type;
    }

    public static RpcType findByCode(byte msgType) {
        for (RpcType value : RpcType.values()) {
            if (value.type == msgType) {
                return value;
            }
        }
        return null;
    }

    public int getType() {
        return type;
    }
}
