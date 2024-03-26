package cn.hiles.consumer.common.context;

import cn.hiles.proxy.api.future.RpcFuture;

/**
 * @author Helios
 * Time：2024-03-25 20:23
 */
public class RpcContext {
    /**
     * RpcContext单例
     */
    private static final RpcContext AGENT = new RpcContext();
    /**
     * 存放RpcFuture的ThreadLocal
     * InheritableThreadLocal可以在子线程中获取父线程的值
     */
    private static final InheritableThreadLocal<RpcFuture> RPC_FUTURE_INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();

    private RpcContext() {
    }

    /**
     * 获取上下文
     */
    public static RpcContext getContext() {
        return AGENT;
    }

    /**
     * 从ThreadLocal中获取RpcFuture
     */
    public RpcFuture getRpcFuture() {
        return RPC_FUTURE_INHERITABLE_THREAD_LOCAL.get();
    }

    /**
     * 将RpcFuture放入ThreadLocal
     */
    public void setRpcFuture(RpcFuture rpcFuture) {
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.set(rpcFuture);
    }

    /**
     * 移除ThreadLocal中的RpcFuture
     */
    public void removeRpcFuture() {
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.remove();
    }
}
