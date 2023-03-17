package ex.openex.astvm.code.opcode;

import ex.openex.astvm.exe.Executor;
import ex.openex.astvm.obj.ExObject;
import ex.openex.exception.VMRuntimeException;

public class PushCode extends OpCode{
    ExObject obj;
    public PushCode(ExObject obj){
        this.obj = obj;
    }

    @Override
    public String toString() {
        return "push "+obj;
    }

    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        executor.push(obj);
    }
}
