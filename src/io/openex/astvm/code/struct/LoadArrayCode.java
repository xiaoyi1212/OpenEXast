package io.openex.astvm.code.struct;

import io.openex.astvm.code.ByteCode;
import io.openex.astvm.exe.Executor;
import io.openex.astvm.obj.ExArray;
import io.openex.astvm.obj.ExObject;
import io.openex.astvm.thread.ThreadManager;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;

public class LoadArrayCode extends StructCode{
    String name;
    ArrayList<GroupByteCode> g;
    int type;
    int size;

    public LoadArrayCode(String name,ArrayList<GroupByteCode> g,int type,int size){
        this.name = name;
        this.g = g;
        this.type = type;
        this.size = size;
    }

    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        ArrayList<ExObject> objs = new ArrayList<>();
        for(GroupByteCode gg:g){
            for(ByteCode bb:gg.bc)bb.executor(executor);
            objs.add(executor.pop());
        }
        ExArray array;
        if(objs.size()==0){
            if(size > -1)array = new ExArray(name,size);
            else array = new ExArray(name,0);
        } else array = new ExArray(name,objs);

        if(type==1)executor.getExecuting().getValues().add(array);
        else if(type == 0) ThreadManager.getValues().add(array);
    }
}
