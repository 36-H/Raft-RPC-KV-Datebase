package cn.hiles.provider.common.server.base;

import cn.hiles.codec.RpcDecoder;
import cn.hiles.codec.RpcEncoder;
import cn.hiles.provider.common.handler.RpcProviderHandler;
import cn.hiles.provider.common.server.api.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Helios
 * Time：2024-03-12 11:15
 */
public class BaseServer implements Server {
    private final static Logger logger = LoggerFactory.getLogger(BaseServer.class);
    /**
     * 主机域名或者IP
     */
    protected String host = "127.0.0.1";
    /**
     * 端口
     */
    protected int port = 27110;
    /**
     * 实体类关系
     */
    protected Map<String, Object> handlerMap = new HashMap<>();

    public BaseServer(String serverAddress) {
        if (!StringUtils.isEmpty(serverAddress)) {
            String[] addressArray = serverAddress.split(":");
            if (addressArray.length != 2) {
                throw new IllegalArgumentException("serverAddress format is error");
            }
            host = addressArray[0];
            port = Integer.parseInt(addressArray[1]);
        }
    }

    @Override
    public void startNettyServer() {
        logger.info("start Netty Server");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // TODO
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcDecoder())
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcProviderHandler(handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(host, port).sync();
            logger.info("RPC Server start success on {},{} ", host, port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("start RPC Netty Server error", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
