package cn.hiles.protocol.response;

import cn.hiles.protocol.base.RpcMessage;

/**
 * @author Helios
 * Time：2024-03-12 16:20
 */
public class RpcResponse extends RpcMessage {
    private static final long serialVersionUID = 4423943943943943943L;
    private String error;
    private Object result;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
