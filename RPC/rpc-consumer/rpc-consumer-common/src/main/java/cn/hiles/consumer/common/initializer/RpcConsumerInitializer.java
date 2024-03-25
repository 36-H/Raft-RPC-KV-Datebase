package cn.hiles.consumer.common.initializer;

import cn.hiles.codec.RpcDecoder;
import cn.hiles.codec.RpcEncoder;
import cn.hiles.consumer.common.handler.RpcConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author Helios
 * Time：2024-03-25 14:56
 */
public class RpcConsumerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new RpcEncoder());
        pipeline.addLast(new RpcDecoder());
        pipeline.addLast(new RpcConsumerHandler());
    }
}
