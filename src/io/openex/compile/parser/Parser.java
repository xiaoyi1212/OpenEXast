package io.openex.compile.parser;

import io.openex.compile.Compiler;
import io.openex.compile.LexicalAnalysis;
import io.openex.compile.expression.ExpressionParsing;
import io.openex.util.CompileException;

import java.io.EOFException;
import java.util.ArrayList;

public class Parser {
    ArrayList<LexicalAnalysis.Token> tds;
    int index;
    LexicalAnalysis.Token buffer;
    String filename;

    public String getFilename() {
        return filename;
    }

    public Parser(ArrayList<LexicalAnalysis.Token> tds, String filename){
        this.tds = tds;
        index = 0;
        this.filename = filename;
    }

    private LexicalAnalysis.Token getToken() throws EOFException {
        if(buffer != null){
            LexicalAnalysis.Token r = buffer;
            buffer = null;
            return r;
        }else {
            if(index >= tds.size())throw new EOFException();
            LexicalAnalysis.Token t = tds.get(index);
            index += 1;
            return t;
        }
    }

    public BaseParser getParser(Compiler c) throws EOFException {
        LexicalAnalysis.Token buf;
        buf = getToken();
        if(buf.getType()==LexicalAnalysis.KEY){
            switch (buf.getData()) {
                case "include" -> {
                    ArrayList<LexicalAnalysis.Token> tds = new ArrayList<>();
                    do {
                        buf = getToken();
                        tds.add(buf);
                    } while (buf.getType() != LexicalAnalysis.END);
                    return new IncludeParser(tds);
                }
                case "value" -> {
                    ArrayList<LexicalAnalysis.Token> tds = new ArrayList<>();
                    do {
                        buf = getToken();
                        tds.add(buf);
                    } while (buf.getType() != LexicalAnalysis.END);
                    return new ValueParser(tds);
                }
                case "function"->{
                    ArrayList<LexicalAnalysis.Token> vars = new ArrayList<>(),groups = new ArrayList<>();
                    LexicalAnalysis.Token t = getToken();
                    if(!(t.getType()==LexicalAnalysis.NAME))throw new CompileException("未知的函数名类型",t,getFilename());
                    String name = t.getData();
                    t = getToken();
                    if(!(t.getType()==LexicalAnalysis.LP&&t.getData().equals("(")))throw new CompileException("未知的参数起始",t,getFilename());
                    do{
                        t = getToken();
                        if(t.getType()==LexicalAnalysis.LR&&t.getData().equals(")"))break;
                        if(t.getType()==LexicalAnalysis.NAME)c.value_names.add(t.getData());
                        vars.add(t);
                    }while (true);
                    t = getToken();
                    int i = 1;
                    if(!(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{")))throw new CompileException("函数必须拥有函数体",t,getFilename());
                    do{
                        t = getToken();
                        if(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{"))i+=1;
                        if(t.getType()==LexicalAnalysis.LR&&t.getData().equals("}"))i-=1;
                        if(i == 0)break;
                        groups.add(t);
                    }while (true);

                    return new FunctionParser(name,vars,new SubParser(groups,this,c).getParsers());

                }
                case "if"->{
                    ArrayList<LexicalAnalysis.Token> vars = new ArrayList<>(),groups = new ArrayList<>(),else_g = new ArrayList<>();
                    LexicalAnalysis.Token t = getToken();

                    if(!(t.getType()==LexicalAnalysis.LP&&t.getData().equals("(")))throw new CompileException("未知的参数",t, filename);
                    t = getToken();
                    int index = 1;
                    do {
                        if(t.getType()==LexicalAnalysis.LP && t.getData().equals("(")){
                            vars.add(t);
                            index += 1;
                        }
                        if (t.getType()==LexicalAnalysis.LR && t.getData().equals(")")&&index > 0){
                            index -=1;
                            vars.add(t);
                        }
                        if(t.getType()==LexicalAnalysis.LR && t.getData().equals(")")&&index <= 0) {
                            boolean isnum = true;
                            for(LexicalAnalysis.Token tddebug:vars) {
                                if (tddebug.getType()==LexicalAnalysis.NAME || tddebug.getType()== LexicalAnalysis.KEY) {
                                    isnum = false;
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
                    if(!(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{")))throw new CompileException("if语句必须有代码体",t,getFilename());
                    do{
                        t = getToken();
                        if(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{"))i+=1;
                        if(t.getType()==LexicalAnalysis.LR&&t.getData().equals("}"))i-=1;
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
                    if(t.getType()==LexicalAnalysis.KEY&&t.getData().equals("else")){
                        t = getToken();
                        if(!(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{")))throw new CompileException("if语句必须有代码体",t,getFilename());
                        do{
                            t = getToken();
                            if(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{"))j+=1;
                            if(t.getType()==LexicalAnalysis.LR&&t.getData().equals("}"))j-=1;
                            if(j == 0)break;
                            else_g.add(t);
                        }while (true);
                    }else buffer = t;

                    return new IfParser(e.calculate(e.transitSuffix()),new SubParser(groups,this,c).getParsers(),new SubParser(else_g,this,c).getParsers());
                }case "while"->{
                    ArrayList<LexicalAnalysis.Token> vars = new ArrayList<>(),groups = new ArrayList<>();
                    LexicalAnalysis.Token t = getToken();

                    if(!(t.getType()==LexicalAnalysis.LP&&t.getData().equals("(")))throw new CompileException("未知的参数",t, this.filename);
                    t = getToken();
                    int index = 1;
                    do {
                        if(t.getType()==LexicalAnalysis.LP && t.getData().equals("(")){
                            vars.add(t);
                            index += 1;
                        }
                        if (t.getType()==LexicalAnalysis.LR && t.getData().equals(")")&&index > 0){
                            index -=1;
                            vars.add(t);
                        }
                        if(t.getType()==LexicalAnalysis.LR && t.getData().equals(")")&&index <= 0) {
                            boolean isnum = true;
                            for(LexicalAnalysis.Token tddebug:vars) {
                                if (tddebug.getType()==LexicalAnalysis.NAME || tddebug.getType()== LexicalAnalysis.KEY) {
                                    isnum = false;
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
                    if(!(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{")))throw new CompileException("while语句必须有代码体",t, this.filename);
                    do{
                        t = getToken();
                        if(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{"))i+=1;
                        if(t.getType()==LexicalAnalysis.LR&&t.getData().equals("}"))i-=1;
                        if(i == 0)break;
                        groups.add(t);
                    }while (true);
                    ExpressionParsing e = new ExpressionParsing(vars,this,c);
                    return new WhileParser(e.calculate(e.transitSuffix()),new SubParser(groups,this,c).getParsers());
                }
                default -> {
                    throw new CompileException("不是语句",buf,getFilename());
                }
            }
        }else if(buf.getType()==LexicalAnalysis.NAME) {
            ArrayList<LexicalAnalysis.Token> tds = new ArrayList<>();
            tds.add(buf);
            do {
                buf = getToken();
                tds.add(buf);
            } while (buf.getType() != LexicalAnalysis.END);
            int a = 0; //1 invoke| 2 value
            for (LexicalAnalysis.Token t : tds) {
                if (t.getType() == LexicalAnalysis.SEM && t.getData().equals(".")) {
                    a = 1;
                    break;
                }
            }
            if (a == 0) {
                return new SetValueParser(tds);
            }
            if (a == 1) {
                return new InvokeParser(tds);
            }

            return null;
        }else if(buf.getType()==LexicalAnalysis.SEM){
            return null;
        } else throw new CompileException("不正确的语句开头",buf,filename);
    }

    public static class SubParser{
        ArrayList<LexicalAnalysis.Token> tds;
        int index;
        Parser parser;
        Compiler compiler;
        LexicalAnalysis.Token buffer;

        public SubParser(ArrayList<LexicalAnalysis.Token> tds,Parser parser,Compiler compiler){
            this.tds = tds;
            index = 0;
            this.parser = parser;
            this.compiler = compiler;
        }

        private LexicalAnalysis.Token getToken() throws EOFException {
            if(buffer != null){
                LexicalAnalysis.Token r = buffer;
                buffer = null;
                return r;
            }else {
                if(index >= tds.size())throw new EOFException();
                LexicalAnalysis.Token t = tds.get(index);
                index += 1;
                return t;
            }
        }

        private BaseParser getParser() throws EOFException {

            LexicalAnalysis.Token buf;
            buf = getToken();
            if(buf.getType()==LexicalAnalysis.KEY){
                switch (buf.getData()) {
                    case "value" -> {
                        ArrayList<LexicalAnalysis.Token> tds = new ArrayList<>();
                        do {
                            buf = getToken();
                            tds.add(buf);
                        } while (buf.getType() != LexicalAnalysis.END);
                        return new ValueParser(tds);
                    }
                    case "if"->{
                        ArrayList<LexicalAnalysis.Token> vars = new ArrayList<>(),groups = new ArrayList<>(),else_g = new ArrayList<>();
                        LexicalAnalysis.Token t = getToken();

                        if(!(t.getType()==LexicalAnalysis.LP&&t.getData().equals("(")))throw new CompileException("未知的参数",t, parser.filename);
                        t = getToken();
                        int index = 1;
                        do {
                            if(t.getType()==LexicalAnalysis.LP && t.getData().equals("(")){
                                vars.add(t);
                                index += 1;
                            }
                            if (t.getType()==LexicalAnalysis.LR && t.getData().equals(")")&&index > 0){
                                index -=1;
                                vars.add(t);
                            }
                            if(t.getType()==LexicalAnalysis.LR && t.getData().equals(")")&&index <= 0) {
                                boolean isnum = true;
                                for(LexicalAnalysis.Token tddebug:vars) {
                                    if (tddebug.getType()==LexicalAnalysis.NAME || tddebug.getType()== LexicalAnalysis.KEY) {
                                        isnum = false;
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
                        if(!(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{")))throw new CompileException("if语句必须有代码体",t, parser.filename);
                        do{
                            t = getToken();
                            if(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{"))i+=1;
                            if(t.getType()==LexicalAnalysis.LR&&t.getData().equals("}"))i-=1;
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
                        if(t.getType()==LexicalAnalysis.KEY&&t.getData().equals("else")){
                            t = getToken();
                            if(!(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{")))throw new CompileException("if语句必须有代码体",t, parser.filename);
                            do{
                                t = getToken();
                                if(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{"))j+=1;
                                if(t.getType()==LexicalAnalysis.LR&&t.getData().equals("}"))j-=1;
                                if(j == 0)break;
                                else_g.add(t);
                            }while (true);
                        }else buffer = t;

                        return new IfParser(e.calculate(e.transitSuffix()),new SubParser(groups,parser,compiler).getParsers(),new SubParser(else_g,parser,compiler).getParsers());
                    }
                    case "while"->{
                        ArrayList<LexicalAnalysis.Token> vars = new ArrayList<>(),groups = new ArrayList<>();
                        LexicalAnalysis.Token t = getToken();

                        if(!(t.getType()==LexicalAnalysis.LP&&t.getData().equals("(")))throw new CompileException("未知的参数",t, parser.filename);
                        t = getToken();
                        int index = 1;
                        do {
                            if(t.getType()==LexicalAnalysis.LP && t.getData().equals("(")){
                                vars.add(t);
                                index += 1;
                            }
                            if (t.getType()==LexicalAnalysis.LR && t.getData().equals(")")&&index > 0){
                                index -=1;
                                vars.add(t);
                            }
                            if(t.getType()==LexicalAnalysis.LR && t.getData().equals(")")&&index <= 0) {
                                boolean isnum = true;
                                for(LexicalAnalysis.Token tddebug:vars) {
                                    if (tddebug.getType()==LexicalAnalysis.NAME || tddebug.getType()== LexicalAnalysis.KEY) {
                                        isnum = false;
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
                        if(!(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{")))throw new CompileException("while语句必须有代码体",t, parser.filename);
                        do{
                            t = getToken();
                            if(t.getType()==LexicalAnalysis.LP&&t.getData().equals("{"))i+=1;
                            if(t.getType()==LexicalAnalysis.LR&&t.getData().equals("}"))i-=1;
                            if(i == 0)break;
                            groups.add(t);
                        }while (true);
                        ExpressionParsing e = new ExpressionParsing(vars,parser,compiler);
                        return new WhileParser(e.calculate(e.transitSuffix()),new SubParser(groups,parser,compiler).getParsers());
                    }
                    case "back"-> {
                        ArrayList<LexicalAnalysis.Token> tds = new ArrayList<>();
                        do {
                            buf = getToken();
                            if(buf.getType() == LexicalAnalysis.END)break;
                            tds.add(buf);
                        } while (true);
                        return new BackParser(tds);
                    }
                    case "return"->{
                        ArrayList<LexicalAnalysis.Token> tds = new ArrayList<>();
                        do {
                            buf = getToken();
                            if(buf.getType() == LexicalAnalysis.END)break;
                            tds.add(buf);
                        } while (true);
                        return new ReturnParser(tds);
                    }
                    default -> throw new CompileException("不是语句",buf, parser.filename);
                }
            }else if(buf.getType()==LexicalAnalysis.NAME){
                ArrayList<LexicalAnalysis.Token> tds = new ArrayList<>();
                tds.add(buf);
                do{
                    buf = getToken();
                    tds.add(buf);
                }while (buf.getType()!=LexicalAnalysis.END);
                int a = 0; //1 invoke| 2 value
                for(LexicalAnalysis.Token t:tds){
                    if(t.getType()==LexicalAnalysis.SEM&&t.getData().equals(".")){
                        a = 1;
                        break;
                    }
                }
                if(a == 0){
                    return new SetValueParser(tds);
                }
                if(a == 1){
                    return new InvokeParser(tds);
                }

                return null;
            } else throw new CompileException("不正确的语句开头",buf,parser.filename);
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
