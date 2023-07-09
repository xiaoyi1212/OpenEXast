package io.openex.exe.obj;

public class ExString extends ExObject {
    String data;
    public ExString(String data){
        this.data = data;
    }
    @Override
    public String getData() {
        return data;
    }

    @Override
    public int getType() {
        return ExObject.STRING;
    }

    @Override
    public String toString() {
        return "[STRING]:"+data;
    }
}
