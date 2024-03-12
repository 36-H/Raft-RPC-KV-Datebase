package cn.hiles.provider.common.handler;

import cn.hiles.protocol.RpcProtocol;
import cn.hiles.protocol.enumeration.RpcType;
import cn.hiles.protocol.head.RpcHeader;
import cn.hiles.protocol.request.RpcRequest;
import cn.hiles.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        logger.info("RPC Provider receive request:" + protocol);
        logger.info("the datas of handlerMap are:");
        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            logger.info("key: " + entry.getKey() + " value: " + entry.getValue());
        }
        RpcHeader header = protocol.getRpcHeader();
        RpcRequest request = protocol.getBody();
        // 创建RPC协议响应对象
        RpcProtocol<RpcResponse> rpcProtocol = new RpcProtocol<>();
        RpcResponse response = new RpcResponse();
        response.setResult("Data exchange successful");
        response.setAsync(request.isAsync());
        response.setOneWay(request.isOneWay());

        header.setMsgType((byte) RpcType.RESPONSE.getType());
        rpcProtocol.setRpcHeader(header);
        rpcProtocol.setBody(response);

        ctx.writeAndFlush(rpcProtocol);
    }
}
