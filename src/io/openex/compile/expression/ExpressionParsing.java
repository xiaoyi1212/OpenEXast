package io.openex.compile.expression;

import io.openex.astvm.code.ByteCode;
import io.openex.astvm.code.opcode.*;
import io.openex.astvm.obj.*;
import io.openex.compile.Compiler;
import io.openex.compile.LexicalAnalysis;
import io.openex.compile.parser.InvokeParser;
import io.openex.compile.parser.Parser;
import io.openex.util.CompileException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class ExpressionParsing {
    ArrayList<LexicalAnalysis.Token> tds;
    Parser parser;
    Compiler compiler;
    int index;

    public ExpressionParsing(ArrayList<LexicalAnalysis.Token> tds,Parser parser,Compiler compiler){
        this.tds = tds;
        this.parser = parser;
        this.compiler = compiler;
        this.index = 0;
    }

    private LexicalAnalysis.Token getToken(ArrayList<LexicalAnalysis.Token> tokens){

        if(index >= tokens.size())return null;
        LexicalAnalysis.Token t = tokens.get(index);
        index += 1;
        return t;
    }

    public ArrayList<LexicalAnalysis.Token> transitSuffix(){
        Stack<LexicalAnalysis.Token> op_stack = new Stack<>();
        ArrayList<LexicalAnalysis.Token> suffixList = new ArrayList<>();

        while (true) {
            try {
                LexicalAnalysis.Token token = getToken(tds);
                if (isOperator(token)) {
                    if (op_stack.isEmpty() || ("(".equals(op_stack.peek().getData()) && op_stack.peek().getType() == LexicalAnalysis.LP) || priority(token) > priority(op_stack.peek())) {
                        op_stack.push(token);
                    } else {
                        while (!op_stack.isEmpty() && !("(".equals(op_stack.peek().getData()) && op_stack.peek().getType() == LexicalAnalysis.LP)) {
                            if (priority(token) <= priority(op_stack.peek())) {
                                suffixList.add(op_stack.pop());
                            }
                            if(op_stack.isEmpty())break;
                            if (priority(token) > priority(op_stack.peek())) break;
                        }
                        op_stack.push(token);
                    }
                } else if (isNumber(token)) {
                    suffixList.add(token);
                }else if (token.getType() == LexicalAnalysis.KEY) {
                    if (token.getData().equals("true") || token.getData().equals("false")) suffixList.add(token);
                    else throw new CompileException("Illegal keywords.",token, parser.getFilename());
                } else if (token.getType() == LexicalAnalysis.NAME) {
                    if (compiler.getValueNames().contains(token.getData())) suffixList.add(token);
                    else throw new CompileException( "Not found value.",token, parser.getFilename());
                } else if ("(".equals(token.getData()) && token.getType() == LexicalAnalysis.LP) {
                    op_stack.push(token);
                } else if (")".equals(token.getData()) && token.getType() == LexicalAnalysis.LR) {
                    while (!op_stack.isEmpty()) {
                        if ("(".equals(op_stack.peek().getData()) && op_stack.peek().getType() == LexicalAnalysis.LP) {
                            op_stack.pop();
                            break;
                        } else {
                            suffixList.add(op_stack.pop());
                        }
                    }
                } else throw new CompileException("Unable to resolve symbols",token, parser.getFilename() );
            }catch (NullPointerException e){
                break;
            }
        }

        while (!op_stack.isEmpty()) {
            suffixList.add(op_stack.pop());
        }


        for(LexicalAnalysis.Token t:suffixList)
            if(t.getType()==LexicalAnalysis.LP&&t.getData().equals("("))throw new CompileException("'()' is incomplete,need ')'",t, parser.getFilename());

        return suffixList;
    }

    private static boolean isNumber(LexicalAnalysis.Token token){
        return token.getType()==LexicalAnalysis.INTEGER||token.getType()==LexicalAnalysis.DOUBLE||token.getType()==LexicalAnalysis.STRING;
    }

    private static boolean isOperator(LexicalAnalysis.Token op) {
        if (op.getType() != LexicalAnalysis.SEM) return false;
        return op.getData().equals("+")
                || op.getData().equals("-")
                || op.getData().equals("*")
                || op.getData().equals("/")
                || op.getData().equals(">=")
                || op.getData().equals("<=")
                || op.getData().equals("==")
                || op.getData().equals("!")
                || op.getData().equals("&")
                || op.getData().equals("|")
                || op.getData().equals("=")
                || op.getData().equals(">")
                || op.getData().equals("<")
                || op.getData().equals(",");
    }

    private static int priority(LexicalAnalysis.Token op) {
        if (op.getType() != LexicalAnalysis.SEM) return -1;

        int i = switch (op.getData()) {
            case "!" -> 7;
            case "*", "/" -> 6;
            case "+", "-" -> 5;
            case ">", ">=", "<=", "<" -> 4;
            case "==" -> 3;
            case "&", "|" -> 2;
            case "=" -> 1;
            case "," -> 0;
            default -> -1;
        };

        return i;
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
                    case "==" -> bbc.add(new EquCode());
                    case ">=" -> bbc.add(new BigEquCode());
                    case "<=" -> bbc.add(new LessEquCode());
                    case "!" -> bbc.add(new NotCode());
                    case "&" -> bbc.add(new AndCode());
                    case "|" -> bbc.add(new OrCode());
                }
            } else if (td.getType()==LexicalAnalysis.STRING) bbc.add(new PushCode(new ExString(td.getData())));
        }
        return bbc;
    }

}
