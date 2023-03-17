package ex.openex.compile.parser;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.code.struct.ReturnCode;
import ex.openex.compile.Compiler;
import ex.openex.compile.LexicalAnalysis;
import ex.openex.compile.expression.ExpressionParsing;
import ex.openex.exception.CompileException;

import java.util.ArrayList;

public class ReturnParser implements BaseParser{
    ArrayList<LexicalAnalysis.Token> tds;
    public ReturnParser(ArrayList<LexicalAnalysis.Token> tds){
        this.tds = tds;
    }

    @Override
    public ByteCode eval(Parser parser, Compiler compiler) throws CompileException {
        ExpressionParsing p = new ExpressionParsing(tds,parser,compiler);
        return new ReturnCode(p.calculate(p.transitSuffix()));
    }
}
