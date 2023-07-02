package io.openex.astvm.code.struct;

import io.openex.astvm.code.ByteCode;
import io.openex.astvm.exe.Executor;
import io.openex.util.VMRuntimeException;

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