package io.openex.astvm.code.opcode;

import io.openex.astvm.exe.Executor;
import io.openex.astvm.obj.ExBool;
import io.openex.astvm.obj.ExObject;
import io.openex.astvm.obj.ExValue;
import io.openex.astvm.obj.ExVarName;
import io.openex.astvm.thread.ThreadManager;
import io.openex.util.VMRuntimeException;

public class EquCode extends OpCode{
    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        ExObject obj = valueof(executor.pop(),executor);
        ExObject obj1 = valueof(executor.pop(),executor);
        if(obj.getType()==obj1.getType()){
            executor.push(new ExBool(obj.getData().equals(obj1.getData())));
        }else executor.push(new ExBool(false));
    }

    private ExObject valueof(ExObject o,Executor executor) throws VMRuntimeException {
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

            if(buf.getType()==ExObject.ARRAY){
                o = buf;
            }else o = buf.getVar();
            return o;
        }else return o;
    }
}
