package io.openex.exe.obj;

public class ExNull extends ExObject{
    @Override
    public String getData() {
        return "null";
    }

    @Override
    public int getType() {
        return NULL;
    }

    @Override
    public String toString() {
        return "[NULL]";
    }
}
