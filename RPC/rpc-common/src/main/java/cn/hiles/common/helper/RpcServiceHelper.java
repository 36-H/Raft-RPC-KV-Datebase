package cn.hiles.common.helper;

/**
 * @author Helios
 * Time：2024-03-25 9:43
 */
public class RpcServiceHelper {
    /**
     * 构建服务名称
     *
     * @param serviceName    服务名称
     * @param serviceVersion 服务版本
     * @param group          服务组
     * @return 服务名称
     */
    public static String buildServiceKey(String serviceName, String serviceVersion, String group) {
        return String.join("#", serviceName, serviceVersion, group);
    }
}
