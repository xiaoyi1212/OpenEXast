package io.openex.exe.obj;

import io.openex.exe.core.Executor;
import io.openex.exe.thread.ThreadManager;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;
import java.util.Arrays;

public class ExArray extends ExValue{
    String name;
    ExObject[] objs;

    public ExArray(String name,int size){
        this.name = name;
        this.objs = new ExObject[size];
    }
    public ExArray(String name, ArrayList<ExObject> objs){
        this.name = name;
        this.objs = objs.toArray(ExObject[]::new);
    }
    public ExArray(String name,ExObject[] objs){
        this.name = name;
        this.objs = objs;
    }

    public void setObj(int index,ExObject obj){
        objs[index] = obj;
    }

    public ExObject[] getObjs() {
        return objs;
    }

    public int length(){
        return objs.length;
    }

    public ExObject getObj(int index, Executor executor) throws VMRuntimeException {
        ExObject obj = objs[index];
        if(obj instanceof ExVarName){
            ExValue buf = null;
            for(ExValue v: ThreadManager.getValues()){
                if(v.getData().equals(obj.getData())){
                    buf = v;
                    break;
                }
            }
            for(ExValue v:executor.getExecuting().getValues()){
                if(v.getData().equals(obj.getData())){
                    buf = v;
                    break;
                }
            }
            if(buf == null)throw new VMRuntimeException("找不到指定变量:"+obj.getData(),executor.getThread());

            if(buf.getType()==ExObject.ARRAY){
                obj = buf;
            }else obj = buf.getVar();
        }
        return obj;
    }

    @Override
    public String toString(){
        return Arrays.toString(objs);
    }

    @Override
    public String getData() {
        return name;
    }

    @Override
    public int getType() {
        return ExObject.ARRAY;
    }
}
