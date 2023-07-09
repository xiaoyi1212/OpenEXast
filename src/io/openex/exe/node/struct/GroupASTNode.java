package io.openex.exe.node.struct;

import io.openex.exe.node.ASTNode;
import io.openex.exe.core.Executor;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;

public class GroupASTNode extends StructNode {
    ArrayList<ASTNode> bc;
    public GroupASTNode(ArrayList<ASTNode> bc){
        this.bc = bc;
    }
    public GroupASTNode(ASTNode bc){
        this.bc = new ArrayList<>();
        this.bc.add(bc);
    }

    @Override
    public String toString() {
        return bc.toString();
    }

    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        for(ASTNode bcc:bc)bcc.executor(executor);
    }
}
