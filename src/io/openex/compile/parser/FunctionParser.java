package io.openex.compile.parser;

import io.openex.exe.node.ASTNode;
import io.openex.exe.node.struct.LoadVarNode;
import io.openex.exe.node.struct.NulASTNode;
import io.openex.exe.lib.Function;
import io.openex.exe.thread.ThreadManager;
import io.openex.compile.Compiler;
import io.openex.compile.Token;
import io.openex.util.CompileException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FunctionParser implements BaseParser{
    ArrayList<Token> vars;
    ArrayList<BaseParser> parsers;
    String function_name;

    public FunctionParser(String function_name, ArrayList<Token> vars,ArrayList<BaseParser> parsers){
        this.function_name = function_name;
        this.parsers = parsers;
        this.vars = vars;
    }

    @Override
    public ASTNode eval(Parser parser, Compiler compiler) throws CompileException {
        ArrayList<ASTNode> bcs = new ArrayList<>();
        try {
            for (Iterator<Token> iterator = vars.iterator(); iterator.hasNext(); ) {
                Token t = iterator.next();
                if (!(t.getType() == Token.NAME))
                    throw new CompileException("Type name '"+t.getData()+"' is not valid..", t, parser.filename);
                bcs.add(new LoadVarNode(t.getData(), "function_var", 1, new ArrayList<>()));
                t = iterator.next();
                if (!(t.getType() == Token.SEM && t.getData().equals(",")))
                    throw new CompileException("Unable to resolve symbols, Please use ','.", t, parser.filename);
            }
        }catch (NoSuchElementException e){
        }
        Collections.reverse(bcs);

        for(BaseParser bp:parsers)bcs.add(bp.eval(parser,compiler));

        ThreadManager.getFunctions().add(new Function(parser.getFilename().split("\\.")[0],function_name,bcs));
        return new NulASTNode();
    }
}
