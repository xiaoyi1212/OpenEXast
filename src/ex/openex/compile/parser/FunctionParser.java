package ex.openex.compile.parser;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.code.struct.LoadVarCode;
import ex.openex.astvm.code.struct.NulByteCode;
import ex.openex.astvm.lib.Function;
import ex.openex.astvm.thread.ThreadManager;
import ex.openex.compile.Compiler;
import ex.openex.compile.LexicalAnalysis;
import ex.openex.exception.CompileException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FunctionParser implements BaseParser{
    ArrayList<LexicalAnalysis.Token> vars;
    ArrayList<BaseParser> parsers;
    String function_name;

    public FunctionParser(String function_name, ArrayList<LexicalAnalysis.Token> vars,ArrayList<BaseParser> parsers){
        this.function_name = function_name;
        this.parsers = parsers;
        this.vars = vars;
    }

    @Override
    public ByteCode eval(Parser parser, Compiler compiler) throws CompileException {
        ArrayList<ByteCode> bcs = new ArrayList<>();
        try {
            for (Iterator<LexicalAnalysis.Token> iterator = vars.iterator(); iterator.hasNext(); ) {
                LexicalAnalysis.Token t = iterator.next();
                if (!(t.getType() == LexicalAnalysis.NAME))
                    throw new CompileException("未知的形参定义", t, parser.filename);
                bcs.add(new LoadVarCode(t.getData(), "function_var", 1, new ArrayList<>()));
                t = iterator.next();
                if (!(t.getType() == LexicalAnalysis.SEM && t.getData().equals(",")))
                    throw new CompileException("未知的形参分隔符号,请使用','分割", t, parser.filename);
            }
        }catch (NoSuchElementException e){
        }
        Collections.reverse(bcs);

        for(BaseParser bp:parsers)bcs.add(bp.eval(parser,compiler));

        ThreadManager.getFunctions().add(new Function(parser.getFilename().split("\\.")[0],function_name,bcs));
        return new NulByteCode();
    }
}
