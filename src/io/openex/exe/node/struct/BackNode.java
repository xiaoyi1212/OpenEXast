package io.openex.exe.node.struct;

import io.openex.exe.node.ASTNode;
import io.openex.exe.core.Executor;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;

public class BackNode extends StructNode {
    ArrayList<ASTNode> b;
    public BackNode(ArrayList<ASTNode> b){
        this.b = b;
    }

    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        for(ASTNode bb:b)bb.executor(executor);
    }
}
