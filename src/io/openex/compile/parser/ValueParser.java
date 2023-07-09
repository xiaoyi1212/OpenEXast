package io.openex.compile.parser;

import io.openex.exe.node.ASTNode;
import io.openex.exe.node.opcode.PushNode;
import io.openex.exe.node.struct.GroupASTNode;
import io.openex.exe.node.struct.LoadArrayNode;
import io.openex.exe.node.struct.LoadVarNode;
import io.openex.exe.obj.ExBool;
import io.openex.exe.obj.ExNull;
import io.openex.compile.Compiler;
import io.openex.compile.Token;
import io.openex.compile.ExpressionParsing;
import io.openex.util.CompileException;

import java.util.ArrayList;

public class ValueParser implements BaseParser{

    ArrayList<Token> tds;
    private int index = 0;

    private Token getTokens(){
        if(index > tds.size())return null;
        Token td = tds.get(index);
        index += 1;
        return td;
    }


    public ValueParser(ArrayList<Token> tds){
        this.tds = tds;
    }

    @Override
    public ASTNode eval(Parser parser, Compiler compiler) throws CompileException {


        String text;
        Token name;
        Token td = getTokens();
//Token.
        if(!(td.getType()==Token.NAME))throw new CompileException("Unable to resolve symbols.",td, parser.filename);
        name = td;
        if(compiler.getValueNames().contains(name.getData()))throw new CompileException("The type "+name.getData()+" is already defined.",td, parser.filename);

        td = getTokens();
        if(!(td.getType()==Token.SEM&&td.getData().equals(":")))throw new CompileException("Unable to resolve symbols",td, parser.filename);
        td = getTokens();
        if(!(td.getType()==Token.STRING)) throw new CompileException("Unable to resolve symbols",td, parser.filename);
        text = td.getData();

        td = getTokens();
        ArrayList<ASTNode> var_bc = new ArrayList<>();

        if(td.getType()==Token.END){
            var_bc.add(new PushNode(new ExNull()));
        }else {
            if (!(td.getType() == Token.SEM))
                throw new CompileException("Unable to resolve symbols.", name, parser.filename);
            td = getTokens();

            if (td.getType() == Token.KEY) {
                switch (td.getData()) {
                    case "true" -> var_bc.add(new PushNode(new ExBool(true)));
                    case "false" -> var_bc.add(new PushNode(new ExBool(false)));
                    case "null" -> var_bc.add(new PushNode(new ExNull()));
                    default -> throw new CompileException("Unable to resolve symbols.", td, parser.filename);
                }
            } else if (td.getType() == Token.END) {
                var_bc.add(new PushNode(new ExNull()));
            }else if(td.getType()==Token.LP){
                boolean isend = true,isf = true;
                ArrayList<GroupASTNode> b = new ArrayList<>();
                do {
                    ArrayList<Token> tds = new ArrayList<>();
                    do {
                        td = getTokens();
                        if (td.getType() == Token.LR && td.getData().equals("]")){
                            isend = false;
                            if(tds.size()==0&&isf){
                                compiler.value_names.add(name.getData());
                                return new LoadArrayNode(name.getData(),new ArrayList<>(),0,0);
                            }
                            break;
                        }
                        if (td.getType() == Token.SEM && td.getData().equals(",")) break;
                        tds.add(td);
                    } while (true);
                    ExpressionParsing p = new ExpressionParsing(tds,parser,compiler);
                    b.add(new GroupASTNode(p.calculate(p.transitSuffix())));
                    isf = false;
                }while (isend);
                compiler.value_names.add(name.getData());
                return new LoadArrayNode(name.getData(),b,0,-1);
            } else {
                ArrayList<Token> t = new ArrayList<>();
                t.add(td);
                do {
                    td = getTokens();
                    if (td.getType() == Token.END) break;
                    t.add(td);
                } while (true);
                ExpressionParsing p = new ExpressionParsing(t, parser, compiler);
                var_bc.addAll(p.calculate(p.transitSuffix()));
            }
        }

        compiler.value_names.add(name.getData());

        return new LoadVarNode(name.getData(),text,0,var_bc);
    }
}
