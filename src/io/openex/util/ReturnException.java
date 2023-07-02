package io.openex.util;

import io.openex.astvm.obj.ExObject;

public class ReturnException extends RuntimeException{
    ExObject obj;
    public ReturnException(ExObject obj){
        this.obj = obj;
    }

    public ExObject getObj() {
        return obj;
    }
}
