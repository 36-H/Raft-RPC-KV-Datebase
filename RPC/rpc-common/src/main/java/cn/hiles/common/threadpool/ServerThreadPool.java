package cn.hiles.common.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务端线程池，用于异步处理客户端请求
 *
 * @author Helios
 * Time：2024-03-25 9:39
 */
public class ServerThreadPool {
    private static ThreadPoolExecutor threadPoolExecutor;

    static {
        threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(65536));
    }

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    public static void shutdown() {
        threadPoolExecutor.shutdown();
    }
}
