package io.openex.util;

import io.openex.Main;
import io.openex.exe.thread.ThreadTask;

public class VMRuntimeException extends Exception{
    public VMRuntimeException(String message, ThreadTask task){
        Main.getOutput().error("ScriptRuntimeError:"+message+"\n\t" +
                "ThreadName:"+task.getName()+"\n\t" +
                "FileName:"+task.getFilename()+"\n\t" +
                "RuntimeVersion:"+Main.runtime_version);
    }
}
