package de.huntler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

/**
 * @author Jonas Leuchtenberger 09.08.2019
 * <p>
 * This class is used to register {@link AsyncTask}s and organize them. You
 * can also
 */
public abstract class TaskRegister<V> {

    public static final int TASK_FINISHED = 1;
    public static final int TASK_REGISTERED = 2;
    public static final int TASK_EXECUTE = 3;

    private HashMap<String, AsyncTask<V>> registeredTasks = new HashMap<>();
    private Queue<String> taskExecutionQueue = new PriorityQueue<>();

    private ArrayList<String> currentTasksRunning = new ArrayList<>();

    private boolean queueTaskRunning;

    /**
     * Method adds a task to the registered tasks. A registered task
     * will not run immediately.
     *
     * @param name should be the name of the task under which it can be found
     * @param task should be an {@link AsyncTask} to register
     */
    public void registerTask(String name, AsyncTask<V> task) {
        this.registeredTasks.put(name, task);
        this.registeredTaskCallback(TASK_REGISTERED, name, null);
    }

    /**
     * Method appends the given task to a queue of {@link AsyncTask}s so it will be
     * executed after the others.
     *
     * @param name should be the name of a task
     */
    public void executeTask(String name) {
        this.taskExecutionQueue.add(name);
        if (!this.queueTaskRunning)
            this.processQueue();
    }

    /**
     * Method runs the task behind name immediately. But the Task will NOT
     * be in {@code currentTasksRunning} if {@code immediately} is true!
     *
     * @param name        should be the task to execute
     * @param immediately if the task should run immediately set this to true
     */
    public void executeTask(String name, boolean immediately) {
        if (immediately) {
            this.registeredTasks.get(name).run();
            return;
        }

        this.executeTask(name);
    }

    /**
     * method processes the queue of tasks
     */
    private void processQueue() {
        if (this.queueTaskRunning) return;

        Thread background = new Thread(() -> {
            this.queueTaskRunning = true;

            while (!this.taskExecutionQueue.isEmpty()) {
                String name = this.taskExecutionQueue.poll();
                this.currentTasksRunning.add(name);

                try {
                    this.registeredTaskCallback(TASK_EXECUTE, name, null);
                    AsyncTask<V> task = this.registeredTasks.get(name);
                    task.run();
                    V result = task.get();
                    this.currentTasksRunning.remove(name);
                    this.registeredTaskCallback(TASK_FINISHED, name, result);

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            this.queueTaskRunning = false;
        });

        background.start();
    }

    /**
     * This method will cancel the queue safely, that means
     * the las task will run to its end and after that the queue will be terminated.
     */
    public void cancelQueue() {
        this.taskExecutionQueue.clear();
    }

    /**
     * Method fetches the amount of tasks inside the queue
     *
     * @return returns the amount of tasks
     */
    public int getTasksInQueue() {
        return this.taskExecutionQueue.size();
    }

    /**
     * Method to get registered running async tasks in the process queue
     *
     * @return returns an array list of it
     */
    public ArrayList<String> getCurrentTasksRunning() {
        return this.currentTasksRunning;
    }

    /**
     * The controller class should extend {@link TaskRegister} and implement this method.
     * On task register, execute, finish, ... this method will be called.
     *
     * @param callbackCode this will be the code for handle the situations of registering,
     *                     execution, finishing, ...
     * @param taskName     this will be the name of the task which send the callback
     * @param result       if the task produce a result, it will be send through. Can be null
     */
    public abstract void registeredTaskCallback(int callbackCode, String taskName, V result);
}
