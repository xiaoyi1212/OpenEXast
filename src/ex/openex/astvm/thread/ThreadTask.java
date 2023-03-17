package ex.openex.astvm.thread;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.exe.Executor;
import ex.openex.astvm.exe.Script;
import ex.openex.astvm.lib.Function;
import ex.openex.exception.VMRuntimeException;

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
