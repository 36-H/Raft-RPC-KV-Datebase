package cn.hiles.proxy.api.consumer;

import cn.hiles.protocol.RpcProtocol;
import cn.hiles.protocol.request.RpcRequest;
import cn.hiles.proxy.api.future.RpcFuture;

/**
 * @author Helios
 * Time：2024-03-26 11:21
 */
public interface Consumer {
    /**
     * 发送Request请求
     */
    RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol) throws Exception;
}
