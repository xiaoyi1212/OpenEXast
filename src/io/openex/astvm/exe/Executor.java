package io.openex.astvm.exe;

import io.openex.astvm.code.ByteCode;
import io.openex.astvm.lib.RuntimeLibrary;
import io.openex.astvm.obj.ExObject;
import io.openex.astvm.thread.ThreadTask;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;
import java.util.Stack;

public class Executor {

    Stack<ExObject> stack = new Stack<>();
    ArrayList<RuntimeLibrary> rls;
    ThreadTask thread;
    Script executing;

    public String getStack() {
        return stack.toString();
    }

    public Script getExecuting() {
        return executing;
    }

    public ThreadTask getThread() {
        return thread;
    }

    public void push(ExObject obj){
        stack.push(obj);
    }
    public ExObject pop(){
        return stack.pop();
    }
    public ExObject peek(){
        return stack.peek();
    }

    public ArrayList<RuntimeLibrary> getLibrary() {
        return rls;
    }

    public Executor(Script script, ThreadTask thread){
        this.executing = script;
        this.thread = thread;
        this.rls = LibraryLoader.getLibs();
    }

    public void launch() throws VMRuntimeException {
        for(ByteCode bc: executing.bcs){
            bc.executor(this);
        }

    }
}
