import de.huntler.AsyncTask;
import de.huntler.TaskRegister;
import org.junit.Test;

import static org.junit.Assert.*;

public class RegisterTest {

    @Test
    public void testRegisteredTaskCallback() {
        MyRegisterClass register = new MyRegisterClass();
    }

    class MyRegisterClass extends TaskRegister<String> {

        AsyncTask<String> task;

        MyRegisterClass() {

            for (int i = 0; i < 50; i++) {

                task = new AsyncTask<>(() -> "Works");
                registerTask("Task_" + i, task);
                executeTask("Task_" + i);

            }

        }

        @Override
        public void registeredTaskCallback(int callbackCode, String taskName, String result) {

            assertTrue(callbackCode < 4 && callbackCode > 0);
            assertNotNull(taskName);
            if (callbackCode > TASK_FINISHED) {
                assertNull(result);
            }

        }
    }

}
