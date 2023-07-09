package io.openex.compile;

import io.openex.CompileManager;
import io.openex.exe.node.ASTNode;
import io.openex.exe.core.Script;
import io.openex.exe.thread.ThreadTask;
import io.openex.compile.parser.BaseParser;
import io.openex.compile.parser.Parser;

import java.io.EOFException;
import java.util.ArrayList;

public class Compiler {
    String filename;
    ArrayList<ASTNode> bcs;
    ArrayList<String> libname;
    public ArrayList<String> value_names;

    public ArrayList<ASTNode> getBcs() {
        return bcs;
    }

    public ArrayList<String> getLibnames() {
        return libname;
    }

    public Compiler(String filename){
        this.filename = filename;
        this.libname = new ArrayList<>();
        this.value_names = new ArrayList<>();
        libname.add(filename.split("\\.")[0]);
        this.bcs = new ArrayList<>();
    }

    public ArrayList<String> getValueNames() {
        return value_names;
    }

    public void compile(ThreadTask task){
        LexicalAnalysis al = new LexicalAnalysis(CompileManager.getFileData(filename),filename);
        ArrayList<Token> t = new ArrayList<>(),o = new ArrayList<>();
        for(Token b:al.getTokens()) {
            if (b.type == Token.LINE) continue;
            t.add(b);
        }

        for (Token b:t)
            if(b.type!=Token.TEXT)o.add(b);

        Parser parser =new Parser(o, al.file_name);
        try {
            while (true){
                BaseParser bp = parser.getParser(this);
                bcs.add(bp.eval(parser,this));
            }
        }catch (EOFException e){
        }

        task.addScripts(new Script(filename.split("\\.")[0],filename,bcs));
    }
}
