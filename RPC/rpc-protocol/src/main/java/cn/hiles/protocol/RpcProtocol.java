package cn.hiles.protocol;

import cn.hiles.protocol.head.RpcHeader;

import java.io.Serializable;

/**
 * @author Helios
 * Time：2024-03-12 16:43
 */
public class RpcProtocol<T> implements Serializable {
    private static final long serialVersionUID = 12565552681L;
    private RpcHeader rpcHeader;
    private T body;

    public RpcProtocol(RpcHeader rpcHeader, T body) {
        this.rpcHeader = rpcHeader;
        this.body = body;
    }

    public RpcHeader getRpcHeader() {
        return rpcHeader;
    }

    public void setRpcHeader(RpcHeader rpcHeader) {
        this.rpcHeader = rpcHeader;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
