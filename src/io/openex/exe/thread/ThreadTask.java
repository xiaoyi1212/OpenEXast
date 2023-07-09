package io.openex.exe.thread;

import io.openex.exe.core.Executor;
import io.openex.exe.core.Script;
import io.openex.exe.lib.Function;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;

public class ThreadTask {
    Executor executor;
    String name;
    Thread thread;
    ThreadManager.Status status;
    ArrayList<Script> scripts;
    Function function;
    public ThreadTask(String name){
        this.name = name;
        this.scripts = new ArrayList<>();
        this.thread = new Thread(() ->{
            try{
                executor.launch();
            }catch (VMRuntimeException e){
                status = ThreadManager.Status.ERROR;
                e.printStackTrace();
            }
        });
        this.thread.setName("OpenEX-Executor-"+name);
        this.status = ThreadManager.Status.LOADING;
    }

    public String getFilename(){
        return executor.getExecuting().getFilename();
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public void setStatus(ThreadManager.Status status) {
        this.status = status;
    }

    public void addScripts(Script scripts) {
        this.scripts.add(scripts);
    }

    public String getName() {
        return name;
    }

    public void start(){
        if(function==null) {
            this.executor = new Executor(scripts.get(0), this);
            this.status = ThreadManager.Status.RUNNING;
            this.thread.start();
        }else {
            this.status = ThreadManager.Status.RUNNING;
            this.executor = new Executor(new Script(function.getLib(), function.getLib()+".exf",function.getBcs()),this);
            this.thread.start();
        }
    }
}
