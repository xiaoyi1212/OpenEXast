package io.openex.exe.core;

import io.openex.exe.node.ASTNode;
import io.openex.exe.obj.ExValue;

import java.util.ArrayList;

public class Script {
    ArrayList<ASTNode> bcs;
    ArrayList<ExValue> values;
    String invoke_name;
    String filename;

    public Script(String invoke_name,String filename,ArrayList<ASTNode> bcs){
        this.invoke_name = invoke_name;
        this.filename= filename;
        this.bcs = bcs;
        this.values = new ArrayList<>();
    }

    public ArrayList<ExValue> getValues() {
        return values;
    }

    public ArrayList<ASTNode> getBcs() {
        return bcs;
    }

    public String getFilename() {
        return filename;
    }

    public String getInvoke_name() {
        return invoke_name;
    }
}
