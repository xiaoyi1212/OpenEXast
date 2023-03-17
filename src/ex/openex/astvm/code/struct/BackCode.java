package ex.openex.astvm.code.struct;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.exe.Executor;
import ex.openex.exception.VMRuntimeException;

import java.util.ArrayList;

public class BackCode extends StructCode{
    ArrayList<ByteCode> b;
    public BackCode(ArrayList<ByteCode> b){
        this.b = b;
    }

    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        for(ByteCode bb:b)bb.executor(executor);
    }
}
