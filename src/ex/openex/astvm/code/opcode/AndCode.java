package ex.openex.astvm.code.opcode;

import ex.openex.astvm.exe.Executor;
import ex.openex.astvm.obj.ExBool;
import ex.openex.astvm.obj.ExObject;
import ex.openex.exception.VMRuntimeException;

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
