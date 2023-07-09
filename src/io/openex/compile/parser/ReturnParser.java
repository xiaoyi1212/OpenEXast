package io.openex.compile.parser;

import io.openex.exe.node.ASTNode;
import io.openex.exe.node.struct.ReturnNode;
import io.openex.compile.Compiler;
import io.openex.compile.Token;
import io.openex.compile.ExpressionParsing;
import io.openex.util.CompileException;

import java.util.ArrayList;

public class ReturnParser implements BaseParser{
    ArrayList<Token> tds;
    public ReturnParser(ArrayList<Token> tds){
        this.tds = tds;
    }

    @Override
    public ASTNode eval(Parser parser, Compiler compiler) throws CompileException {
        ExpressionParsing p = new ExpressionParsing(tds,parser,compiler);
        return new ReturnNode(p.calculate(p.transitSuffix()));
    }
}
