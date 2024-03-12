package cn.hiles.test.provider.service.impl;

import cn.hiles.annotation.RpcService;
import cn.hiles.test.provider.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Helios
 * Time：2024-03-12 12:53
 */
@RpcService(
        interfaceClass = DemoService.class,
        interfaceClassName = "cn.hiles.test.provider.service.DemoService",
        version = "1.0.0",
        group = "test"
)
public class ProviderDemoServiceImpl implements DemoService {
    private final Logger logger = LoggerFactory.getLogger(ProviderDemoServiceImpl.class);

    @Override
    public String hello(String name) {
        logger.info("调用hello方法传入的参数为===>>>{}", name);
        return "hello " + name;
    }
}
