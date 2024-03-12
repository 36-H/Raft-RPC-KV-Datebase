package cn.hiles.protocol.head;

import cn.hiles.common.id.IdFactory;
import cn.hiles.constants.RpcConstants;
import cn.hiles.protocol.enumeration.RpcType;

/**
 * @author Helios
 * Time：2024-03-12 16:31
 */
public class RpcHeaderFactory {
    public static RpcHeader getRequestHeader(String serializationType) {
        RpcHeader rpcHeader = new RpcHeader();
        long requestId = IdFactory.getId();
        rpcHeader.setMagic(RpcConstants.MAGIC);
        rpcHeader.setRequestId(requestId);
        rpcHeader.setMsgType((byte) RpcType.REQUEST.getType());
        rpcHeader.setStatus((byte) 0x1);
        rpcHeader.setSerializationType(serializationType);
        return rpcHeader;
    }
}
