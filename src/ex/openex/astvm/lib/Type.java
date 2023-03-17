package ex.openex.astvm.lib;

import ex.openex.astvm.exe.Executor;
import ex.openex.astvm.obj.*;
import ex.openex.astvm.thread.ThreadManager;
import ex.openex.exception.VMRuntimeException;
import ex.openex.astvm.lib.util.ObjectSize;

import java.util.ArrayList;

public class Type implements RuntimeLibrary{

    ArrayList<RuntimeFunction> functions;
    public Type(){
        functions = new ArrayList<>();
        functions.add(new SizeOf());
        functions.add(new Typeof());
    }
    @Override
    public ArrayList<RuntimeFunction> functions() {
        return functions;
    }

    @Override
    public java.lang.String getName() {
        return "type";
    }

    private static class Typeof implements RuntimeFunction{

        @Override
        public int getVarNum() {
            return 1;
        }

        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            ExObject object = vars.get(0);
            String type = "unknown";
            if(object instanceof ExVarName){
                ExValue buf = null;
                for(ExValue v: ThreadManager.getValues()){
                    if(v.getData().equals(object.getData())){
                        buf = v;
                        break;
                    }
                }
                for(ExValue v:executor.getExecuting().getValues()){
                    if(v.getData().equals(object.getData())){
                        buf = v;
                        break;
                    }
                }
                if(buf == null)throw new VMRuntimeException("找不到指定变量:"+object.getData(),executor.getThread());

                if(buf.getType()==ExObject.ARRAY){
                    object = buf;
                }else object = buf.getVar();
            }

            switch (object.getType()){
                case ExObject.BOOLEAN -> type = "BOOL";
                case ExObject.STRING -> type = "STRING";
                case ExObject.INTEGER -> type = "INT";
                case ExObject.DOUBLE -> type = "DOUBLE";
                case ExObject.ARRAY -> type = "ARRAY";
                case ExObject.NULL -> type = "NULL";
            }

            return new ExString(type);
        }

        @Override
        public java.lang.String getName() {
            return "typeof";
        }
    }

    private static class SizeOf implements RuntimeFunction{
        @Override
        public int getVarNum() {
            return 1;
        }

        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            return new ExString(ObjectSize.getSize(vars.get(0),executor));
        }

        @Override
        public java.lang.String getName() {
            return "sizeof";
        }
    }
}
