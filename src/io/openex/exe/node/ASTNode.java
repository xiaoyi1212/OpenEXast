package io.openex.exe.node;

import io.openex.exe.core.Executor;
import io.openex.util.VMRuntimeException;

public interface ASTNode {
    public void executor(Executor executor)throws VMRuntimeException;
}
