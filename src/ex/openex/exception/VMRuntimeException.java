package ex.openex.exception;

import ex.openex.Main;
import ex.openex.astvm.thread.ThreadTask;

public class VMRuntimeException extends Exception{
    public VMRuntimeException(String message, ThreadTask task){
        Main.getOutput().error("ScriptRuntimeError:"+message+"\n\t" +
                "ThreadName:"+task.getName()+"\n\t" +
                "RuntimeVersion:"+Main.runtime_version);
        super.printStackTrace();
    }
}
