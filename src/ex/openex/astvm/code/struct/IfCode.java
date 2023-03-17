package ex.openex.astvm.code.struct;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.exe.Executor;
import ex.openex.astvm.obj.ExObject;
import ex.openex.exception.VMRuntimeException;

import java.util.ArrayList;

public class IfCode extends StructCode{
    ArrayList<ByteCode> bool,group,else_group;

    public IfCode(ArrayList<ByteCode> bool,ArrayList<ByteCode> group,ArrayList<ByteCode> else_group){
        this.bool = bool;
        this.group = group;
        this.else_group = else_group;
    }
    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        for(ByteCode b:bool)b.executor(executor);
        ExObject o = executor.pop();

        if(Boolean.parseBoolean(o.getData())){
            for(ByteCode b:group)b.executor(executor);
        }else {
            for(ByteCode b:else_group)b.executor(executor);
        }
    }
}
