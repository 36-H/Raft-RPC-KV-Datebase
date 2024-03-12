package cn.hiles.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Helios
 * Time：2024-03-10 21:40
 * @description 服务消费者注解
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Autowired
public @interface RpcReference {
    /**
     * 版本号 默认1.0.0
     */
    String version() default "1.0.0";

    /**
     * 注册中心类型 默认zookeeper
     * 支持zookeeper、nacos、etcd、consul
     */
    String registryType() default "zookeeper";

    /**
     * 注册中心地址
     */
    String registryAddress() default "127.0.0.1:2181";

    /**
     * 负载均衡 默认基于zookeeper的一致性hash
     */
    String loadBalanceType() default "zkconsistencyhash";

    /**
     * 序列化方式 默认protostuff
     * 支持protostuff、kryo、json、jdk、hessian2、fst
     */
    String serializationType() default "protostuff";

    /**
     * 超时时间 默认5000ms
     */
    int timeout() default 5000;

    /**
     * 是否异步调用 默认false
     */
    boolean async() default false;

    /**
     * 是否单向调用 默认false
     */
    boolean oneway() default false;

    /**
     * 代理类型 默认jdk
     * 支持jdk、cglib、javassist
     */
    String proxy() default "jdk";

    /**
     * 服务分组
     */
    String group() default "";
}
