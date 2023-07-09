package io.openex.exe.node.opcode;

import io.openex.exe.core.Executor;
import io.openex.exe.obj.ExObject;
import io.openex.util.VMRuntimeException;

public class PushNode extends OpNode {
    ExObject obj;
    public PushNode(ExObject obj){
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
