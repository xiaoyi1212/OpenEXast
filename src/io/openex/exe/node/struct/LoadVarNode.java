package io.openex.exe.node.struct;

import io.openex.exe.node.ASTNode;
import io.openex.exe.core.Executor;
import io.openex.exe.obj.ExValue;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;

public class LoadVarNode extends StructNode {
    String name;
    String text;
    int type;
    ArrayList<ASTNode> bcs;
    public LoadVarNode(String name, String text, int type, ArrayList<ASTNode> bcs){
        this.name = name;
        this.text = text;
        this.bcs = bcs;
        this.type = type;
    }

    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        ExValue v = new ExValue(name,text,type);

        for(ASTNode b:bcs)b.executor(executor);
        v.setVar(executor.pop());


        executor.getExecuting().getValues().add(v);
    }

    @Override
    public String toString() {
        return "LOAD:"+bcs;
    }
}
