package cn.hiles.protocol.head;

import java.io.Serializable;

/**
 * @author Helios
 * Time：2024-03-12 16:23
 */
public class RpcHeader implements Serializable {
    private static final long serialVersionUID = 125652681L;
    /*
    +--------------------------------------------------+
    | 魔数 2byte | 报文类型 1byte | 状态 1byte | ID 8byte |
    +--------------------------------------------------+
    |    序列化类型 16byte    |       数据长度 4byte       |
    +--------------------------------------------------+
     */
    /**
     * 魔数
     */
    private short magic;
    /**
     * 报文类型
     */
    private byte msgType;
    /**
     * 状态
     */
    private byte status;
    /**
     * 消息 ID
     */
    private long requestId;
    /**
     * 序列化类型 不足16位补0 约定序列化类型长度不超过16位
     */
    private String serializationType;
    /**
     * 数据长度
     */
    private int msgLen;

    public short getMagic() {
        return magic;
    }

    public void setMagic(short magic) {
        this.magic = magic;
    }

    public byte getMsgType() {
        return msgType;
    }

    public void setMsgType(byte msgType) {
        this.msgType = msgType;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getSerializationType() {
        return serializationType;
    }

    public void setSerializationType(String serializationType) {
        this.serializationType = serializationType;
    }

    public int getMsgLen() {
        return msgLen;
    }

    public void setMsgLen(int msgLen) {
        this.msgLen = msgLen;
    }
}
