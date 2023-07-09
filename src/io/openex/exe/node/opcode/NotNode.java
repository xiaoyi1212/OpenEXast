package io.openex.exe.node.opcode;

import io.openex.exe.core.Executor;
import io.openex.exe.obj.ExBool;
import io.openex.exe.obj.ExObject;
import io.openex.exe.obj.ExValue;
import io.openex.exe.obj.ExVarName;
import io.openex.exe.thread.ThreadManager;
import io.openex.util.VMRuntimeException;

public class NotNode extends OpNode {
    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        ExObject obj = executor.pop();
        obj = getVar(obj,executor);
        if(obj.getType()==ExObject.BOOLEAN){
            executor.push(new ExBool(!Boolean.parseBoolean(obj.getData())));
        }else throw new VMRuntimeException("逻辑运算时提取到未知类型",executor.getThread());
    }
    private ExObject getVar(ExObject o,Executor executor) throws VMRuntimeException {
        if(o instanceof ExVarName) {
            ExValue buf = null;
            for (ExValue v : ThreadManager.getValues()) {
                if (v.getData().equals(o.getData())) {
                    buf = v;
                    break;
                }
            }
            for (ExValue v : executor.getExecuting().getValues()) {
                if (v.getData().equals(o.getData())) {
                    buf = v;
                    break;
                }
            }
            if (buf == null) throw new VMRuntimeException("找不到指定变量:" + o.getData(), executor.getThread());

            o = buf.getVar();
        }else if(o instanceof ExValue) o = ((ExValue) o).getVar();
        return o;
    }
}
