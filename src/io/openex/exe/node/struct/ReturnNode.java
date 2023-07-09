package io.openex.exe.node.struct;

import io.openex.exe.node.ASTNode;
import io.openex.exe.core.Executor;
import io.openex.exe.obj.ExObject;
import io.openex.exe.obj.ExValue;
import io.openex.exe.obj.ExVarName;
import io.openex.exe.thread.ThreadManager;
import io.openex.util.ReturnException;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;

public class ReturnNode extends StructNode {
    ArrayList<ASTNode> bcs;
    public ReturnNode(ArrayList<ASTNode> bcs){
        this.bcs = bcs;
    }

    @Override
    public void executor(Executor executor) throws VMRuntimeException {

        for(ASTNode b:bcs)b.executor(executor);

        ExObject o = executor.pop();

        if(o instanceof ExVarName){
            ExValue buf = null;
            for(ExValue v: ThreadManager.getValues()){
                if(v.getData().equals(o.getData())){
                    buf = v;
                    break;
                }
            }
            for(ExValue v:executor.getExecuting().getValues()){
                if(v.getData().equals(o.getData())){
                    buf = v;
                    break;
                }
            }
            if(buf == null)throw new VMRuntimeException("找不到指定变量:"+o.getData(),executor.getThread());

            if(buf.getType()== ExObject.ARRAY){
                o = buf;
            }else o = buf.getVar();
        }

        throw new ReturnException(o);
    }
}
