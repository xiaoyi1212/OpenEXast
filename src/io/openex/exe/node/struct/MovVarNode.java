package io.openex.exe.node.struct;

import io.openex.exe.core.Executor;
import io.openex.exe.obj.ExValue;
import io.openex.util.VMRuntimeException;

public class MovVarNode extends StructNode {

    String name;
    public MovVarNode(String name){
        this.name = name;
    }


    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        ExValue ex = null;
        for(ExValue e:executor.getExecuting().getValues()){
            if(e.getData().equals(name)){
                ex = e;
            }
        }
        if(ex == null)throw new VMRuntimeException("找不到指定变量:"+name,executor.getThread());
        executor.push(ex);
    }
}
