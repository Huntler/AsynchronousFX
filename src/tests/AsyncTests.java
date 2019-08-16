import de.huntler.AsyncTask;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AsyncTests {

    @Test (expected = RuntimeException.class)
    public void taskShouldThrowExceptionWithoutCallable() {
        AsyncTask task = new AsyncTask();
        task.run();
    }

    @Test
    public void taskShouldReturnNotNull() {
        AsyncTask<String> task = new AsyncTask<>(() -> "Works");
        task.setOnFinish(Assert::assertNotNull);
        task.run();
    }

    @Test
    public void taskShouldReturnValuesInProgress() {
        AtomicInteger index = new AtomicInteger();
        AsyncTask task = new AsyncTask();
        task.setOnProgress(callback -> assertEquals((int) callback, index.getAndIncrement()));
        task.setCallable(() -> {
            for (int i = 0; i < 50; i++) {
                task.getProgressCallback().callback(i);
            }
            return null;
        });
    }

}
