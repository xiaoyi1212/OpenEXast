package ex.openex.astvm.lib;

import ex.openex.astvm.exe.Executor;
import ex.openex.astvm.obj.ExBool;
import ex.openex.astvm.obj.ExNull;
import ex.openex.astvm.obj.ExObject;
import ex.openex.astvm.obj.ExString;
import ex.openex.exception.VMRuntimeException;

import java.io.*;
import java.util.ArrayList;

public class File implements RuntimeLibrary{
    ArrayList<RuntimeFunction> functions;
    public File(){
        functions = new ArrayList<>();
        functions.add(new ReadFile());
        functions.add(new WriteFile());
        functions.add(new ExistsFile());
    }

    @Override
    public ArrayList<RuntimeFunction> functions() {
        return functions;
    }

    @Override
    public java.lang.String getName() {
        return "file";
    }

    private static class ExistsFile implements RuntimeFunction{

        @Override
        public int getVarNum() {
            return 1;
        }

        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            ExObject name = vars.get(0);
            return new ExBool(new java.io.File(name.getData()).exists());
        }

        @Override
        public java.lang.String getName() {
            return "exists";
        }
    }

    private static class WriteFile implements RuntimeFunction{
        @Override
        public int getVarNum() {
            return 2;
        }

        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            ExObject name = vars.get(0);
            ExObject data = vars.get(1);
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(name.getData()))){
                writer.write(data.getData());
                return new ExBool(true);
            }catch (IOException e){
                return new ExBool(false);
            }
        }

        @Override
        public java.lang.String getName() {
            return "write";
        }
    }

    private static class ReadFile implements RuntimeFunction{
        @Override
        public int getVarNum() {
            return 1;
        }

        @Override
        public ExObject invoke(ArrayList<ExObject> vars, Executor executor) throws VMRuntimeException {
            ExObject obj = vars.get(0);
            StringBuilder sb = new StringBuilder();
            String line;
            try(BufferedReader reader = new BufferedReader(new FileReader(obj.getData()))){
                while ((line = reader.readLine())!=null)sb.append(line).append("\n");
                return new ExString(sb.toString());
            }catch (FileNotFoundException e){
                return new ExNull();
            }catch (IOException e){
                throw new VMRuntimeException("[FILE]:读取文件时发生错误",executor.getThread());
            }
        }

        @Override
        public java.lang.String getName() {
            return "read";
        }
    }
}
