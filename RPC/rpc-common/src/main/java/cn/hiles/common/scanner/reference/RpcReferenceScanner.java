package cn.hiles.common.scanner.reference;

import cn.hiles.annotation.RpcReference;
import cn.hiles.common.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Helios
 * Time：2024-03-11 9:14
 * Description: RPC引用扫描器
 */
public class RpcReferenceScanner extends ClassScanner {
    private static final Logger logger = LoggerFactory.getLogger(RpcReferenceScanner.class);

    /**
     * 扫描指定包下的所有类
     *
     * @param packageName 包名
     */
    public static Map<String, Object> doScannerWithRpcReferenceAnnotationFilter(String packageName) throws Exception {
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(packageName);
        if (classNameList.isEmpty()) {
            return handlerMap;
        }
        classNameList.stream().forEach(
                (className) -> {
                    try {
                        Class<?> clazz = Class.forName(className);
                        Field[] fields = clazz.getDeclaredFields();
                        Stream.of(fields).forEach(
                                (field) -> {
                                    if (field.isAnnotationPresent(RpcReference.class)) {
                                        RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                                        logger.info("scan rpc reference field: {}", field.getName());
                                        logger.info("@RpcReference(version = {}, group = {},registryType = {}" +
                                                        ",registryAddress = {})",
                                                rpcReference.version(),
                                                rpcReference.group(),
                                                rpcReference.registryType(),
                                                rpcReference.registryAddress());
                                    }
                                }
                        );
                    } catch (Exception e) {
                        logger.error("scan classes throws exception", e);
                    }
                }
        );
        return handlerMap;
    }
}
