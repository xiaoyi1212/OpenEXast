package io.openex.astvm.code.opcode;

import io.openex.astvm.exe.Executor;
import io.openex.astvm.obj.ExBool;
import io.openex.astvm.obj.ExObject;
import io.openex.astvm.obj.ExValue;
import io.openex.astvm.obj.ExVarName;
import io.openex.astvm.thread.ThreadManager;
import io.openex.util.VMRuntimeException;

public class OrCode extends OpCode{
    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        ExObject obj = valueof(executor.pop(),executor);
        ExObject obj1 = valueof(executor.pop(),executor);
        if(obj1.getType()==ExObject.BOOLEAN&&obj.getType()==ExObject.BOOLEAN){
            executor.push(new ExBool(Boolean.parseBoolean(obj.getData())||Boolean.parseBoolean(obj1.getData())));
        }else throw new VMRuntimeException("逻辑运算时提取到未知类型",executor.getThread());
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
