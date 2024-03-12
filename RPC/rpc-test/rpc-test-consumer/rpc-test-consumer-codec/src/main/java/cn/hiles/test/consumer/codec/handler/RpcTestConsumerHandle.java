package cn.hiles.test.consumer.codec.handler;

import cn.hiles.protocol.RpcProtocol;
import cn.hiles.protocol.head.RpcHeaderFactory;
import cn.hiles.protocol.request.RpcRequest;
import cn.hiles.protocol.response.RpcResponse;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Helios
 * Time：2024-03-12 22:28
 */
public class RpcTestConsumerHandle extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {
    private final Logger logger = LoggerFactory.getLogger(RpcTestConsumerHandle.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("start to send message to server");
        //模拟发送消息给服务端
        RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
        rpcProtocol.setRpcHeader(RpcHeaderFactory.getRequestHeader("jdk"));
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setGroup("test");
        rpcRequest.setClassName("cn.hiles.test.provider.service.DemoService");
        rpcRequest.setMethodName("hello");
        rpcRequest.setParameters(new Object[]{"Helios"});
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setVersion("1.0.0");
        rpcRequest.setAsync(false);
        rpcRequest.setOneWay(false);
        rpcProtocol.setBody(rpcRequest);
        logger.info("send message to server, request:{}", JSONObject.toJSONString(rpcProtocol));
        ctx.writeAndFlush(rpcProtocol);
        logger.info("send message to server over");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> rpcResponseRpcProtocol) throws Exception {
        logger.info("receive message from server, response:{}", JSONObject.toJSONString(rpcResponseRpcProtocol));
    }
}
