package ex.openex.astvm.code.struct;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.exe.Executor;
import ex.openex.astvm.obj.ExValue;
import ex.openex.astvm.thread.ThreadManager;
import ex.openex.exception.VMRuntimeException;

import java.util.ArrayList;

public class LoadVarCode extends StructCode{
    String name;
    String text;
    int type;
    ArrayList<ByteCode> bcs;
    public LoadVarCode(String name,String text,int type,ArrayList<ByteCode> bcs){
        this.name = name;
        this.text = text;
        this.bcs = bcs;
        this.type = type;
    }

    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        ExValue v = new ExValue(name,text,type);

        for(ByteCode b:bcs)b.executor(executor);
        v.setVar(executor.pop());

        if(type==1){
            executor.getExecuting().getValues().add(v);
        }else if(type == 0){
            ThreadManager.getValues().add(v);
        }
    }
}
