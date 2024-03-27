package cn.hiles.registry.api;

import cn.hiles.protocol.meta.ServiceMeta;
import cn.hiles.registry.api.config.RegistryConfig;

/**
 * @author Helios
 * Time：2024-03-27 15:15
 */
public interface RegistryService {
    /**
     * 服务注册
     *
     * @param serviceMeta 服务元数据
     * @throws Exception 抛出异常
     */
    void register(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务注销
     *
     * @param serviceMeta 服务元数据
     * @throws Exception 抛出异常
     */
    void unRegister(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务发现
     *
     * @param serviceName     服务名称
     * @param invokerHashCode 服务调用者的hashcode
     * @return 服务元数据
     * @throws Exception 抛出异常
     */
    ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception;

    /**
     * 服务销毁
     *
     * @throws Exception 抛出异常
     */
    void destroy() throws Exception;

    /**
     * 默认初始化
     *
     * @param registryConfig 注册中心配置
     * @throws Exception 抛出异常
     */
    default void init(RegistryConfig registryConfig) throws Exception {
    }

}
