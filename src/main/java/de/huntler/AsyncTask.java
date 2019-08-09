package de.huntler;

import java.util.concurrent.*;

/**
 * @author Jonas Leuchtenberger 09.08.2019
 */
public class AsyncTask<V> implements RunnableFuture<V> {

    private Callable<V> task;
    private Callback<V> finishCallback;
    private Callback progressCallback;

    private boolean isDone = false;
    private V result;

    /**
     * AsyncTask runs a callable in the background.
     *
     * @param callable should be the callable to run in background
     */
    public AsyncTask(Callable<V> callable) {
        this.task = callable;
    }

    public AsyncTask() {
    }

    /**
     * sets the callable if not set
     *
     * @param callable should be the callable to run in background
     */
    public void setCallable(Callable<V> callable) {
        this.task = callable;
    }

    /**
     * set up the onProgress callback so you can get a progress
     *
     * @param callback should be the callback handler
     */
    public void setOnProgress(Callback callback) {
        this.progressCallback = callback;
    }

    public Callback getProgressCallback() {
        if (this.progressCallback == null) throw new RuntimeException("No callback set for: progress");
        return this.progressCallback;
    }

    /**
     * set up the onFinish callback so you can get a result {@code v}
     *
     * @param callback should be a finishCallback handler
     */
    public void setOnFinish(Callback<V> callback) {
        this.finishCallback = callback;
    }

    @Override
    public void run() {
        if (this.task == null) throw new RuntimeException("No callable to run in background specified.");

        this.isDone = false;
        new Thread(() -> {
            result = null;
            try {
                result = this.task.call();
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.isDone = true;

            if (this.finishCallback != null)
                this.finishCallback.callback(result);

            synchronized (task) {
                task.notify();
            }
            ;
        }).start();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new RuntimeException("Cancel an AsyncThread is not implemented yet.");
    }

    @Override
    public boolean isCancelled() {
        throw new RuntimeException("Cancel an AsyncThread is not implemented yet.");
    }

    @Override
    public boolean isDone() {
        return this.isDone;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        synchronized (task) {
            try {
                task.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new RuntimeException("Cancel for an result is not implemented yet.");
    }
}
