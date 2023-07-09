package io.openex.exe.obj;

public class ExValue extends ExObject{
    String name,text;
    int type; // 1 local| 0 global
    ExObject var;
    public ExValue(String name,String text,int type){
        this.name = name;
        this.type = type;
        this.text = text;
        this.var = new ExNull();
    }
    public ExValue(){
    }

    public void setVar(ExObject var) {
        this.var = var;
    }

    public ExObject getVar() {
        return var;
    }

    @Override
    public String getData() {
        return name;
    }

    @Override
    public int getType() {
        return ExObject.VALUE;
    }

    @Override
    public String toString() {
        return "Value:"+name+"|Var:"+var;
    }
}
