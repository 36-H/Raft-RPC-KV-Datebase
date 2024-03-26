package cn.hiles.common.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 消费者线程池
 *
 * @author Helios
 * Time：2024-03-26 10:30
 */
public class ClientThreadPool {
    private static final ThreadPoolExecutor threadPoolExecutor;

    static {
        threadPoolExecutor = new ThreadPoolExecutor(
                16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(65536)
        );
    }

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    public static void shutdown() {
        threadPoolExecutor.shutdown();
    }
}
