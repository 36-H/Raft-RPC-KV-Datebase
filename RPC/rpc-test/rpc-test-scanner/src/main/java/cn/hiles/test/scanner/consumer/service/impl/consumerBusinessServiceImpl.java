package cn.hiles.test.scanner.consumer.service.impl;

import cn.hiles.annotation.RpcReference;
import cn.hiles.test.scanner.consumer.service.ConsumerBusinessService;
import cn.hiles.test.scanner.service.DemoService;

/**
 * @author Helios
 * Time：2024-03-11 9:42
 */
public class consumerBusinessServiceImpl implements ConsumerBusinessService {
    @RpcReference(version = "1.0.0", group = "test")
    private DemoService demoService;

}
