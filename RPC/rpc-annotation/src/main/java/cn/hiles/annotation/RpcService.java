package cn.hiles.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Helios
 * Time：2024-03-10 21:40
 * @description 服务提供者注解
 * e.g
 * * @RpcService(interfaceClass = HelloService.class, version = "1.0.0", group = "test")
 * * @RpcService(interfaceClassName = "cn.hiles.service.HelloService", version = "1.0.0", group = "test")
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    /**
     * 接口的class
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 接口的类名称
     */
    String interfaceClassName() default "";

    /**
     * 版本号 默认1.0.0
     */
    String version() default "1.0.0";

    /**
     * 分组 默认为空
     */
    String group() default "";
}
