package io.openex.astvm.lib;

import io.openex.astvm.code.ByteCode;
import io.openex.astvm.obj.ExValue;

import java.util.ArrayList;

public class Function extends ExValue {
    String lib;
    String name;
    ArrayList<ByteCode> bcs;

    public Function(String lib, String name, ArrayList<ByteCode> bcs){
        this.lib = lib;
        this.name = name;
        this.bcs = bcs;
    }

    public ArrayList<ByteCode> getBcs() {
        return bcs;
    }

    public String getName() {
        return name;
    }

    public String getLib() {
        return lib;
    }

}
