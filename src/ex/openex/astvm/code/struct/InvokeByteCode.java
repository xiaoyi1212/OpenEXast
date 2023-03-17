package ex.openex.astvm.code.struct;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.exe.Executor;
import ex.openex.astvm.lib.Function;
import ex.openex.astvm.lib.RuntimeLibrary;
import ex.openex.astvm.thread.ThreadManager;
import ex.openex.compile.parser.BackParser;
import ex.openex.exception.ReturnException;
import ex.openex.exception.VMRuntimeException;

import java.util.ArrayList;

public class InvokeByteCode extends StructCode{
    String lib,function;
    ArrayList<ByteCode> var;

    public InvokeByteCode(String lib, String function, ArrayList<ByteCode> var){
        this.lib = lib;
        this.function = function;
        this.var = var;
    }

    @Override
    public String toString() {
        return "Invoke-Path:"+lib+"/"+function+"\n\t" +
                "Value:"+var;
    }

    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        try {
            for (RuntimeLibrary rl : executor.getLibrary()) {
                if (rl.getName().equals(lib)) {
                    for (RuntimeLibrary.RuntimeFunction rf : rl.functions()) {
                        if (rf.getName().equals(function)) {
                            for (ByteCode bc : var) bc.executor(executor);
                            rf.exe(executor);
                            return;
                        }
                    }
                    throw new VMRuntimeException("找不到指定方法:" + function, executor.getThread());
                }
            }
            for (Function function1 : ThreadManager.getFunctions()) {
                if (function1.getLib().equals(lib)) {
                    if (function1.getName().equals(function)) {
                        for (ByteCode bc : var) bc.executor(executor);
                        for (ByteCode bc : function1.getBcs()) {
                            bc.executor(executor);
                        }
                        return;
                    }
                }
            }
        }catch (ReturnException e){
            executor.push(e.getObj());
            return;
        }
        throw new VMRuntimeException("找不到指定库:"+lib,executor.getThread());
    }
}
