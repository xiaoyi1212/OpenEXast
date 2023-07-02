package io.openex.astvm.code;

import io.openex.astvm.exe.Executor;
import io.openex.util.VMRuntimeException;

public interface ByteCode {
    public void executor(Executor executor)throws VMRuntimeException;
}
