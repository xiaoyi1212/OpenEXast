package io.openex.exe.node.opcode;

import io.openex.exe.core.Executor;
import io.openex.exe.obj.ExObject;
import io.openex.exe.obj.ExValue;
import io.openex.util.VMRuntimeException;

public class MovNode extends OpNode {
    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        ExObject o = executor.pop();
        ExObject o1 = executor.pop();

        o = getValue(o);

        if(o1.getType()!=ExObject.VALUE)throw new VMRuntimeException("The operation type is incorrect",executor.getThread());
        ExValue value = (ExValue) o1;
        value.setVar(o);
    }

    public ExObject getValue(ExObject object){
        if(object instanceof ExValue){
            return ((ExValue) object).getVar();
        }
        return object;
    }
}
