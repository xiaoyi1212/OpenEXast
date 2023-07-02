package io.openex.compile.parser;

import io.openex.astvm.code.ByteCode;
import io.openex.astvm.code.struct.BackCode;
import io.openex.compile.Compiler;
import io.openex.compile.LexicalAnalysis;
import io.openex.compile.expression.ExpressionParsing;
import io.openex.util.CompileException;

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
