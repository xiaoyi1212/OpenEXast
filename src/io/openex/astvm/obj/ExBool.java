package io.openex.astvm.obj;

public class ExBool extends ExObject{
    boolean data;
    public ExBool(boolean data){
        this.data = data;
    }

    @Override
    public String getData() {
        return data+"";
    }

    @Override
    public int getType() {
        return BOOLEAN;
    }

    @Override
    public String toString() {
        return "[BOOL]:"+data;
    }
}
