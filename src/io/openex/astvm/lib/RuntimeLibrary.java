package io.openex.astvm.lib;

import io.openex.astvm.exe.Executor;
import io.openex.astvm.obj.ExObject;
import io.openex.astvm.obj.ExValue;
import io.openex.astvm.obj.ExVarName;
import io.openex.astvm.thread.ThreadManager;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;

public interface RuntimeLibrary {
    public static interface RuntimeFunction{
        public int getVarNum();
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException;
        public java.lang.String getName();

        public default void exe(Executor executor) throws VMRuntimeException {
            try {
                ArrayList<ExObject> obj = new ArrayList<>();
                for (int i = 0; i < getVarNum(); i++){
                    ExObject o = executor.pop();
                    ExValue buf = null;

                    if(o instanceof ExVarName){
                        for(ExValue v: ThreadManager.getValues()){
                            if(v.getData().equals(o.getData())){
                                buf = v;
                                break;
                            }
                        }

                        for(ExValue v:executor.getExecuting().getValues()){
                            if(v.getData().equals(o.getData())){
                                buf = v;
                                break;
                            }
                        }
                        if(buf == null)throw new VMRuntimeException("找不到指定变量:"+o.getData(),executor.getThread());

                        if(buf.getType()==ExObject.ARRAY){
                            o = buf;
                        }else o = buf.getVar();
                    }
                    obj.add(o);
                }
                Collections.reverse(obj);
                executor.push(invoke(obj, executor));
            }catch (EmptyStackException e){
                throw new VMRuntimeException("获取函数形参时发生错误,可能传入参数个数不匹配,实际参数为("+getVarNum()+"个)",executor.getThread());
            }
        }
    }
    public ArrayList<RuntimeFunction> functions();
    public java.lang.String getName();
}
