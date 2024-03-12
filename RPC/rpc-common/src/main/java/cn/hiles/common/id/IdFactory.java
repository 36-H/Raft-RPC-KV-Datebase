package cn.hiles.common.id;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Helios
 * Time：2024-03-12 16:35
 */
public class IdFactory {
    private static final AtomicLong REQUEST_ID_GEN = new AtomicLong(0);

    public static long getId() {
        return REQUEST_ID_GEN.incrementAndGet();
    }
}
