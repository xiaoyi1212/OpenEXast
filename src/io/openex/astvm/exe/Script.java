package io.openex.astvm.exe;

import io.openex.astvm.code.ByteCode;
import io.openex.astvm.obj.ExValue;

import java.util.ArrayList;

public class Script {
    ArrayList<ByteCode> bcs;
    ArrayList<ExValue> values;
    String invoke_name;
    String filename;

    public Script(String invoke_name,String filename,ArrayList<ByteCode> bcs){
        this.invoke_name = invoke_name;
        this.filename= filename;
        this.bcs = bcs;
        this.values = new ArrayList<>();
    }

    public ArrayList<ExValue> getValues() {
        return values;
    }

    public ArrayList<ByteCode> getBcs() {
        return bcs;
    }

    public String getFilename() {
        return filename;
    }

    public String getInvoke_name() {
        return invoke_name;
    }
}
