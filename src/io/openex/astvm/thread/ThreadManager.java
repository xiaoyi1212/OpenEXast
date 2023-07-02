package io.openex.astvm.thread;

import io.openex.astvm.lib.Function;
import io.openex.astvm.obj.ExValue;

import java.util.ArrayList;

public class ThreadManager {
    static ArrayList<ExValue> values = new ArrayList<>();
    static ArrayList<Function> functions = new ArrayList<>();
    static ArrayList<ThreadTask> tasks = new ArrayList<>();

    public static ArrayList<Function> getFunctions() {
        return functions;
    }

    public static ArrayList<ExValue> getValues() {
        return values;
    }

    public enum Status{
        RUNNING,DEATH,LOADING,WAIT,ERROR
    }
    public static void addThread(ThreadTask task){
        tasks.add(task);
    }
    public static void launch(){
        for(ThreadTask task:tasks){
            if(task.getName().equals("main")){
                task.start();
                return;
            }
        }
    }
}
