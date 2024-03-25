package cn.hiles.provider;

import cn.hiles.common.scanner.server.RpcServiceScanner;
import cn.hiles.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Helios
 * Time：2024-03-12 11:33
 */
public class RpcSingleServer extends BaseServer {
    private final Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);

    public RpcSingleServer(String serverAddress, String reflectType, String scanPackage) {
        super(serverAddress, reflectType);
        try {
            handlerMap = RpcServiceScanner.doScanWithRpcServiceAnnotationFilterAndRegistryService(scanPackage);
        } catch (Exception e) {
            logger.error("RpcSingleServer init error", e);
        }
    }
}
