package ex.openex.astvm.code.struct;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.exe.Executor;
import ex.openex.exception.VMRuntimeException;

import java.util.ArrayList;

public class GroupByteCode extends StructCode {
    ArrayList<ByteCode> bc;
    public GroupByteCode(ArrayList<ByteCode> bc){
        this.bc = bc;
    }

    @Override
    public String toString() {
        return bc.toString();
    }

    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        for(ByteCode bcc:bc)bcc.executor(executor);
    }
}
