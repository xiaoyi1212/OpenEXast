package io.openex.exe.node.opcode;

import io.openex.exe.core.Executor;
import io.openex.exe.obj.*;
import io.openex.exe.thread.ThreadManager;
import io.openex.util.VMRuntimeException;

public class AddNode extends OpNode {

    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        ExObject t1 = executor.pop();
        ExObject t2 = executor.pop();

        t1 = getVar(t1,executor);
        t2 = getVar(t2,executor);

        if(t1.getType()==ExObject.STRING||t2.getType()==ExObject.STRING||t1.getType()==ExObject.BOOLEAN||t2.getType()==ExObject.BOOLEAN){
            executor.push(new ExString(t2.getData()+t1.getData()));
        }
        else if(t1.getType()==ExObject.DOUBLE||t2.getType()==ExObject.DOUBLE){
            executor.push(new ExDouble(Double.parseDouble(t2.getData())+Double.parseDouble(t1.getData())));
        }
        else if(t1.getType()==ExObject.NULL||t2.getType()==ExObject.NULL)throw new VMRuntimeException("加法运算时发生空指针异常",executor.getThread());
        else{
            executor.push(new ExInt(Integer.parseInt(t2.getData())+Integer.parseInt(t1.getData())));
        }
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
