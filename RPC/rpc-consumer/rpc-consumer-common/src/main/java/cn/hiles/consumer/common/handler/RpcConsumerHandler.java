package cn.hiles.consumer.common.handler;

import cn.hiles.consumer.common.context.RpcContext;
import cn.hiles.protocol.RpcProtocol;
import cn.hiles.protocol.head.RpcHeader;
import cn.hiles.protocol.request.RpcRequest;
import cn.hiles.protocol.response.RpcResponse;
import cn.hiles.proxy.api.future.RpcFuture;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Helios
 * Time：2024-03-25 14:35
 */
public class RpcConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {
    private final Logger logger = LoggerFactory.getLogger(RpcConsumerHandler.class);

    private volatile Channel channel;
    private SocketAddress remotePeer;
    /**
     * 用于存放请求的唯一标识和对应的响应结果
     * <p>
     * 当调用RpcConsumerHandler类中的sendRequest()方法向服务提供者发送数据后，
     * 解析RpcRequest协议对象中的header，并获取到header中的requestId，
     * 在一个while(true)循环中不断检测pendingResponse成员变量中是否存在以requestId为Key的RpcResponse协议对象,
     * 如果不存在，则继续循环检测，如果存在，则直接获取返回的结果数据。
     * pendingResponse成员变量中的数据是在RpcConsumerHandler类中的channelRead0()方法中进行填充的，
     * 也就是说，服务消费者接收到服务提供者响应的结果数据RpcResponse协议对象后，就会解析RpcResponse协议对象，获取到header中的requestId，
     * 将requestId作为Key，RpcResponse协议对象作为Value存储到pendingResponse成员变量中。
     * 当pendingResponse成员变量中存储了与sendRequest()方法中获取的相同requestId的RpcResponse协议对象时，
     * sendRequest()方法就会获取到pendingResponse成员变量中的数据，从而退出while(true)循环并返回结果数据。
     * </p>
     * 修改为RpcFuture 用于异步转同步
     * 解除了while(true)的阻塞
     */
    private Map<Long, RpcFuture> pendingRpc = new ConcurrentHashMap<>();


    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> rpcResponseRpcProtocol) throws Exception {
        if (Objects.isNull(rpcResponseRpcProtocol)) {
            return;
        }
        logger.info("Client receive message: [{}]", JSONObject.toJSONString(rpcResponseRpcProtocol));
        long requestId = rpcResponseRpcProtocol.getRpcHeader().getRequestId();
        RpcFuture rpcFuture = pendingRpc.remove(requestId);
        if (Objects.nonNull(rpcFuture)) {
            rpcFuture.done(rpcResponseRpcProtocol);
        }
    }

    /**
     * 消费者发送请求
     *
     * @param request 请求
     */
    public RpcFuture sendRequest(RpcProtocol<RpcRequest> request, boolean async, boolean oneWay) {
        logger.info("Client send message: [{}]", JSONObject.toJSONString(request));
        return oneWay ? this.sendRequestOneWay(request) : (async ? this.sendRequestAsync(request) : this.sendRequestSync(request));
    }

    /**
     * 消费者发送请求 同步调用
     *
     * @param request 请求
     * @return RpcFuture
     */
    private RpcFuture sendRequestSync(RpcProtocol<RpcRequest> request) {
        RpcFuture rpcFuture = this.getRpcFuture(request);
        channel.writeAndFlush(request);
        return rpcFuture;
    }

    /**
     * 消费者发送请求 异步调用
     *
     * @param request 请求
     * @return RpcFuture
     */
    private RpcFuture sendRequestAsync(RpcProtocol<RpcRequest> request) {
        RpcFuture rpcFuture = this.getRpcFuture(request);
        //异步调用 将RpcFuture放入RpcContext上下文
        RpcContext.getContext().setRpcFuture(rpcFuture);
        channel.writeAndFlush(request);
        //通过RpcContext获取结果 所以这里返回null
        return null;
    }

    /**
     * 消费者发送请求 单向调用
     *
     * @param request 请求
     * @return RpcFuture
     */
    private RpcFuture sendRequestOneWay(RpcProtocol<RpcRequest> request) {
        channel.writeAndFlush(request);
        return null;
    }


    private RpcFuture getRpcFuture(RpcProtocol<RpcRequest> request) {
        RpcFuture rpcFuture = new RpcFuture(request);
        RpcHeader rpcHeader = request.getRpcHeader();
        pendingRpc.put(rpcHeader.getRequestId(), rpcFuture);
        return rpcFuture;
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
