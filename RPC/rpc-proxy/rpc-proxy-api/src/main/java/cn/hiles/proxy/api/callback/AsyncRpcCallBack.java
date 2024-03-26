package cn.hiles.proxy.api.callback;

/**
 * @author Helios
 * Time：2024-03-26 10:26
 */
public interface AsyncRpcCallBack {
    /**
     * 成功回调
     *
     * @param result 结果
     */
    void onSuccess(Object result);

    /**
     * 失败回调
     *
     * @param throwable 异常
     */
    void onException(Throwable throwable);
}
