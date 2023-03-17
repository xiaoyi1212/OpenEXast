package ex.openex.exception;

import ex.openex.astvm.obj.ExObject;

public class ReturnException extends RuntimeException{
    ExObject obj;
    public ReturnException(ExObject obj){
        this.obj = obj;
    }

    public ExObject getObj() {
        return obj;
    }
}
