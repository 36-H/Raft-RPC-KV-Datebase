package cn.hiles.consumer.common.future;


import cn.hiles.protocol.RpcProtocol;
import cn.hiles.protocol.request.RpcRequest;
import cn.hiles.protocol.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author Helios
 * Time：2024-03-25 19:02
 */
public class RpcFuture extends CompletableFuture<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RpcFuture.class);
    /**
     * 同步锁
     */
    private Sync sync;
    /**
     * 请求协议
     */
    private RpcProtocol<RpcRequest> requestRpcProtocol;
    /**
     * 响应协议
     */
    private RpcProtocol<RpcResponse> responseRpcProtocol;
    /**
     * 请求开始时间
     */
    private long startTime;
    /**
     * 默认超时时间
     */
    private long responseTimeThreshold = 5000;

    public RpcFuture(RpcProtocol<RpcRequest> requestRpcProtocol) {
        this.sync = new Sync();
        this.requestRpcProtocol = requestRpcProtocol;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() {
        sync.acquire(-1);
        if (this.responseRpcProtocol != null) {
            return this.responseRpcProtocol.getBody().getResult();
        } else {
            return null;
        }
    }

    /**
     * 超时阻塞获取结果
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the computed result
     */
    @Override
    public Object get(long timeout, TimeUnit unit) {
        try {
            boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
            if (success) {
                if (this.responseRpcProtocol != null) {
                    return this.responseRpcProtocol.getBody().getResult();
                } else {
                    return null;
                }
            } else {
                throw new RuntimeException("Timeout exception. Request id: " + this.requestRpcProtocol.getRpcHeader().getRequestId()
                        + ". Request class name: " + this.requestRpcProtocol.getBody().getClassName()
                        + ". Request method: " + this.requestRpcProtocol.getBody().getMethodName());
            }
        } catch (InterruptedException e) {
            logger.error("Get result error. Request id: " + this.requestRpcProtocol.getRpcHeader().getRequestId(), e);
            return null;
        }
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    /**
     * 当服务端返回结果时，调用此方法 将结果设置到future中 并唤醒等待线程
     *
     * @param responseRpcProtocol 响应协议
     */
    public void done(RpcProtocol<RpcResponse> responseRpcProtocol) {
        this.responseRpcProtocol = responseRpcProtocol;
        sync.release(1);
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > responseTimeThreshold) {
            logger.warn("The response time is too slow. Request id: "
                    + responseRpcProtocol.getRpcHeader().getRequestId()
                    + ". Response Time: " + responseTime + "ms");
        }
    }

    static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 1L;
        /**
         * future 状态
         */
        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending) {
                return compareAndSetState(pending, done);
            }
            return false;
        }

        protected boolean isDone() {
            return getState() == done;
        }
    }
}
