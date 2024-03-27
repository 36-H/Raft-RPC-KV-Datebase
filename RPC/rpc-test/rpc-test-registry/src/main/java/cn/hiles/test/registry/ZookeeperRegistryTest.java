package cn.hiles.test.registry;

import cn.hiles.protocol.meta.ServiceMeta;
import cn.hiles.registry.api.RegistryService;
import cn.hiles.registry.api.config.RegistryConfig;
import cn.hiles.registry.zookeeper.ZookeeperRegistryService;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Helios
 * Time：2024-03-27 15:54
 */
public class ZookeeperRegistryTest {
    private RegistryService registryService;
    private ServiceMeta serviceMeta;

    @Before
    public void init() throws Exception {
        RegistryConfig registryConfig = new RegistryConfig(
                "5.34.222.105:2181",
                "zookeeper"
        );
        this.registryService = new ZookeeperRegistryService();
        this.registryService.init(registryConfig);
        this.serviceMeta = new ServiceMeta(
                ZookeeperRegistryTest.class.getName(),
                "1.0.0",
                "127.0.0.1",
                8080,
                "test"
        );
    }

    @Test
    public void testRegister() throws Exception {
        this.registryService.register(serviceMeta);
    }

    @Test
    public void testUnRegister() throws Exception {
        this.registryService.unRegister(serviceMeta);
    }

    @Test
    public void testDiscovery() throws Exception {
        this.registryService.discovery(RegistryService.class.getName(), "test".hashCode());
    }

    @Test
    public void testDestroy() throws Exception {
        this.registryService.destroy();
    }

}
