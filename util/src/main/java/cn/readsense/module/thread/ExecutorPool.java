package cn.readsense.module.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorPool {
    private static ExecutorService executorService;

    static {
        executorService = Executors.newCachedThreadPool();
    }

    public static void exec(Runnable runnable) {
        executorService.submit(runnable);
    }
}
