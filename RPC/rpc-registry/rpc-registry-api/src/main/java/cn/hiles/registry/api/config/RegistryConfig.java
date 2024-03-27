package cn.hiles.registry.api.config;

/**
 * @author Helios
 * Time：2024-03-27 15:04
 */
public class RegistryConfig {
    private static final long serialVersionUID = 665428966144542L;
    /**
     * 注册中心地址
     */
    private String registryAddr;
    /**
     * 注册类型
     */
    private String registryType;

    public RegistryConfig(String registryAddr, String registryType) {
        this.registryAddr = registryAddr;
        this.registryType = registryType;
    }

    public String getRegistryAddr() {
        return registryAddr;
    }

    public void setRegistryAddr(String registryAddr) {
        this.registryAddr = registryAddr;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }
}
