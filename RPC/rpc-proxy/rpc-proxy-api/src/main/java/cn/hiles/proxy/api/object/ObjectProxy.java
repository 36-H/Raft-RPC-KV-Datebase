package cn.hiles.proxy.api.object;

import cn.hiles.protocol.RpcProtocol;
import cn.hiles.protocol.head.RpcHeaderFactory;
import cn.hiles.protocol.request.RpcRequest;
import cn.hiles.proxy.api.consumer.Consumer;
import cn.hiles.proxy.api.future.RpcFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author Helios
 * Time：2024-03-26 11:24
 */
public class ObjectProxy<T> implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(ObjectProxy.class);
    /**
     * 接口的class对象
     */
    private Class<T> clazz;
    /**
     * 服务版本号
     */
    private String serviceVersion;
    /**
     * 服务分组
     */
    private String serviceGroup;
    /**
     * 超时时间 默认15s
     */
    private long timeout = 15000;
    /**
     * 服务消费者
     */
    private Consumer consumer;
    /**
     * 序列化类型
     */
    private String serializationType;
    /**
     * 是否异步
     */
    private boolean async;
    /**
     * 是否单向
     */
    private boolean oneway;

    public ObjectProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ObjectProxy(Class<T> clazz, String serviceVersion,
                       String serviceGroup,
                       long timeout,
                       Consumer consumer,
                       String serializationType,
                       boolean async,
                       boolean oneway) {
        this.clazz = clazz;
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.timeout = timeout;
        this.consumer = consumer;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            switch (name) {
                case "equals":
                    return proxy == args[0];
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "toString":
                    return proxy.getClass().getName() + "@" +
                            Integer.toHexString(System.identityHashCode(proxy)) +
                            ", with InvocationHandler " + this;
                default:
                    throw new IllegalStateException(String.valueOf(method));
            }
        }
        RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
        requestRpcProtocol.setRpcHeader(RpcHeaderFactory.getRequestHeader(serializationType));

        RpcRequest request = new RpcRequest();
        request.setVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setGroup(this.serviceGroup);
        request.setParameters(args);
        request.setAsync(async);
        request.setOneWay(oneway);
        requestRpcProtocol.setBody(request);
        //debug
        logger.debug(method.getDeclaringClass().getName() + "." + method.getName() + " request: " + Arrays.toString(args));
        if (method.getParameterTypes().length > 0) {
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                logger.debug("参数类型：" + method.getParameterTypes()[i].getName() + " 参数值：" + args[i]);
            }
        }
        if (args != null) {
            for (Object arg : args) {
                logger.debug("参数类型：" + arg.getClass().getName() + " 参数值：" + arg);
            }
        }
        RpcFuture rpcFuture = consumer.sendRequest(requestRpcProtocol);
        return rpcFuture == null ? null : timeout > 0 ? rpcFuture.get(timeout, TimeUnit.MILLISECONDS) : rpcFuture.get();
    }
}
