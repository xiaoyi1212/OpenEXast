package io.openex.astvm.code.struct;

import io.openex.astvm.code.ByteCode;
import io.openex.astvm.exe.Executor;
import io.openex.astvm.obj.ExObject;
import io.openex.astvm.obj.ExValue;
import io.openex.astvm.obj.ExVarName;
import io.openex.astvm.thread.ThreadManager;
import io.openex.util.ReturnException;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;

public class ReturnCode extends StructCode{
    ArrayList<ByteCode> bcs;
    public ReturnCode(ArrayList<ByteCode> bcs){
        this.bcs = bcs;
    }

    @Override
    public void executor(Executor executor) throws VMRuntimeException {

        for(ByteCode b:bcs)b.executor(executor);

        ExObject o = executor.pop();

        if(o instanceof ExVarName){
            ExValue buf = null;
            for(ExValue v: ThreadManager.getValues()){
                if(v.getData().equals(o.getData())){
                    buf = v;
                    break;
                }
            }
            for(ExValue v:executor.getExecuting().getValues()){
                if(v.getData().equals(o.getData())){
                    buf = v;
                    break;
                }
            }
            if(buf == null)throw new VMRuntimeException("找不到指定变量:"+o.getData(),executor.getThread());

            if(buf.getType()== ExObject.ARRAY){
                o = buf;
            }else o = buf.getVar();
        }

        throw new ReturnException(o);
    }
}
