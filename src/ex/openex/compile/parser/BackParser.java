package ex.openex.compile.parser;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.code.struct.BackCode;
import ex.openex.compile.Compiler;
import ex.openex.compile.LexicalAnalysis;
import ex.openex.compile.expression.ExpressionParsing;
import ex.openex.exception.CompileException;

import java.util.ArrayList;

public class BackParser implements BaseParser{
    ArrayList<LexicalAnalysis.Token> t;
    public BackParser(ArrayList<LexicalAnalysis.Token> t){
        this.t = t;
    }

    @Override
    public ByteCode eval(Parser parser, Compiler compiler) throws CompileException {
        ExpressionParsing p = new ExpressionParsing(t,parser,compiler);
        return new BackCode(p.calculate(p.transitSuffix()));
    }
}
