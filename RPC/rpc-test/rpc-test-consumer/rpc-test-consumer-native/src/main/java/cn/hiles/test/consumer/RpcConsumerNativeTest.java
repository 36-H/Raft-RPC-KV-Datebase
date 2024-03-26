package cn.hiles.test.consumer;

import cn.hiles.consumer.RpcClient;
import cn.hiles.proxy.api.async.IAsyncObjectProxy;
import cn.hiles.proxy.api.future.RpcFuture;
import cn.hiles.test.provider.service.DemoService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Helios
 * Time：2024-03-26 16:55
 */
public class RpcConsumerNativeTest {
    private static final Logger logger = LoggerFactory.getLogger(RpcConsumerNativeTest.class);

    public static void main(String[] args) {
        //客户端
        RpcClient rpcClient = new RpcClient("1.0.0", "test", "jdk", 3000, false, false);
        //远端服务
        DemoService helloService = rpcClient.create(DemoService.class);
        String result = helloService.hello("Helios");
        logger.info("result: {}", result);
        rpcClient.shutdown();
    }

    @Test
    public void testAsyncInterfaceRpc() throws Exception {
        RpcClient rpcClient = new RpcClient("1.0.0", "test", "jdk", 3000, false, false);
        IAsyncObjectProxy demoService = rpcClient.createAsync(DemoService.class);
        RpcFuture future = demoService.call("hello", "Helios");
        logger.info("result: {}", future.get());
        rpcClient.shutdown();
    }
}
