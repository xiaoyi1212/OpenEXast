package io.openex.compile.parser;

import io.openex.astvm.code.ByteCode;
import io.openex.astvm.code.struct.GroupByteCode;
import io.openex.astvm.code.struct.InvokeByteCode;
import io.openex.compile.Compiler;
import io.openex.compile.LexicalAnalysis;
import io.openex.compile.expression.ExpressionParsing;
import io.openex.util.CompileException;

import java.util.ArrayList;
import java.util.Collections;

public class InvokeParser implements BaseParser{
    ArrayList<LexicalAnalysis.Token> tds;
    private int index = 0;

    private LexicalAnalysis.Token getTokens(){
        if(index > tds.size())return null;
        LexicalAnalysis.Token td = tds.get(index);
        index += 1;
        return td;
    }


    public InvokeParser(ArrayList<LexicalAnalysis.Token> tds){
        this.tds = tds;
    }
    @Override
    public ByteCode eval(Parser parser, Compiler compiler)throws CompileException {
        LexicalAnalysis.Token lib = null;
        LexicalAnalysis.Token function = null;

        LexicalAnalysis.Token td = tds.get(0);
        index += 1;

        if((td.getType()==LexicalAnalysis.NAME||(td.getType()==LexicalAnalysis.KEY&&td.getData().equals("this")))) lib = td;

        if(lib==null)throw new CompileException("请检查调用库的名称是否正确:"+td.getData(),td, parser.filename);
        td = getTokens();

        if(!(td.getType()==LexicalAnalysis.SEM&&td.getData().equals(".")))throw new CompileException("未知的调用符",td, parser.filename);td = getTokens();

        if(td.getType()==LexicalAnalysis.NAME) function = td;

        if(function==null)throw new CompileException("请检查调用函数的名称是否正确",td, parser.filename);
        td = getTokens();
        if(!(td.getType()==LexicalAnalysis.LP&&td.getData().equals("(")))throw new CompileException("未知的参数",td, parser.filename);
        td = getTokens();

        if(!compiler.getLibnames().contains(lib.getData()))throw new CompileException("找不到指定库,查看是否经过include语句导入,您应该使用‘include\""+lib.getData()+"\";'来导入这个库",lib, parser.filename);

        ArrayList<ByteCode> values = new ArrayList<>();

        try {
            do {
                ArrayList<LexicalAnalysis.Token> tds = new ArrayList<>();
                int index = 1;
                do {
                    if(td.getType()==LexicalAnalysis.LP && td.getData().equals("(")){
                        tds.add(td);
                        index += 1;
                    }
                    if((td.getType()==LexicalAnalysis.SEM && td.getData().equals(",")&&index <= 0)){
                        td = getTokens();
                        ExpressionParsing inble = new ExpressionParsing(tds,parser,compiler);
                        values.add(new GroupByteCode(inble.calculate(inble.transitSuffix())));
                        tds.clear();
                        continue;
                    }
                    if (td.getType()==LexicalAnalysis.LR && td.getData().equals(")")&&index > 0){
                        index -=1;
                        tds.add(td);
                    }
                    if(td.getType()==LexicalAnalysis.LR && td.getData().equals(")")&&index <= 0) {
                        boolean isnum = true;
                        for(LexicalAnalysis.Token tddebug:tds) {
                            if (tddebug.getType()==LexicalAnalysis.NAME || tddebug.getType()== LexicalAnalysis.KEY) {
                                isnum = false;
                                break;
                            }
                        }
                        ExpressionParsing inble = new ExpressionParsing(tds,parser,compiler);
                        values.add(new GroupByteCode(inble.calculate(inble.transitSuffix())));
                        tds.clear();
                        break;
                    }
                    tds.add(td);
                    td = getTokens();
                } while (true);
            } while (!(td.getType()==LexicalAnalysis.LR && td.getData().equals(")")));
        }catch (IndexOutOfBoundsException ignored) {
        }
        Collections.reverse(values);
        return new InvokeByteCode(lib.getData(),function.getData(),values);
    }
}
