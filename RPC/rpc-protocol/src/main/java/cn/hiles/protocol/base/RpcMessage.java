package cn.hiles.protocol.base;

import java.io.Serializable;

/**
 * @author Helios
 * Time：2024-03-12 16:09
 */
public class RpcMessage implements Serializable {
    /**
     * 是否单向
     */
    private boolean oneWay;
    /**
     * 是否异步
     */
    private boolean async;

    public RpcMessage() {
    }

    public RpcMessage(boolean oneWay, boolean async) {
        this.oneWay = oneWay;
        this.async = async;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }
}
