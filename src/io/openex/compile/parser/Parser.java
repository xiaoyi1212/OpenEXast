package io.openex.compile.parser;

import io.openex.compile.Compiler;
import io.openex.compile.Token;
import io.openex.compile.ExpressionParsing;
import io.openex.util.CompileException;

import java.io.EOFException;
import java.util.ArrayList;

public class Parser {
    ArrayList<Token> tds;
    int index;
    Token buffer;
    String filename;

    public String getFilename() {
        return filename;
    }

    public Parser(ArrayList<Token> tds, String filename){
        this.tds = tds;
        index = 0;
        this.filename = filename;
    }

    private Token getToken() throws EOFException {
        if(buffer != null){
            Token r = buffer;
            buffer = null;
            return r;
        }else {
            if(index >= tds.size())throw new EOFException();
            Token t = tds.get(index);
            index += 1;
            return t;
        }
    }

    public BaseParser getParser(Compiler c) throws EOFException {
        Token buf;
        buf = getToken();
        if(buf.getType()==Token.KEY){
            switch (buf.getData()) {
                case "include" -> {
                    ArrayList<Token> tds = new ArrayList<>();
                    do {
                        buf = getToken();
                        tds.add(buf);
                    } while (buf.getType() != Token.END);
                    return new IncludeParser(tds);
                }
                case "value" -> {
                    ArrayList<Token> tds = new ArrayList<>();
                    do {
                        buf = getToken();
                        tds.add(buf);
                    } while (buf.getType() != Token.END);
                    return new ValueParser(tds);
                }
                case "function"->{
                    ArrayList<Token> vars = new ArrayList<>(),groups = new ArrayList<>();
                    Token t = getToken();
                    if(!(t.getType()==Token.NAME))throw new CompileException("Type name '"+t.getData()+"' is not valid.",t,getFilename());
                    String name = t.getData();
                    t = getToken();
                    if(!(t.getType()==Token.LP&&t.getData().equals("(")))throw new CompileException("'('expected.",t,getFilename());
                    do{
                        t = getToken();
                        if(t.getType()==Token.LR&&t.getData().equals(")"))break;
                        if(t.getType()==Token.NAME)c.value_names.add(t.getData());
                        vars.add(t);
                    }while (true);
                    t = getToken();
                    int i = 1;
                    if(!(t.getType()==Token.LP&&t.getData().equals("{")))throw new CompileException("Missing function body.",t,getFilename());
                    do{
                        t = getToken();
                        if(t.getType()==Token.LP&&t.getData().equals("{"))i+=1;
                        if(t.getType()==Token.LR&&t.getData().equals("}"))i-=1;
                        if(i == 0)break;
                        groups.add(t);
                    }while (true);

                    return new FunctionParser(name,vars,new SubParser(groups,this,c).getParsers());

                }
                case "if"->{
                    ArrayList<Token> vars = new ArrayList<>(),groups = new ArrayList<>(),else_g = new ArrayList<>();
                    Token t = getToken();

                    if(!(t.getType()==Token.LP&&t.getData().equals("(")))throw new CompileException("'(' expected.",t, filename);
                    t = getToken();
                    int index = 1;
                    do {
                        if(t.getType()==Token.LP && t.getData().equals("(")){
                            vars.add(t);
                            index += 1;
                        }
                        if (t.getType()==Token.LR && t.getData().equals(")")&&index > 0){
                            index -=1;
                            vars.add(t);
                        }
                        if(t.getType()==Token.LR && t.getData().equals(")")&&index <= 0) break;

                        vars.add(t);
                        t = getToken();
                    } while (true);
                    t = getToken();
                    int i = 1;
                    if(!(t.getType()==Token.LP&&t.getData().equals("{")))throw new CompileException("Missing statement body.",t,getFilename());
                    do{
                        t = getToken();
                        if(t.getType()==Token.LP&&t.getData().equals("{"))i+=1;
                        if(t.getType()==Token.LR&&t.getData().equals("}"))i-=1;
                        if(i == 0)break;
                        groups.add(t);
                    }while (true);
                    ExpressionParsing e = new ExpressionParsing(vars,this,c);
                    try {
                        t = getToken();
                    }catch (EOFException e11){
                        return new IfParser(e.calculate(e.transitSuffix()),new SubParser(groups,this,c).getParsers(),new ArrayList<>());
                    }
                    int j = 1;
                    if(t.getType()==Token.KEY&&t.getData().equals("else")){
                        t = getToken();
                        if(!(t.getType()==Token.LP&&t.getData().equals("{")))throw new CompileException("Missing statement body.",t,getFilename());
                        do{
                            t = getToken();
                            if(t.getType()==Token.LP&&t.getData().equals("{"))j+=1;
                            if(t.getType()==Token.LR&&t.getData().equals("}"))j-=1;
                            if(j == 0)break;
                            else_g.add(t);
                        }while (true);
                    }else buffer = t;

                    return new IfParser(e.calculate(e.transitSuffix()),new SubParser(groups,this,c).getParsers(),new SubParser(else_g,this,c).getParsers());
                }case "while"->{
                    ArrayList<Token> vars = new ArrayList<>(),groups = new ArrayList<>();
                    Token t = getToken();

                    if(!(t.getType()==Token.LP&&t.getData().equals("(")))throw new CompileException("'(' expected.",t, this.filename);
                    t = getToken();
                    int index = 1;
                    do {
                        if(t.getType()==Token.LP && t.getData().equals("(")){
                            vars.add(t);
                            index += 1;
                        }
                        if (t.getType()==Token.LR && t.getData().equals(")")&&index > 0){
                            index -=1;
                            vars.add(t);
                        }
                        if(t.getType()==Token.LR && t.getData().equals(")")&&index <= 0) {
                            for(Token tddebug:vars) {
                                if (tddebug.getType()==Token.NAME || tddebug.getType()== Token.KEY) {
                                    break;
                                }
                            }
                            break;
                        }
                        vars.add(t);
                        t = getToken();
                    } while (true);
                    t = getToken();
                    int i = 1;
                    if(!(t.getType()==Token.LP&&t.getData().equals("{")))throw new CompileException("Missing statement body.",t, this.filename);
                    do{
                        t = getToken();
                        if(t.getType()==Token.LP&&t.getData().equals("{"))i+=1;
                        if(t.getType()==Token.LR&&t.getData().equals("}"))i-=1;
                        if(i == 0)break;
                        groups.add(t);
                    }while (true);
                    ExpressionParsing e = new ExpressionParsing(vars,this,c);
                    return new WhileParser(e.calculate(e.transitSuffix()),new SubParser(groups,this,c).getParsers());
                }
                default -> throw new CompileException("Not a statement.",buf,getFilename());
            }
        }else if(buf.getType()==Token.NAME) {
            ArrayList<Token> tds = new ArrayList<>();
            tds.add(buf);
            do {
                buf = getToken();
                tds.add(buf);
            } while (buf.getType() != Token.END);
            int a = 0; //1 invoke| 2 value
            for (Token t : tds) {
                if (t.getType() == Token.SEM && t.getData().equals(".")) {
                    a = 1;
                    break;
                }
            }
            if (a == 0) {

                return new ExpParser(tds);
            }
            return new InvokeParser(tds);

        }else if(buf.getType()==Token.SEM){
            return null;
        } else throw new CompileException("Illegal start of expression.",buf,filename);
    }

    public static class SubParser{
        ArrayList<Token> tds;
        int index;
        Parser parser;
        Compiler compiler;
        Token buffer;

        public SubParser(ArrayList<Token> tds,Parser parser,Compiler compiler){
            this.tds = tds;
            index = 0;
            this.parser = parser;
            this.compiler = compiler;
        }

        private Token getToken() throws EOFException {
            if(buffer != null){
                Token r = buffer;
                buffer = null;
                return r;
            }else {
                if(index >= tds.size())throw new EOFException();
                Token t = tds.get(index);
                index += 1;
                return t;
            }
        }

        private BaseParser getParser() throws EOFException {

            Token buf;
            buf = getToken();
            if(buf.getType()==Token.KEY){
                switch (buf.getData()) {
                    case "value" -> {
                        ArrayList<Token> tds = new ArrayList<>();
                        do {
                            buf = getToken();
                            tds.add(buf);
                        } while (buf.getType() != Token.END);
                        return new ValueParser(tds);
                    }
                    case "if"->{
                        ArrayList<Token> vars = new ArrayList<>(),groups = new ArrayList<>(),else_g = new ArrayList<>();
                        Token t = getToken();

                        if(!(t.getType()==Token.LP&&t.getData().equals("(")))throw new CompileException("'(' expected.",t, parser.filename);
                        t = getToken();
                        int index = 1;
                        do {
                            if(t.getType()==Token.LP && t.getData().equals("(")){
                                vars.add(t);
                                index += 1;
                            }
                            if (t.getType()==Token.LR && t.getData().equals(")")&&index > 0){
                                index -=1;
                                vars.add(t);
                            }
                            if(t.getType()==Token.LR && t.getData().equals(")")&&index <= 0) {
                                for(Token tddebug:vars) {
                                    if (tddebug.getType()==Token.NAME || tddebug.getType()== Token.KEY) {
                                        break;
                                    }
                                }
                                break;
                            }
                            vars.add(t);
                            t = getToken();
                        } while (true);
                        t = getToken();
                        int i = 1;
                        if(!(t.getType()==Token.LP&&t.getData().equals("{")))throw new CompileException("Missing statement body.",t, parser.filename);
                        do{
                            t = getToken();
                            if(t.getType()==Token.LP&&t.getData().equals("{"))i+=1;
                            if(t.getType()==Token.LR&&t.getData().equals("}"))i-=1;
                            if(i == 0)break;
                            groups.add(t);
                        }while (true);
                        ExpressionParsing e = new ExpressionParsing(vars,parser,compiler);
                        try {
                            t = getToken();
                        }catch (EOFException e11){
                            return new IfParser(e.calculate(e.transitSuffix()),new SubParser(groups,parser,compiler).getParsers(),new ArrayList<>());
                        }
                        int j = 1;
                        if(t.getType()==Token.KEY&&t.getData().equals("else")){
                            t = getToken();
                            if(!(t.getType()==Token.LP&&t.getData().equals("{")))throw new CompileException("Missing statement body.",t, parser.filename);
                            do{
                                t = getToken();
                                if(t.getType()==Token.LP&&t.getData().equals("{"))j+=1;
                                if(t.getType()==Token.LR&&t.getData().equals("}"))j-=1;
                                if(j == 0)break;
                                else_g.add(t);
                            }while (true);
                        }else buffer = t;

                        return new IfParser(e.calculate(e.transitSuffix()),new SubParser(groups,parser,compiler).getParsers(),new SubParser(else_g,parser,compiler).getParsers());
                    }
                    case "while"->{
                        ArrayList<Token> vars = new ArrayList<>(),groups = new ArrayList<>();
                        Token t = getToken();

                        if(!(t.getType()==Token.LP&&t.getData().equals("(")))throw new CompileException("'(' expected.",t, parser.filename);
                        t = getToken();
                        int index = 1;
                        do {
                            if(t.getType()==Token.LP && t.getData().equals("(")){
                                vars.add(t);
                                index += 1;
                            }
                            if (t.getType()==Token.LR && t.getData().equals(")")&&index > 0){
                                index -=1;
                                vars.add(t);
                            }
                            if(t.getType()==Token.LR && t.getData().equals(")")&&index <= 0) {
                                for(Token tddebug:vars) {
                                    if (tddebug.getType()==Token.NAME || tddebug.getType()== Token.KEY) {
                                        break;
                                    }
                                }
                                break;
                            }
                            vars.add(t);
                            t = getToken();
                        } while (true);
                        t = getToken();
                        int i = 1;
                        if(!(t.getType()==Token.LP&&t.getData().equals("{")))throw new CompileException("Missing statement body.",t, parser.filename);
                        do{
                            t = getToken();
                            if(t.getType()==Token.LP&&t.getData().equals("{"))i+=1;
                            if(t.getType()==Token.LR&&t.getData().equals("}"))i-=1;
                            if(i == 0)break;
                            groups.add(t);
                        }while (true);
                        ExpressionParsing e = new ExpressionParsing(vars,parser,compiler);
                        return new WhileParser(e.calculate(e.transitSuffix()),new SubParser(groups,parser,compiler).getParsers());
                    }
                    case "back"-> {
                        ArrayList<Token> tds = new ArrayList<>();
                        do {
                            buf = getToken();
                            if(buf.getType() == Token.END)break;
                            tds.add(buf);
                        } while (true);
                        return new BackParser(tds);
                    }
                    case "return"->{
                        ArrayList<Token> tds = new ArrayList<>();
                        do {
                            buf = getToken();
                            if(buf.getType() == Token.END)break;
                            tds.add(buf);
                        } while (true);
                        return new ReturnParser(tds);
                    }
                    default -> throw new CompileException("Not a statement.",buf, parser.filename);
                }
            }else if(buf.getType()==Token.NAME){
                ArrayList<Token> tds = new ArrayList<>();
                tds.add(buf);
                do{
                    buf = getToken();
                    tds.add(buf);
                }while (buf.getType()!=Token.END);
                int a = 0; //1 invoke| 2 value
                for(Token t:tds){
                    if(t.getType()==Token.SEM&&t.getData().equals(".")){
                        a = 1;
                        break;
                    }
                }
                if(a == 0){
                    return new ExpParser(tds);
                }
                return new InvokeParser(tds);

            } else throw new CompileException("Illegal start of expression.",buf,parser.filename);
        }

        public ArrayList<BaseParser> getParsers(){
            ArrayList<BaseParser> bp = new ArrayList<>();
            try {
                while (true) bp.add(getParser());
            }catch (EOFException e){}
            return bp;
        }
    }
}
