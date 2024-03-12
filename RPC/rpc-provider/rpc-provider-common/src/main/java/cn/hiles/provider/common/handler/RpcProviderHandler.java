package cn.hiles.provider.common.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Helios
 * Time：2024-03-12 11:06
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);
    private final Map<String, Object> handlerMap;

    public RpcProviderHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("RPC Provider receive request:" + msg.toString());
        logger.info("the datas of handlerMap are:");
        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            logger.info("key: " + entry.getKey() + " value: " + entry.getValue());
        }
        ctx.writeAndFlush(msg);
    }
}
