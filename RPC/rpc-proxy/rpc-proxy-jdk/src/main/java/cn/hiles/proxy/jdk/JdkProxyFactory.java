package cn.hiles.proxy.jdk;


import cn.hiles.proxy.api.BaseProxyFactory;
import cn.hiles.proxy.api.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * @author Helios
 * Time：2024-03-26 16:26
 */
public class JdkProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                objectProxy
        );
    }

}
