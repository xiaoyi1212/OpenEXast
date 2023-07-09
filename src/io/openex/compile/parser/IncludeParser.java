package io.openex.compile.parser;

import io.openex.exe.node.ASTNode;
import io.openex.exe.node.struct.NulASTNode;
import io.openex.compile.Compiler;
import io.openex.compile.Token;
import io.openex.util.CompileException;

import java.util.ArrayList;

public class IncludeParser implements BaseParser{
    ArrayList<Token> tds;

    public IncludeParser(ArrayList<Token> tds){
        this.tds = tds;
    }

    @Override
    public ASTNode eval(Parser parser, Compiler compiler)throws CompileException {
        if(tds.size()>2)throw new CompileException("Unable to resolve symbols.",tds.get(tds.size()-2),parser.filename);
        Token l = tds.get(0);
        if(l.getType()==Token.STRING) compiler.getLibnames().add(l.getData());
        else throw new CompileException("Type name '"+l.getData()+"' is not valid.",l, parser.filename);
        return new NulASTNode();
    }
}
