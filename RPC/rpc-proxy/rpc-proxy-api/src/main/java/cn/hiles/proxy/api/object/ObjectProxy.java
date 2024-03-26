package cn.hiles.proxy.api.object;

import cn.hiles.protocol.RpcProtocol;
import cn.hiles.protocol.head.RpcHeaderFactory;
import cn.hiles.protocol.request.RpcRequest;
import cn.hiles.proxy.api.async.IAsyncObjectProxy;
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
public class ObjectProxy<T> implements InvocationHandler, IAsyncObjectProxy {
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

    /**
     * 代理对象调用方法 同步直接返回结果
     *
     * @param proxy  代理对象
     * @param method 方法
     * @param args   参数
     * @return 返回值
     * @throws Throwable 异常
     */
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

    /**
     * 异步代理对象调用方法 异步返回结果
     *
     * @param funcName 方法名
     * @param args     参数
     * @return RpcFuture
     */
    @Override
    public RpcFuture call(String funcName, Object... args) {
        RpcProtocol<RpcRequest> requestRpcProtocol = createRequest(this.clazz.getName(), funcName, args);
        RpcFuture rpcFuture = null;
        try {
            rpcFuture = consumer.sendRequest(requestRpcProtocol);
        } catch (Exception e) {
            logger.error("call error", e);
        }
        return rpcFuture;
    }

    private RpcProtocol<RpcRequest> createRequest(String className, String methodName, Object[] args) {
        RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
        requestRpcProtocol.setRpcHeader(RpcHeaderFactory.getRequestHeader(serializationType));

        RpcRequest request = new RpcRequest();
        request.setVersion(this.serviceVersion);
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameters(args);
        request.setGroup(this.serviceGroup);
        request.setAsync(async);
        request.setOneWay(oneway);
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);

        requestRpcProtocol.setBody(request);
        for (int i = 0; i < parameterTypes.length; i++) {
            logger.debug("参数类型：" + parameterTypes[i].getName() + " 参数值：" + args[i]);
        }
        for (Object arg : args) {
            logger.debug("参数类型：" + arg.getClass().getName() + " 参数值：" + arg);
        }
        return requestRpcProtocol;

    }

    private Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getTypeName();
        switch (typeName) {
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
            default:
                return classType;
        }
    }
}
