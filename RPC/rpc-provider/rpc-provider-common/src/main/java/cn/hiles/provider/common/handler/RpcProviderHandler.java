package cn.hiles.provider.common.handler;

import cn.hiles.common.helper.RpcServiceHelper;
import cn.hiles.common.threadpool.ServerThreadPool;
import cn.hiles.protocol.RpcProtocol;
import cn.hiles.protocol.enumeration.RpcStatus;
import cn.hiles.protocol.enumeration.RpcType;
import cn.hiles.protocol.head.RpcHeader;
import cn.hiles.protocol.request.RpcRequest;
import cn.hiles.protocol.response.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Helios
 * Time：2024-03-12 11:06
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    private static final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);
    /**
     * 存储服务提供者中被@RpcService注解标注的类的对象
     * key为：serviceName#serviceVersion#group
     * value为：@RpcService注解标注的类的对象
     */
    private final Map<String, Object> handlerMap;

    public RpcProviderHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) throws Exception {
        ServerThreadPool.submit(() -> {
            RpcHeader header = protocol.getRpcHeader();
            header.setMsgType((byte) RpcType.RESPONSE.getType());
            RpcRequest request = protocol.getBody();
            logger.debug("Receive request: {}", request);
            // 构建响应对象
            RpcProtocol<RpcResponse> responseProtocol = new RpcProtocol<>();
            RpcResponse response = new RpcResponse();
            try {
                Object result = handle(request);
                response.setResult(result);
                response.setAsync(request.isAsync());
                response.setOneWay(request.isOneWay());
                header.setStatus((byte) RpcStatus.SUCCESS.getCode());
            } catch (Throwable t) {
                response.setError(t.toString());
                header.setStatus((byte) RpcStatus.FAIL.getCode());
                logger.error("RPC Server handle request error", t);
            }
            responseProtocol.setRpcHeader(header);
            responseProtocol.setBody(response);
            ctx.writeAndFlush(responseProtocol).addListener(
                    new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            logger.debug("Send response for request: {}", header.getRequestId());
                        }
                    }
            );
        });
    }

    private Object handle(RpcRequest request) throws Throwable {
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        Object serviceBean = handlerMap.get(serviceKey);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("Can not find service: %s:%s", request.getClassName(), request.getMethodName()));
        }
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        logger.debug(serviceClass.getName());
        logger.debug(methodName);
        if (parameterTypes != null) {
            for (Class<?> parameterType : parameterTypes) {
                logger.debug(parameterType.getName());
            }
        }
        if (parameters != null) {
            for (Object parameter : parameters) {
                logger.debug(parameter.toString());
            }
        }
        return invokeMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
    }

    private Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
        // 获取方法
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        // 执行方法
        return method.invoke(serviceBean, parameters);
    }



}
