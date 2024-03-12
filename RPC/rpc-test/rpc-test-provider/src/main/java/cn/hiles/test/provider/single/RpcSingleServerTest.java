package cn.hiles.test.provider.single;

import cn.hiles.provider.RpcSingleServer;
import org.junit.Test;

/**
 * @author Helios
 * Time：2024-03-12 12:55
 */
public class RpcSingleServerTest {
    @Test
    public void testStartNettyServer() {
        RpcSingleServer rpcSingleServer = new RpcSingleServer(
                "127.0.0.1:27880",
                "cn.hiles.test"
        );
        rpcSingleServer.startNettyServer();
    }
}
