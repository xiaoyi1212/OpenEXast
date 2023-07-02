package io.openex.astvm.code.struct;

import io.openex.astvm.code.ByteCode;
import io.openex.astvm.exe.Executor;
import io.openex.astvm.obj.ExObject;
import io.openex.astvm.obj.ExValue;
import io.openex.astvm.thread.ThreadManager;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;

public class MovVarCode extends StructCode{

    String name;
    ArrayList<ByteCode> bcs;
    public MovVarCode(String name,ArrayList<ByteCode> bcs){
        this.name = name;
        this.bcs = bcs;
    }


    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        ExObject buf = null;
        for(ByteCode b:bcs)b.executor(executor);
        for(ExValue v: ThreadManager.getValues()){
            if(v.getData().equals(name)){
                buf = v;
                v.setVar(executor.pop());
                break;
            }
        }
        for(ExValue v:executor.getExecuting().getValues()){
            if(v.getData().equals(name)){
                buf = v;
                v.setVar(executor.pop());
                break;
            }
        }
        if(buf == null)throw new VMRuntimeException("找不到指定变量:"+name,executor.getThread());
    }
}
