package io.openex.compile;

import io.openex.exe.node.ASTNode;
import io.openex.exe.node.opcode.*;
import io.openex.exe.node.struct.GroupASTNode;
import io.openex.exe.node.struct.InvokeASTNode;
import io.openex.exe.node.struct.MovVarNode;
import io.openex.exe.obj.*;
import io.openex.compile.parser.InvokeParser;
import io.openex.compile.parser.Parser;
import io.openex.util.CompileException;

import java.util.ArrayList;
import java.util.Stack;

public class ExpressionParsing {
    ArrayList<Token> tds;
    Parser parser;
    Compiler compiler;
    Token buffer = null;
    int index;

    public ExpressionParsing(ArrayList<Token> tds,Parser parser,Compiler compiler){
        this.tds = tds;
        this.parser = parser;
        this.compiler = compiler;
        this.index = 0;
    }

    private Token getToken(ArrayList<Token> tokens){
        Token t;
        if(buffer==null) {
            if (index >= tokens.size()) return null;
            t = tokens.get(index);
            index += 1;
        }else {
            t = buffer;
            buffer = null;
        }
        return t;
    }

    public ArrayList<Token> transitSuffix(){

        Stack<Token> op_stack = new Stack<>();
        ArrayList<Token> suffixList = new ArrayList<>();

        while (true) {
            try {
                Token token = getToken(tds);
                if (isOperator(token)) {
                    if (op_stack.isEmpty() || ("(".equals(op_stack.peek().getData()) && op_stack.peek().getType() == Token.LP) || priority(token) > priority(op_stack.peek())) {
                        op_stack.push(token);
                    } else {
                        while (!op_stack.isEmpty() && !("(".equals(op_stack.peek().getData()) && op_stack.peek().getType() == Token.LP)) {
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
                }else if (token.getType() == Token.KEY) {
                    if (token.getData().equals("true") || token.getData().equals("false")) suffixList.add(token);
                    else throw new CompileException("Illegal keywords.",token, parser.getFilename());
                } else if (token.getType() == Token.NAME) {

                    if(compiler.getLibnames().contains(token.getData())){
                        ArrayList<Token> t = new ArrayList<>();
                        int in = 1;
                        t.add(token);
                        do{
                            token = getToken(tds);
                            if(token.getType()==Token.LP&&token.getData().equals("(")){
                                t.add(token);
                                break;
                            }
                            t.add(token);
                        }while (true);
                        do{
                            token = getToken(tds);
                            if(token.getType()==Token.LP&&token.getData().equals("(")) in+=1;
                            if(token.getType()==Token.LR&&token.getData().equals(")")) in -= 1;
                            t.add(token);
                        }while (in > 0);

                        suffixList.add(new TokenX(new GroupASTNode(new InvokeParser(t).eval(parser,compiler))));
                    }else if (compiler.getValueNames().contains(token.getData())){
                        Token name = token;
                        token = getToken(tds);

                        if(token != null) {
                            if (token.getType() == Token.LP && token.getData().equals("[")) {
                                int ibl = 1;
                                ArrayList<Token> ts = new ArrayList<>();
                                ArrayList<ASTNode> bcs = new ArrayList<>(), var = new ArrayList<>();
                                do {
                                    token = getToken(tds);
                                    if (token.getType() == Token.LP && token.getData().equals("[")) ibl += 1;
                                    if (token.getType() == Token.LR && token.getData().equals("]")) ibl -= 1;
                                    if (ibl <= 0) break;
                                    ts.add(token);
                                } while (true);
                                ExpressionParsing ep = new ExpressionParsing(ts, parser, compiler);

                                var.add(new PushNode(new ExVarName(name.getData())));
                                var.add(new GroupASTNode(ep.calculate(ep.transitSuffix())));

                                bcs.add(new InvokeASTNode("array", "get_object", var));

                                suffixList.add(new TokenX(new GroupASTNode(bcs)));
                                continue;
                            } else buffer = token;
                        }

                        suffixList.add(name);
                    }
                    else throw new CompileException("Unable to resolve symbols.",token, parser.getFilename());
                } else if ("(".equals(token.getData()) && token.getType() == Token.LP) {
                    op_stack.push(token);
                } else if (")".equals(token.getData()) && token.getType() == Token.LR) {
                    while (!op_stack.isEmpty()) {
                        if ("(".equals(op_stack.peek().getData()) && op_stack.peek().getType() == Token.LP) {
                            op_stack.pop();
                            break;
                        } else {
                            suffixList.add(op_stack.pop());
                        }
                    }
                } else throw new CompileException("Unable to resolve symbols.",token, parser.getFilename() );
            }catch (NullPointerException e){
                break;
            }
        }

        while (!op_stack.isEmpty()) {
            suffixList.add(op_stack.pop());
        }


        for(Token t:suffixList)
            if(t.getType()==Token.LP&&t.getData().equals("("))throw new CompileException("')' expected.",t, parser.getFilename());


        return suffixList;
    }

    private static boolean isNumber(Token token){
        return token.getType()==Token.INTEGER||token.getType()==Token.DOUBLE||token.getType()==Token.STRING;
    }

    private static boolean isOperator(Token op) {
        if (op.getType() != Token.SEM) return false;
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

    private static int priority(Token op) {
        if (op.getType() != Token.SEM) return -1;

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

    public ArrayList<ASTNode> calculate(ArrayList<Token> suffx){

        ArrayList<ASTNode> bbc = new ArrayList<>();
        for (Token td : suffx) {
            if (td.getType() == Token.NAME) bbc.add(new MovVarNode(td.getData()));
            else if (td.getType() == Token.KEY) {
                switch (td.getData()) {
                    case "true" -> bbc.add(new PushNode(new ExBool(true)));
                    case "false" -> bbc.add(new PushNode(new ExBool(false)));
                    case "null" -> bbc.add(new PushNode(new ExNull()));
                    default -> throw new CompileException("Illegal keywords.", td, parser.getFilename());
                }
            } else if (td.getType() == Token.INTEGER) bbc.add(new PushNode(new ExInt(Integer.parseInt(td.getData()))));
            else if (td.getType() == Token.DOUBLE)
                bbc.add(new PushNode(new ExDouble(Double.parseDouble(td.getData()))));
            else if (td.getType() == Token.SEM) {
                switch (td.getData()) {
                    case "+" -> bbc.add(new AddNode());
                    case "-" -> bbc.add(new SubNode());
                    case "*" -> bbc.add(new MulNode());
                    case "/" -> bbc.add(new DivNode());
                    case "==" -> bbc.add(new EquNode());
                    case ">=" -> bbc.add(new BigEquNode());
                    case "<=" -> bbc.add(new LessEquNode());
                    case "!" -> bbc.add(new NotNode());
                    case "&" -> bbc.add(new AndNode());
                    case "|" -> bbc.add(new OrNode());
                    case "=" -> bbc.add(new MovNode());

                }
            } else if (td.getType() == Token.STRING) bbc.add(new PushNode(new ExString(td.getData())));
            else if (td.getType() == Token.EXP) bbc.add(((TokenX) td).getBc());
        }

        return bbc;
    }

}
