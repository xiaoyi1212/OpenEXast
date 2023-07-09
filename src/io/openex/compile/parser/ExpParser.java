package io.openex.compile.parser;

import io.openex.exe.node.ASTNode;
import io.openex.exe.node.struct.GroupASTNode;
import io.openex.compile.Compiler;
import io.openex.compile.Token;
import io.openex.compile.ExpressionParsing;
import io.openex.util.CompileException;

import java.util.ArrayList;

public class ExpParser implements BaseParser{
    ArrayList<Token> tds;

    public ExpParser(ArrayList<Token> tds){
        this.tds = tds;
    }
    @Override
    public ASTNode eval(Parser parser, Compiler compiler) throws CompileException {

        ExpressionParsing ep = new ExpressionParsing(tds,parser,compiler);
        return new GroupASTNode(ep.calculate(ep.transitSuffix()));
    }
}
