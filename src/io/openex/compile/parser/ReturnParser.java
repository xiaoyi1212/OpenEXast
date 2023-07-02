package io.openex.compile.parser;

import io.openex.astvm.code.ByteCode;
import io.openex.astvm.code.struct.ReturnCode;
import io.openex.compile.Compiler;
import io.openex.compile.LexicalAnalysis;
import io.openex.compile.expression.ExpressionParsing;
import io.openex.util.CompileException;

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
