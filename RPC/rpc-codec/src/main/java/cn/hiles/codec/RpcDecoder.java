package cn.hiles.codec;

import cn.hiles.common.exception.SerializerException;
import cn.hiles.common.utils.SerializationUtils;
import cn.hiles.constants.RpcConstants;
import cn.hiles.protocol.RpcProtocol;
import cn.hiles.protocol.enumeration.RpcType;
import cn.hiles.protocol.head.RpcHeader;
import cn.hiles.protocol.request.RpcRequest;
import cn.hiles.protocol.response.RpcResponse;
import cn.hiles.serialization.api.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Helios
 * Time：2024-03-12 21:06
 */
public class RpcDecoder extends ByteToMessageDecoder implements RpcCodec {
    /*
    +--------------------------------------------------+
    | 魔数 2byte | 报文类型 1byte | 状态 1byte | ID 8byte |
    +--------------------------------------------------+
    |    序列化类型 16byte    |       数据长度 4byte       |
    +--------------------------------------------------+
     */
    @Override
    public final void decode(ChannelHandlerContext channelHandlerContext,
                             ByteBuf in,
                             List<Object> out) {
        if (in.readableBytes() < RpcConstants.HEADER_TOTAL_LENGTH) {
            throw new SerializerException("the length of byteBuf is less than HEADER_TOTAL_LENGTH");
        }
        //标记当前读指针位置
        in.markReaderIndex();
        short magic = in.readShort();
        if (magic != RpcConstants.MAGIC) {
            throw new SerializerException("magic number error: " + magic);
        }
        byte msgType = in.readByte();
        byte status = in.readByte();
        long requestId = in.readLong();
        ByteBuf serializationTypeBuf = in.readBytes(
                SerializationUtils.MAX_SERIALIZATION_TYPE_LENGTH
        );
        String serializationType = SerializationUtils.
                trimString(serializationTypeBuf.toString(StandardCharsets.UTF_8));
        int dataLength = in.readInt();
        //数据长度不足
        if (in.readableBytes() < dataLength) {
            //重置读指针
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        RpcType rpcType = RpcType.findByCode(msgType);
        if (rpcType == null) {
            throw new SerializerException("msgType error: " + msgType);
        }
        RpcHeader header = new RpcHeader();
        header.setMagic(magic);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setSerializationType(serializationType);
        header.setMsgLen(dataLength);
        //TODO 可拓展
        Serialization serialization = getJdkSerialization();
        switch (rpcType) {
            case REQUEST:
                RpcRequest rpcRequest = serialization.deserialize(data, RpcRequest.class);
                if (rpcRequest != null) {
                    RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
                    rpcProtocol.setRpcHeader(header);
                    rpcProtocol.setBody(rpcRequest);
                    out.add(rpcProtocol);
                }
                break;
            case RESPONSE:
                RpcResponse rpcResponse = serialization.deserialize(data, RpcResponse.class);
                if (rpcResponse != null) {
                    RpcProtocol<RpcResponse> rpcProtocol = new RpcProtocol<>();
                    rpcProtocol.setRpcHeader(header);
                    rpcProtocol.setBody(rpcResponse);
                    out.add(rpcProtocol);
                }
                break;
            case HEARTBEAT:
                //TODO
                break;
        }
    }
}
