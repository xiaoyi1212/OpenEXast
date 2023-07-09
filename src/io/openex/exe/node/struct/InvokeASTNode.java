package io.openex.exe.node.struct;

import io.openex.util.CommandManager;
import io.openex.exe.node.ASTNode;
import io.openex.exe.core.Executor;
import io.openex.exe.lib.Function;
import io.openex.exe.lib.RuntimeLibrary;
import io.openex.exe.lib.util.NetSocketImp;
import io.openex.exe.obj.ExObject;
import io.openex.exe.thread.ThreadManager;
import io.openex.util.ReturnException;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;

public class InvokeASTNode extends StructNode {
    String lib,function;
    ArrayList<ASTNode> var;

    public InvokeASTNode(String lib, String function, ArrayList<ASTNode> var){
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
                            for (ASTNode bc : var) bc.executor(executor);

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
                        for (ASTNode bc : var) bc.executor(executor);
                        for (ASTNode bc : function1.getBcs()) {
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
        if(CommandManager.isWebSocket) {
            for (ASTNode bc : var) bc.executor(executor);
            ArrayList<ExObject> obj = new ArrayList<>(executor.getStackList());
            executor.push(NetSocketImp.getInstance().invokeSocketFunction(lib,function,obj,executor));
        }else throw new VMRuntimeException("找不到指定库:"+lib,executor.getThread());
    }
}
