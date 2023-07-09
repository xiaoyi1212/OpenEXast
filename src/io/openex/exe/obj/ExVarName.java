package io.openex.exe.obj;

public class ExVarName extends ExObject{
    String name;
    public ExVarName(String name) {
        this.name = name;
    }

    @Override
    public String getData() {
        return name;
    }

    @Override
    public String toString() {
        return "[VALUE]:"+name;
    }

    @Override
    public int getType() {
        return ExObject.VALUE;
    }
}
