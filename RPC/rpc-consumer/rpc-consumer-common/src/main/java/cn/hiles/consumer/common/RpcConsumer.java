package cn.hiles.consumer.common;

import cn.hiles.consumer.common.handler.RpcConsumerHandler;
import cn.hiles.consumer.common.initializer.RpcConsumerInitializer;
import cn.hiles.protocol.RpcProtocol;
import cn.hiles.protocol.request.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Helios
 * Time：2024-03-25 15:06
 */
public class RpcConsumer {
    private static volatile RpcConsumer rpcConsumer;
    private static Map<String, RpcConsumerHandler> handlerMap = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    private RpcConsumer() {
        this.bootstrap = new Bootstrap();
        this.eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new RpcConsumerInitializer());
    }

    public static RpcConsumer getInstance() {
        if (rpcConsumer == null) {
            synchronized (RpcConsumer.class) {
                if (rpcConsumer == null) {
                    rpcConsumer = new RpcConsumer();
                }
            }
        }
        return rpcConsumer;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

    public void sendRequest(RpcProtocol<RpcRequest> protocol) throws Exception {
        //TODO 暂时写死 后面改成从注册中心获取
        String serverAddress = "127.0.0.1";
        int port = 27880;
        String key = serverAddress.concat("_").concat(String.valueOf(port));
        RpcConsumerHandler handler = handlerMap.get(key);
        //如果没RpcConsumerHandler则创建一个
        if (Objects.isNull(handler)) {
            handler = getRpcConsumerHandler(serverAddress, port);
            handlerMap.put(key, handler);
        }
        //如果RpcConsumerHandler已经关闭则重新创建一个
        else if (!handler.getChannel().isActive()) {
            handler.close();
            handler = getRpcConsumerHandler(serverAddress, port);
            handlerMap.put(key, handler);
        }
        handler.sendRequest(protocol);
    }

    /**
     * 创建链接
     *
     * @param serverAddress 服务地址
     * @param port          端口
     * @return RpcConsumerHandler
     */
    private RpcConsumerHandler getRpcConsumerHandler(String serverAddress, int port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(serverAddress, port).sync();
        channelFuture.addListener((ChannelFutureListener) listener -> {
            if (listener.isSuccess()) {
                logger.info("Successfully connect to remote server: [{}:{}]", serverAddress, port);
            } else {
                logger.error("Failed to connect to remote server: [{}:{}]", serverAddress, port);
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        return channelFuture.channel().pipeline().get(RpcConsumerHandler.class);
    }

}
