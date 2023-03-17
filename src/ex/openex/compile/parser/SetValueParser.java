package ex.openex.compile.parser;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.code.opcode.PushCode;
import ex.openex.astvm.code.struct.MovVarCode;
import ex.openex.astvm.obj.ExBool;
import ex.openex.astvm.obj.ExNull;
import ex.openex.compile.CompileManager;
import ex.openex.compile.Compiler;
import ex.openex.compile.LexicalAnalysis;
import ex.openex.compile.expression.ExpressionParsing;
import ex.openex.exception.CompileException;

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
