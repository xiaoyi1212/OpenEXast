package io.openex.astvm.code.opcode;

import io.openex.astvm.exe.Executor;
import io.openex.astvm.obj.ExObject;
import io.openex.util.VMRuntimeException;

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
