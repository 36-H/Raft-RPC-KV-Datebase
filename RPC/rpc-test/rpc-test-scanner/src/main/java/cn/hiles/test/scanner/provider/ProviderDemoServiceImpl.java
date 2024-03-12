package cn.hiles.test.scanner.provider;

import cn.hiles.annotation.RpcService;
import cn.hiles.test.scanner.service.DemoService;

/**
 * @author Helios
 * Time：2024-03-11 9:36
 */
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "cn.hiles.test.scanner.service.DemoService",
        version = "1.0.0", group = "test")
public class ProviderDemoServiceImpl implements DemoService {
}
