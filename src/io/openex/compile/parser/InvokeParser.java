package io.openex.compile.parser;

import io.openex.exe.node.ASTNode;
import io.openex.exe.node.struct.GroupASTNode;
import io.openex.exe.node.struct.InvokeASTNode;
import io.openex.compile.Compiler;
import io.openex.compile.Token;
import io.openex.compile.ExpressionParsing;
import io.openex.util.CompileException;

import java.util.ArrayList;
import java.util.Collections;

public class InvokeParser implements BaseParser{
    ArrayList<Token> tds;
    private int index = 0;

    private Token getTokens(){
        if(index > tds.size())return null;
        Token td = tds.get(index);
        index += 1;
        return td;
    }


    public InvokeParser(ArrayList<Token> tds){
        this.tds = tds;
    }
    @Override
    public ASTNode eval(Parser parser, Compiler compiler)throws CompileException {
        Token lib = null;
        Token function = null;

        Token td = tds.get(0);
        index += 1;

        if((td.getType()==Token.NAME||(td.getType()==Token.KEY&&td.getData().equals("this")))) lib = td;

        if(lib==null)throw new CompileException("Type name '"+td.getData()+"' is not valid.",td, parser.filename);
        td = getTokens();

        if(!(td.getType()==Token.SEM&&td.getData().equals(".")))throw new CompileException("'.'expected.",td, parser.filename);td = getTokens();

        if(td.getType()==Token.NAME) function = td;

        if(function==null)throw new CompileException("Type name '"+td.getData()+"' is not valid.",td, parser.filename);
        td = getTokens();
        if(!(td.getType()==Token.LP&&td.getData().equals("(")))throw new CompileException("'('expected.",td, parser.filename);
        td = getTokens();

        ArrayList<ASTNode> values = new ArrayList<>();

        try {
            do {
                ArrayList<Token> tds = new ArrayList<>();
                int index = 1;
                do {
                    if(td.getType()==Token.LP && td.getData().equals("(")){
                        tds.add(td);
                        index += 1;
                    }
                    if((td.getType()==Token.SEM && td.getData().equals(",")&&index <= 0)){
                        td = getTokens();
                        ExpressionParsing inble = new ExpressionParsing(tds,parser,compiler);
                        values.add(new GroupASTNode(inble.calculate(inble.transitSuffix())));
                        tds.clear();
                        continue;
                    }
                    if (td.getType()==Token.LR && td.getData().equals(")")&&index > 0){
                        index -=1;
                        tds.add(td);
                    }
                    if(td.getType()==Token.LR && td.getData().equals(")")&&index <= 0) {

                        Token tdd= tds.get(0);
                        if(!(tdd.getType()==Token.LP && tdd.getData().equals("("))){
                            tdd = tds.get(tds.size()-1);
                            if(tdd.getType()==Token.LR && tdd.getData().equals(")")) tds.remove(tds.size()-1);
                        }

                        ExpressionParsing inble = new ExpressionParsing(tds,parser,compiler);
                        values.add(new GroupASTNode(inble.calculate(inble.transitSuffix())));
                        tds.clear();
                        break;
                    }
                    tds.add(td);
                    td = getTokens();
                } while (true);
            } while (!(td.getType()==Token.LR && td.getData().equals(")")));
        }catch (IndexOutOfBoundsException ignored) {
        }
        Collections.reverse(values);
        return new InvokeASTNode(lib.getData(),function.getData(),values);
    }
}
