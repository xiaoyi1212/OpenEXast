package io.openex.util;

import io.openex.Main;
import io.openex.compile.LexicalAnalysis;

public class CompileException extends RuntimeException{
    public CompileException(String message,String filename){
        Main.getOutput().error("CompileError:"+message+"\n\t" +
                "Filename:"+filename+"\n\t" +
                "FileLine:unknown\n\t" +
                "Code:unknown");
    }
    public CompileException(String message, LexicalAnalysis.Token token,String filename){
        Main.getOutput().error("CompileError:"+message+"\n\t" +
                "Filename:"+filename+"\n\t" +
                "FileLine:"+token.getLine()+"\n\t" +
                "Code:"+token.getData()+"\n\t" +
                "CompilerVersion:"+Main.compile_version);
    }
}
