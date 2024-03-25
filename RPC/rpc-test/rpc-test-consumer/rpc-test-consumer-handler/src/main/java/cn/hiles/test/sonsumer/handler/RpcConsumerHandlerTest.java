package cn.hiles.test.sonsumer.handler;

import cn.hiles.consumer.common.RpcConsumer;
import cn.hiles.protocol.RpcProtocol;
import cn.hiles.protocol.head.RpcHeaderFactory;
import cn.hiles.protocol.request.RpcRequest;

/**
 * @author Helios
 * Time：2024-03-25 15:38
 */
public class RpcConsumerHandlerTest {
    public static void main(String[] args) throws Exception {
        RpcConsumer rpcConsumer = RpcConsumer.getInstance();
        rpcConsumer.sendRequest(getRpcRequestProtocol());
        Thread.sleep(2000);
        rpcConsumer.close();
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocol() {
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
        return rpcProtocol;
    }
}