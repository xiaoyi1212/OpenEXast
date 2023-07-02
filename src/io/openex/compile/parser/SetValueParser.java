package io.openex.compile.parser;

import io.openex.astvm.code.ByteCode;
import io.openex.astvm.code.opcode.PushCode;
import io.openex.astvm.code.struct.MovVarCode;
import io.openex.astvm.obj.ExBool;
import io.openex.astvm.obj.ExNull;
import io.openex.compile.CompileManager;
import io.openex.compile.Compiler;
import io.openex.compile.LexicalAnalysis;
import io.openex.compile.expression.ExpressionParsing;
import io.openex.util.CompileException;

import java.util.ArrayList;

public class SetValueParser implements BaseParser{
    ArrayList<LexicalAnalysis.Token> tds;
    private int index = 0;

    private LexicalAnalysis.Token getTokens(){
        if(index > tds.size())return null;
        LexicalAnalysis.Token td = tds.get(index);
        index += 1;
        return td;
    }
    public SetValueParser(ArrayList<LexicalAnalysis.Token> tds){
        this.tds = tds;
    }
    @Override
    public ByteCode eval(Parser parser, Compiler compiler) throws CompileException {
        LexicalAnalysis.Token td = getTokens();
        if(!(compiler.value_names.contains(td.getData())|| CompileManager.value_names.contains(td.getData())))throw new CompileException("未知的变量名,请检查是否定义过该变量",td, parser.filename);

        LexicalAnalysis.Token name = td;

        td = getTokens();

        ArrayList<ByteCode> var_bc = new ArrayList<>();

        if(td.getType()==LexicalAnalysis.END){
           throw new CompileException("不是语句",td, parser.filename);
        }else {
            if (!(td.getType() == LexicalAnalysis.SEM))
                throw new CompileException("变量初始值需要‘=’号", name, parser.filename);
            td = getTokens();

            if (td.getType() == LexicalAnalysis.KEY) {
                if (td.getData().equals("true")) var_bc.add(new PushCode(new ExBool(true)));
                else if (td.getData().equals("false")) var_bc.add(new PushCode(new ExBool(false)));
                else if (td.getData().equals("null")) var_bc.add(new PushCode(new ExNull()));
                else throw new CompileException("未知的初始值定义", td, parser.filename);
            } else if (td.getType() == LexicalAnalysis.END) {
                var_bc.add(new PushCode(new ExNull()));
            } else {
                ArrayList<LexicalAnalysis.Token> t = new ArrayList<>();
                t.add(td);
                do {
                    td = getTokens();
                    if (td.getType() == LexicalAnalysis.END) break;
                    t.add(td);
                } while (true);
                ExpressionParsing p = new ExpressionParsing(t, parser, compiler);
                var_bc.addAll(p.calculate(p.transitSuffix()));
            }
        }
        return new MovVarCode(name.getData(),var_bc);
    }
}
