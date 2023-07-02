package io.openex.astvm.code.opcode;

import io.openex.astvm.exe.Executor;
import io.openex.astvm.obj.ExBool;
import io.openex.astvm.obj.ExObject;
import io.openex.util.VMRuntimeException;

public class NotCode extends OpCode{
    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        ExObject obj = executor.pop();
        if(obj.getType()==ExObject.BOOLEAN){
            executor.push(new ExBool(!Boolean.parseBoolean(obj.getData())));
        }else throw new VMRuntimeException("逻辑运算时提取到未知类型",executor.getThread());
    }
}
