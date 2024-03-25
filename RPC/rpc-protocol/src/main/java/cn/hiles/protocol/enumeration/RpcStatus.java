package cn.hiles.protocol.enumeration;

/**
 * RPC调用状态
 *
 * @author Helios
 * Time：2024-03-25 9:35
 */
public enum RpcStatus {
    SUCCESS(0),
    FAIL(1);
    private final int code;

    RpcStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
