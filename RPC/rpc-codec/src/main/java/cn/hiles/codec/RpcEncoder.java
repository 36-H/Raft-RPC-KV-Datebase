package cn.hiles.codec;

import cn.hiles.common.utils.SerializationUtils;
import cn.hiles.protocol.RpcProtocol;
import cn.hiles.protocol.head.RpcHeader;
import cn.hiles.serialization.api.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author Helios
 * Time：2024-03-12 20:34
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> implements RpcCodec {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          RpcProtocol<Object> objectRpcProtocol,
                          ByteBuf byteBuf) throws Exception {
        RpcHeader rpcHeader = objectRpcProtocol.getRpcHeader();
        byteBuf.writeShort(rpcHeader.getMagic());
        byteBuf.writeByte(rpcHeader.getMsgType());
        byteBuf.writeByte(rpcHeader.getStatus());
        byteBuf.writeLong(rpcHeader.getRequestId());
        String serializationType = rpcHeader.getSerializationType();
        //TODO Serialization 可被拓展
        Serialization serialization = getJdkSerialization();
        byteBuf.writeBytes(SerializationUtils.paddingString(serializationType).getBytes(StandardCharsets.UTF_8));
        byte[] data = serialization.serialize(objectRpcProtocol.getBody());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
