package ex.openex.astvm.code;

import ex.openex.astvm.exe.Executor;
import ex.openex.exception.VMRuntimeException;

public interface ByteCode {
    public void executor(Executor executor)throws VMRuntimeException;
}
