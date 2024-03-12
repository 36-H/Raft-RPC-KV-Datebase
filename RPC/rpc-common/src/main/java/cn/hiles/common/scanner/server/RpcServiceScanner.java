package cn.hiles.common.scanner.server;

import cn.hiles.annotation.RpcService;
import cn.hiles.common.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Helios
 * Time：2024-03-11 8:54
 */
public class RpcServiceScanner extends ClassScanner {
    private static final Logger logger = LoggerFactory.getLogger(RpcServiceScanner.class);

    public static Map<String, Object> doScanWithRpcServiceAnnotationFilterAndRegistryService(String scanPackage) throws Exception {
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList.isEmpty()) {
            return handlerMap;
        }
        classNameList.stream().forEach(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(RpcService.class)) {
                    RpcService rpcService = clazz.getAnnotation(RpcService.class);
                    logger.info("scan rpc service class: {}", className);
                    logger.info("@RpcService(interfaceClass = {}, version = {}, group = {})", rpcService.interfaceClass(), rpcService.version(), rpcService.group());
                    String serviceName = getServiceName(rpcService);
                    String key = serviceName.concat(rpcService.version()).concat(rpcService.group());
                    handlerMap.put(key, clazz.newInstance());
                }
            } catch (Exception e) {
                logger.error("scan classes throws exception", e);
            }
        });
        return handlerMap;
    }

    private static String getServiceName(RpcService rpcService) {
        //优先使用interfaceClass，interfaceClassName为空时使用interfaceClassName
        if (!rpcService.interfaceClass().equals(void.class)) {
            return rpcService.interfaceClass().getName();
        } else {
            return rpcService.interfaceClassName();
        }
    }
}
