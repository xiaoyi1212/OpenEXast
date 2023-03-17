package ex.openex.exception;

import ex.openex.Main;
import ex.openex.compile.LexicalAnalysis;

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
