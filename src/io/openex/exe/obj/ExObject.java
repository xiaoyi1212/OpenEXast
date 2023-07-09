package io.openex.exe.obj;

public abstract class ExObject {
    public abstract String getData();
    public abstract int getType();
    public static final int STRING = 0,INTEGER = 1,DOUBLE = 2,BOOLEAN = 3,NULL = 4,VALUE = 5,ARRAY = 6;
}
