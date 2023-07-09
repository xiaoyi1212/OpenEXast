package io.openex.compile.parser;

import io.openex.exe.node.ASTNode;
import io.openex.exe.node.struct.BackNode;
import io.openex.compile.Compiler;
import io.openex.compile.Token;
import io.openex.compile.ExpressionParsing;
import io.openex.util.CompileException;

import java.util.ArrayList;

public class BackParser implements BaseParser{
    ArrayList<Token> t;
    public BackParser(ArrayList<Token> t){
        this.t = t;
    }

    @Override
    public ASTNode eval(Parser parser, Compiler compiler) throws CompileException {
        ExpressionParsing p = new ExpressionParsing(t,parser,compiler);
        return new BackNode(p.calculate(p.transitSuffix()));
    }
}
