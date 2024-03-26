package cn.hiles.proxy.api.async;

import cn.hiles.proxy.api.future.RpcFuture;

/**
 * @author Helios
 * Time：2024-03-26 19:57
 */
public interface IAsyncObjectProxy {
    /**
     * 异步代理对象调用方法
     *
     * @param funcName 方法名
     * @param args     参数
     * @return RpcFuture
     */
    RpcFuture call(String funcName, Object... args);
}
