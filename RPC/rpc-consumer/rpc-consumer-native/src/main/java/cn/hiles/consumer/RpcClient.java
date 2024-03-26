package cn.hiles.consumer;

import cn.hiles.consumer.common.RpcConsumer;
import cn.hiles.proxy.api.async.IAsyncObjectProxy;
import cn.hiles.proxy.api.object.ObjectProxy;
import cn.hiles.proxy.jdk.JdkProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Helios
 * Time：2024-03-26 16:44
 */
public class RpcClient {
    private final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    /**
     * 服务版本
     */
    private String serviceVersion;
    /**
     * 服务分组
     */
    private String serviceGroup;
    /**
     * 序列化类型
     */
    private String serializationType;
    /**
     * 超时时间
     */
    private long timeout;

    /**
     * 是否异步调用
     */
    private boolean async;

    /**
     * 是否单向调用
     */
    private boolean oneway;

    public RpcClient(String serviceVersion, String serviceGroup, String serializationType, long timeout, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.serviceGroup = serviceGroup;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
    }

    /**
     * 创建代理对象
     * 屏蔽了底层的实现细节，只需要传入接口类即可
     *
     * @param interfaceClass 接口类
     * @param <T>            泛型
     * @return 代理对象
     */
    public <T> T create(Class<T> interfaceClass) {
        JdkProxyFactory<T> jdkProxyFactory = new JdkProxyFactory<T>(
                serviceVersion,
                serviceGroup,
                serializationType,
                timeout, RpcConsumer.getInstance(),
                async,
                oneway
        );
        return jdkProxyFactory.getProxy(interfaceClass);
    }

    public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(
                interfaceClass,
                serviceVersion,
                serviceGroup,
                timeout,
                RpcConsumer.getInstance(),
                serializationType,
                async,
                oneway
        );
    }

    /**
     * 用于关闭底层Netty服务
     */
    public void shutdown() {
        RpcConsumer.getInstance().close();
    }

}
