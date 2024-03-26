package cn.hiles.proxy.api;

import cn.hiles.proxy.api.config.ProxyConfig;

/**
 * @author Helios
 * Time：2024-03-26 21:13
 */
public interface ProxyFactory {
    /**
     * 创建代理对象
     *
     * @param clazz 接口类
     * @param <T>   泛型
     * @return 代理对象
     */
    <T> T getProxy(Class<T> clazz);

    /**
     * 默认初始化
     *
     * @param proxyConfig 代理配置
     */
    default <T> void init(ProxyConfig<T> proxyConfig) {
    }
}
