package io.openex.astvm.lib;

import io.openex.Main;
import io.openex.astvm.exe.Executor;
import io.openex.astvm.obj.ExDouble;
import io.openex.astvm.obj.ExNull;
import io.openex.astvm.obj.ExObject;
import io.openex.astvm.obj.ExString;
import io.openex.astvm.thread.ThreadManager;
import io.openex.astvm.thread.ThreadTask;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;
import java.util.Scanner;

public class Sys implements RuntimeLibrary{
    ArrayList<RuntimeFunction> rfs;
    public Sys(){
        rfs = new ArrayList<>();
        rfs.add(new Print());
        rfs.add(new Memory());
        rfs.add(new Input());
        rfs.add(new Thread());
        rfs.add(new CompilerVersion());
        rfs.add(new RuntimeVersion());
        rfs.add(new Stop());
        rfs.add(new Sleep());
    }
    @Override
    public ArrayList<RuntimeFunction> functions() {
        return rfs;
    }
    @Override
    public java.lang.String getName() {
        return "system";
    }
    private static class Print implements RuntimeFunction{
        @Override
        public int getVarNum() {
            return 1;
        }
        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) {
            ExObject obj = vars.get(0);
            if(obj.getType()==ExObject.ARRAY){
                System.out.println(obj);
            }else System.out.println(obj.getData());
            return new ExNull();
        }
        @Override
        public java.lang.String getName() {
            return "print";
        }
    }

    private static class Sleep implements RuntimeFunction{

        @Override
        public int getVarNum() {
            return 1;
        }

        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            ExObject o = vars.get(0);
            if(o.getType()!=ExObject.INTEGER)throw new VMRuntimeException("不兼容INT的类型,无法将未知类型输入进sleep函数的参数",executor.getThread());
            try {
                java.lang.Thread.sleep(Integer.parseInt(o.getData()));
            } catch (InterruptedException e) {
                throw new VMRuntimeException("[VMERROR-内部错误]:"+e.getLocalizedMessage(),executor.getThread());
            }
            return new ExNull();
        }

        @Override
        public java.lang.String getName() {
            return "sleep";
        }
    }

    private static class Stop implements RuntimeFunction{

        @Override
        public int getVarNum() {
            return 1;
        }

        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            ExObject o = vars.get(0);
            if(o.getType()!=ExObject.INTEGER)throw new VMRuntimeException("不兼容INT的类型,无法将未知类型输入进stop函数的参数",executor.getThread());
            System.exit(Integer.parseInt(o.getData()));
            return new ExNull();
        }

        @Override
        public java.lang.String getName() {
            return "stop";
        }
    }

    private static class Memory implements RuntimeFunction{
        @Override
        public int getVarNum() {
            return 0;
        }

        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) {
            Runtime runtime = Runtime.getRuntime();
            long mb = runtime.totalMemory() - runtime.freeMemory();
            return new ExDouble(mb(mb));
        }

        private static double mb (long s) {
            return Double.parseDouble(String.format("%.2f",(double)s / (1024 * 1024)));
        }


        @Override
        public java.lang.String getName() {
            return "memory";
        }
    }

    private static class Input implements RuntimeFunction{
        @Override
        public int getVarNum() {
            return 0;
        }

        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) {
            executor.getThread().setStatus(ThreadManager.Status.WAIT);
            Scanner s = new Scanner(System.in);
            String data = s.nextLine();
            executor.getThread().setStatus(ThreadManager.Status.RUNNING);
            return new ExString(data);
        }

        @Override
        public java.lang.String getName() {
            return "input";
        }
    }

    private static class Thread implements RuntimeFunction{

        @Override
        public int getVarNum() {
            return 2;
        }

        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            ExObject name = vars.get(0),func = vars.get(1);
            ThreadTask task = new ThreadTask(name.getData());
            Function function = null;
            for(Function f:ThreadManager.getFunctions()) {
                if(f.getLib().equals(executor.getExecuting().getInvoke_name())){
                    if(f.getName().equals(func.getData())) function = f;
                }
            }
            if(function == null)throw new VMRuntimeException("找不到指定函数:"+func.getData(),executor.getThread());
            task.setFunction(function);
            task.start();
            ThreadManager.addThread(task);
            return new ExString(task.getName());
        }

        @Override
        public java.lang.String getName() {
            return "thread";
        }
    }
    private static class CompilerVersion implements RuntimeFunction{

        @Override
        public int getVarNum() {
            return 0;
        }

        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            return new ExString(Main.compile_version);
        }

        @Override
        public java.lang.String getName() {
            return "compiler_version";
        }
    }
    private static class RuntimeVersion implements RuntimeFunction{

        @Override
        public int getVarNum() {
            return 0;
        }

        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            return new ExString(Main.runtime_version);
        }

        @Override
        public java.lang.String getName() {
            return "runtime_version";
        }
    }
}
