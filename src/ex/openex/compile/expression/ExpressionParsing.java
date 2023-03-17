package ex.openex.compile.expression;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.code.opcode.*;
import ex.openex.astvm.obj.*;
import ex.openex.compile.Compiler;
import ex.openex.compile.LexicalAnalysis;
import ex.openex.compile.parser.InvokeParser;
import ex.openex.compile.parser.Parser;
import ex.openex.exception.CompileException;
import ex.openex.exception.VMRuntimeException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class ExpressionParsing {
    ArrayList<LexicalAnalysis.Token> tds;
    Parser parser;
    Compiler compiler;

    public ExpressionParsing(ArrayList<LexicalAnalysis.Token> tds,Parser parser,Compiler compiler){
        this.tds = tds;
        this.parser = parser;
        this.compiler = compiler;
    }

    public ArrayList<LexicalAnalysis.Token> transitSuffix(){
        Stack<LexicalAnalysis.Token> stack = new Stack<>();
        ArrayList<LexicalAnalysis.Token> suffixList = new ArrayList<>();
        for (Iterator<LexicalAnalysis.Token> iterator = tds.iterator(); iterator.hasNext(); ) {
            LexicalAnalysis.Token tmp;
            LexicalAnalysis.Token t = iterator.next();

            int type = t.getType();
            if(type==LexicalAnalysis.SEM||type==LexicalAnalysis.STRING||type==LexicalAnalysis.INTEGER||type==LexicalAnalysis.DOUBLE) {
                switch (t.getData()) {
                    case "(" -> stack.push(t);
                    case "+", "-" -> {
                        while (stack.size() != 0) {
                            tmp = stack.pop();
                            if (tmp.getData().equals("(")) {
                                stack.push(tmp);
                                break;
                            }
                            suffixList.add(tmp);
                        }
                        stack.push(t);
                    }
                    case "*", "/" -> {
                        while (stack.size() != 0) {
                            tmp = stack.pop();
                            if (tmp.getData().equals("+") || tmp.getData().equals("-") || tmp.getData().equals("(")) {
                                stack.push(tmp);
                                break;
                            }
                            suffixList.add(tmp);
                        }
                        stack.push(t);
                    }
                    case ")" -> {
                        while (!stack.isEmpty()) {
                            tmp = stack.pop();
                            if (tmp.getData().equals("(")) {
                                break;
                            }
                            suffixList.add(tmp);
                        }
                    }
                    default -> suffixList.add(t);
                }
            }else if (type==LexicalAnalysis.NAME) {
                if(compiler.getLibnames().contains(t.getData())) {
                    ArrayList<LexicalAnalysis.Token> invoke = new ArrayList<>();
                    invoke.add(t);
                    int lp_ax = 1, isf = 0;
                    try {
                        do {
                            t = iterator.next();
                            if ((t.getType() == LexicalAnalysis.LP && t.getData().equals("(")) && isf == 0) {
                                isf = 1;
                                invoke.add(t);
                                continue;
                            }
                            if (t.getType() == LexicalAnalysis.LP && t.getData().equals("(")) lp_ax += 1;
                            if (t.getType() == LexicalAnalysis.LR && t.getData().equals(")")) lp_ax -= 1;
                            invoke.add(t);
                        } while (lp_ax > 0);

                        suffixList.addAll(invoke);
                    } catch (NoSuchElementException var1) {
                        suffixList.add(t);
                    }
                }else suffixList.add(t);
            }else {
                suffixList.add(t);
            }
        }

        while (!stack.isEmpty()) {suffixList.add(stack.pop());}

        return suffixList;
    }

    int index = 0;
    LexicalAnalysis.Token buffer;
    private LexicalAnalysis.Token getLeaf(ArrayList<LexicalAnalysis.Token> suffx){
        if(buffer!=null){
            LexicalAnalysis.Token l = buffer;
            buffer = null;
            return l;
        }else {
            if(index >= suffx.size())return null;
            LexicalAnalysis.Token l = suffx.get(index);
            index += 1;
            return l;
        }
    }

    public ArrayList<ByteCode> calculate(ArrayList<LexicalAnalysis.Token> suffx){
        ArrayList<ByteCode> bbc = new ArrayList<>();
        for (Iterator<LexicalAnalysis.Token> iterator = suffx.iterator(); iterator.hasNext(); ) {
            LexicalAnalysis.Token td = iterator.next();
            if (td.getType()==LexicalAnalysis.NAME) {
                if(compiler.getLibnames().contains(td.getData())){
                    int exe_index = 1;boolean isfirst = false;
                    ArrayList<LexicalAnalysis.Token> v_tds = new ArrayList<>();
                    v_tds.add(td);
                    do{
                        td = iterator.next();
                        v_tds.add(td);
                        if(td.getType()==LexicalAnalysis.LP&&td.getData().equals("(")&&isfirst) exe_index += 1;
                        if(td.getType()==LexicalAnalysis.LP&&td.getData().equals("(")) isfirst = true;
                        if(td.getType()==LexicalAnalysis.LR&&td.getData().equals(")")) exe_index-= 1;
                    }while (exe_index > 0);
                    bbc.add(new InvokeParser(v_tds).eval(parser,compiler));
                    continue;
                }else{
                    ExVarName valuea = null;
                    valuea = new ExVarName(td.getData());

                    bbc.add(new PushCode(valuea));
                }
            }else if(td.getType()==LexicalAnalysis.KEY){
                switch (td.getData()) {
                    case "true" -> bbc.add(new PushCode(new ExBool(true)));
                    case "false" -> bbc.add(new PushCode(new ExBool(false)));
                    case "null" -> bbc.add(new PushCode(new ExNull()));
                    default -> throw new CompileException("表达式中不能使用该关键字", td, parser.getFilename());
                }
            }else if (td.getType()==LexicalAnalysis.INTEGER) bbc.add(new PushCode(new ExInt(Integer.parseInt(td.getData()))));
            else if(td.getType()==LexicalAnalysis.DOUBLE) bbc.add(new PushCode(new ExDouble(Double.parseDouble(td.getData()))));
            else if (td.getType()==LexicalAnalysis.SEM) {
                switch (td.getData()) {
                    case "+" -> bbc.add(new AddCode());
                    case "-" -> bbc.add(new SubCode());
                    case "*" -> bbc.add(new MulCode());
                    case "/" -> bbc.add(new DivCode());
                }
            } else if (td.getType()==LexicalAnalysis.STRING) bbc.add(new PushCode(new ExString(td.getData())));
        }
        return bbc;
    }

}
