package io.openex.exe.node.struct;

import io.openex.exe.node.ASTNode;
import io.openex.exe.core.Executor;
import io.openex.exe.obj.ExObject;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;

public class IfNode extends StructNode {
    ArrayList<ASTNode> bool,group,else_group;

    public IfNode(ArrayList<ASTNode> bool, ArrayList<ASTNode> group, ArrayList<ASTNode> else_group){
        this.bool = bool;
        this.group = group;
        this.else_group = else_group;
    }
    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        for(ASTNode b:bool)b.executor(executor);
        ExObject o = executor.pop();

        if(Boolean.parseBoolean(o.getData())){
            for(ASTNode b:group)b.executor(executor);
        }else {
            for(ASTNode b:else_group)b.executor(executor);
        }
    }
}
