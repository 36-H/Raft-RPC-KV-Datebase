package cn.hiles.test.provider.service.impl;

import cn.hiles.annotation.RpcService;
import cn.hiles.test.provider.service.DemoService;

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
}
