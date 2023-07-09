package io.openex.exe.lib;

import io.openex.exe.core.Executor;
import io.openex.exe.obj.*;
import io.openex.util.VMRuntimeException;

import java.util.ArrayList;

public class StringLib implements RuntimeLibrary{
    ArrayList<RuntimeFunction> functions;

    public StringLib(){
        functions = new ArrayList<>();
        functions.add(new Split());
        functions.add(new IndexOf());
        functions.add(new Contains());
    }

    @Override
    public ArrayList<RuntimeFunction> functions() {
        return functions;
    }

    @Override
    public String getName() {
        return "string";
    }

    private static class Contains implements RuntimeFunction{

        @Override
        public int getVarNum() {
            return 2;
        }

        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            ExObject string = vars.get(0);
            ExObject sp = vars.get(1);
            return new ExBool(string.getData().contains(sp.getData()));
        }

        @Override
        public String getName() {
            return "contains";
        }
    }

    private static class IndexOf implements RuntimeFunction{
        @Override
        public int getVarNum() {
            return 2;
        }
        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            ExObject string = vars.get(0);
            ExObject sp = vars.get(1);
            return new ExInt(string.getData().indexOf(sp.getData()));
        }
        @Override
        public String getName() {
            return "indexof";
        }
    }

    private static class Split implements RuntimeFunction{
        @Override
        public int getVarNum() {
            return 2;
        }
        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            ExObject string = vars.get(0);
            ExObject sp = vars.get(1);
            ArrayList<ExObject> o = new ArrayList<>();
            for(String s:string.getData().split(sp.getData())) o.add(new ExString(s));
            return new ExArray("<STRING>",o);
        }
        @Override
        public String getName() {
            return "split";
        }
    }
}
