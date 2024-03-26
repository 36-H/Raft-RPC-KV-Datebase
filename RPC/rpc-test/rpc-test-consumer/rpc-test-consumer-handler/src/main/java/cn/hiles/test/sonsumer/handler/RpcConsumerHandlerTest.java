package cn.hiles.test.sonsumer.handler;

import cn.hiles.consumer.common.RpcConsumer;
import cn.hiles.consumer.common.callback.AsyncRpcCallBack;
import cn.hiles.consumer.common.context.RpcContext;
import cn.hiles.consumer.common.future.RpcFuture;
import cn.hiles.protocol.RpcProtocol;
import cn.hiles.protocol.head.RpcHeaderFactory;
import cn.hiles.protocol.request.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Helios
 * Time：2024-03-25 15:38
 */
public class RpcConsumerHandlerTest {
    private final static Logger logger = LoggerFactory.getLogger(RpcConsumerHandlerTest.class);
    public static void main(String[] args) throws Exception {
        RpcConsumer rpcConsumer = RpcConsumer.getInstance();
        rpcConsumer.sendRequest(getRpcRequestProtocol());
        RpcFuture rpcFuture = RpcContext.getContext().getRpcFuture();
        rpcFuture.addCallBack(new AsyncRpcCallBack() {
            @Override
            public void onSuccess(Object result) {
                logger.info("run callback success, args:{}", result);
            }

            @Override
            public void onException(Throwable throwable) {
                logger.info("run callback error, args:{}", throwable.getMessage());
            }
        });
//        logger.info("receive response:{}", rpcConsumer.sendRequest(getRpcRequestProtocol()).get());
        logger.info("receive response:{}", rpcFuture.get());
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
//        rpcRequest.setAsync(false);
        rpcRequest.setAsync(true);
        rpcRequest.setOneWay(false);
        rpcProtocol.setBody(rpcRequest);
        return rpcProtocol;
    }
}
