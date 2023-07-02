package io.openex.astvm.code.opcode;

import io.openex.astvm.exe.Executor;
import io.openex.astvm.obj.ExBool;
import io.openex.astvm.obj.ExObject;
import io.openex.util.VMRuntimeException;

public class AndCode extends OpCode{
    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        ExObject obj = executor.pop();
        ExObject obj1 = executor.pop();
        if(obj1.getType()==ExObject.BOOLEAN&&obj.getType()==ExObject.BOOLEAN){
            executor.push(new ExBool(Boolean.parseBoolean(obj.getData())&&Boolean.parseBoolean(obj1.getData())));
        }else throw new VMRuntimeException("逻辑运算时提取到未知类型",executor.getThread());
    }
}
