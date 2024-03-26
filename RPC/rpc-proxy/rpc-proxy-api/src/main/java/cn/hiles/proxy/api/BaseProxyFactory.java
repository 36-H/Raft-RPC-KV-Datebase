package cn.hiles.proxy.api;

import cn.hiles.proxy.api.config.ProxyConfig;
import cn.hiles.proxy.api.object.ObjectProxy;

/**
 * 基础代理工厂
 *
 * @author Helios
 * Time：2024-03-26 21:15
 */
public abstract class BaseProxyFactory<T> implements ProxyFactory {
    /**
     * 代理配置
     */
    protected ObjectProxy<T> objectProxy;

    @Override
    public <T> void init(ProxyConfig<T> proxyConfig) {
        this.objectProxy = new ObjectProxy(
                proxyConfig.getClazz(),
                proxyConfig.getServiceVersion(),
                proxyConfig.getServiceGroup(),
                proxyConfig.getTimeout(),
                proxyConfig.getConsumer(),
                proxyConfig.getSerializationType(),
                proxyConfig.isAsync(),
                proxyConfig.isOneway()
        );
    }
}
