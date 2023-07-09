package io.openex.exe.lib;

import io.openex.exe.core.Executor;
import io.openex.exe.obj.*;
import io.openex.exe.thread.ThreadManager;
import io.openex.util.VMRuntimeException;
import io.openex.exe.lib.util.ObjectSize;

import java.util.ArrayList;

public class Type implements RuntimeLibrary{

    ArrayList<RuntimeFunction> functions;
    public Type(){
        functions = new ArrayList<>();
        functions.add(new SizeOf());
        functions.add(new Typeof());
        functions.add(new ToString());
        functions.add(new ToDouble());
        functions.add(new ToBoolean());
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

    private static class ToString implements RuntimeFunction{
        @Override
        public int getVarNum() {
            return 1;
        }
        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            return new ExString(vars.get(0).getData());
        }
        @Override
        public String getName() {
            return "to_string";
        }
    }
    private static class ToDouble implements RuntimeFunction{
        @Override
        public int getVarNum() {
            return 1;
        }
        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            try {
                ExObject o = vars.get(0);
                if (o.getType() == ExObject.ARRAY)
                    throw new VMRuntimeException("Cannot convert an array type to a double type.", executor.getThread());
                if (o.getType() == ExObject.BOOLEAN) {
                    if (o.getData().equals("true")) return new ExDouble(1.0);
                    else return new ExDouble(0.0);
                }
                return new ExDouble(Double.parseDouble(o.getData()));
            }catch (Exception e){
                throw new VMRuntimeException(e.getLocalizedMessage(),executor.getThread());
            }
        }
        @Override
        public String getName() {
            return "to_double";
        }
    }
    private static class ToInteger implements RuntimeFunction{
        @Override
        public int getVarNum() {
            return 1;
        }
        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            try {
                ExObject o = vars.get(0);
                if (o.getType() == ExObject.ARRAY)
                    throw new VMRuntimeException("Cannot convert an array type to a integer type.", executor.getThread());
                if (o.getType() == ExObject.BOOLEAN) {
                    if (o.getData().equals("true")) return new ExInt(1);
                    else return new ExInt(0);
                }
                return new ExInt(Integer.parseInt(o.getData()));
            }catch (Exception e){
                throw new VMRuntimeException(e.getLocalizedMessage(),executor.getThread());
            }
        }
        @Override
        public String getName() {
            return "to_integer";
        }
    }
    private static class ToBoolean implements RuntimeFunction{
        @Override
        public int getVarNum() {
            return 1;
        }
        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            try {
                ExObject o = vars.get(0);
                if (o.getType() == ExObject.ARRAY)
                    throw new VMRuntimeException("Cannot convert an array type to a double type.", executor.getThread());

                if (o.getType() == ExObject.DOUBLE) {
                    double d = Double.parseDouble(o.getData());
                    if(d > 0)return new ExBool(true);
                    else return new ExBool(false);
                }else if(o.getType()==ExObject.INTEGER){
                    int i = Integer.parseInt(o.getData());
                    if(i > 0)return new ExBool(true);
                    else return new ExBool(false);
                }
                return new ExBool(Boolean.parseBoolean(o.getData()));
            }catch (Exception e){
                throw new VMRuntimeException(e.getLocalizedMessage(),executor.getThread());
            }
        }
        @Override
        public String getName() {
            return "to_bool";
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
