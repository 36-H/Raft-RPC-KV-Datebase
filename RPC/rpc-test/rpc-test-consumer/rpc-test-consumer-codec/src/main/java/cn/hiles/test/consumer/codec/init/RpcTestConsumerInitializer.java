package cn.hiles.test.consumer.codec.init;

import cn.hiles.codec.RpcDecoder;
import cn.hiles.codec.RpcEncoder;
import cn.hiles.test.consumer.codec.handler.RpcTestConsumerHandle;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author Helios
 * Time：2024-03-12 22:47
 */
public class RpcTestConsumerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new RpcEncoder())
                .addLast(new RpcDecoder())
                .addLast(new RpcTestConsumerHandle());
    }
}
