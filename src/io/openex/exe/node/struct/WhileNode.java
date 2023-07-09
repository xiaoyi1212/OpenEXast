package io.openex.exe.node.struct;

import io.openex.exe.node.ASTNode;
import io.openex.exe.core.Executor;
import io.openex.exe.obj.ExObject;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;

public class WhileNode extends StructNode {
    ArrayList<ASTNode> bool,group;
    public WhileNode(ArrayList<ASTNode> bool, ArrayList<ASTNode> group){
        this.bool = bool;
        this.group = group;
    }
    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        while (true){
            for(ASTNode b:bool){
                b.executor(executor);
            }


            ExObject obj = executor.pop();

            if(!Boolean.parseBoolean(obj.getData()))break;

            for(ASTNode b:group){
                if(b instanceof BackNode)return;

                b.executor(executor);
            }
        }
    }
}
