package ex.openex.compile;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.exe.Script;
import ex.openex.astvm.thread.ThreadTask;
import ex.openex.compile.parser.BaseParser;
import ex.openex.compile.parser.IncludeParser;
import ex.openex.compile.parser.Parser;

import java.io.EOFException;
import java.util.ArrayList;

public class Compiler {
    String filename;
    ArrayList<ByteCode> bcs;
    ArrayList<String> libname;
    public ArrayList<String> value_names;

    public ArrayList<ByteCode> getBcs() {
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
    public void compile(ThreadTask task){
        LexicalAnalysis al = new LexicalAnalysis(CompileManager.getFileData(filename),filename);
        ArrayList<BaseParser> p = new ArrayList<>();
        ArrayList<LexicalAnalysis.Token> t = new ArrayList<>(),o = new ArrayList<>();
        for(LexicalAnalysis.Token b:al.getTokens()) {
            if (b.type == LexicalAnalysis.LINE) continue;
            t.add(b);
        }

        for (LexicalAnalysis.Token b:t)
            if(b.type!=LexicalAnalysis.TEXT)o.add(b);

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
