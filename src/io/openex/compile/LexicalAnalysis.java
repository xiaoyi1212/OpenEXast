package io.openex.compile;

import io.openex.Main;
import io.openex.util.CompileException;

import java.util.ArrayList;

public class LexicalAnalysis {
    char[] arrays;
    int file_line, index;
    Character buf;
    String file_name;boolean isl = false;


    public LexicalAnalysis(ArrayList<String> data, String file_name) {
        StringBuilder sb = new StringBuilder();
        for (String s : data) {
            if (s.contains("//")) {
                sb.append(s.split("//")[0]).append("\n");
            } else sb.append(s).append("\n");
        }
        arrays = sb.toString().toCharArray();
        file_line = 1;
        index = 0;
        this.file_name = file_name;
    }

    private boolean isLP(int c) {
        return (c == '(' || c == '[' || c == '{');
    }

    private boolean isLR(int c) {
        return (c == ')' || c == ']' || c == '}');
    }

    private boolean isSpace(int c) {
        return ((char) c == ' ');
    }

    private boolean isKey(int c) {
        char a = (char) c;
        return (a >= 'a' && a <= 'z') || (a >= 'A' && a <= 'Z') || a == '_';
    }

    private boolean isNum(int c) {
        return Character.isDigit((char) c);
    }

    private boolean isSEM(int c) {
        return (c == ':') || (c == '!') || (c == '.') || (c == ',') || (c == '%') || (c == '&') || (c == '|') || (c == '$');
    }


    private Integer getChar() {
        if (buf != null) {
            char re = buf;
            buf = null;
            return (int) re;
        } else {
            if (index >= arrays.length) return null;
            char c = arrays[index];
            index += 1;
            return (int) c;
        }
    }

    private Token lex() throws NullPointerException {
        StringBuilder sb = new StringBuilder();
        int c;
        do {
            c = getChar();
        } while (isSpace(c));
        if (isSEM(c)) {
            sb.append((char) c);
            return new Token(Token.SEM, sb.toString(), file_line);
        } else if (isNum(c)) {
            boolean isdouble = false;
            do {
                sb.append((char) c);
                c = getChar();
                if (c == '.') isdouble = true;
            } while (isNum(c) || c == '.');
            if (isKey(c)) {
                do {
                    sb.append((char) c);
                    c = getChar();
                } while (isKey(c) || isNum(c));
                buf = (char) c;
                return new Token(Token.NAME, sb.toString(), file_line);
            }else if(c=='-'){
                isl = true;
            }
            buf = (char) c;
            if (isdouble) return new Token(Token.DOUBLE, sb.toString(), file_line);
            else return new Token(Token.INTEGER, sb.toString(), file_line);
        } else if (isKey(c)) {
            do {
                sb.append((char) c);
                c = getChar();
                if (isSEM(c)) break;
            } while (isKey(c) || isNum(c));
            buf = (char) c;
            if (Main.isKey(sb.toString())) return new Token(Token.KEY, sb.toString(), file_line);
            return new Token(Token.NAME, sb.toString(), file_line);
        } else if (c == '/') {
            sb.append((char) c);
            c = getChar();
            if (c == '*') {
                do {
                    do {
                        c = getChar();
                        sb.append((char) c);
                    } while (c != '*');
                    c = getChar();
                    sb.append((char) c);
                } while (c != '/');
                sb.deleteCharAt(0).deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1);
                return new Token(Token.TEXT, sb.toString(), file_line);
            } else return new Token(Token.SEM, "/", file_line);
        } else if (c == '*') return new Token(Token.SEM, "*", file_line);
        else if (c == '"') {
            do {
                c = getChar();
                if (c == '\n')
                    throw new CompileException("'\"' expected.", new Token(Token.STRING, sb.toString(), file_line), file_name);
                if (c == '\\') {
                    c = getChar();
                    if (c == 'n') {
                        sb.append("\n");
                    } else if (c == 't') {
                        sb.append("\t");
                    } else if (c == '\\') {
                        sb.append("\\");
                    } else if (c == '"') {
                        sb.append("\"");
                    } else
                        throw new CompileException("Illegal escape character in string literal.", new Token(Token.STRING, sb.toString(), file_line), file_name);
                    continue;
                }
                sb.append((char) c);
            } while (c != '"');
            sb.deleteCharAt(sb.indexOf("\""));
            return new Token(Token.STRING, sb.toString(), file_line);
        } else if (c == '=') {
            sb.append((char) c);
            c = getChar();
            if (c == '=' || c == '!') {
                sb.append((char) c);
                return new Token(Token.SEM, sb.toString(), file_line);
            }
            buf = (char) c;
            return new Token(Token.SEM, sb.toString(), file_line);
        } else if (c == '>' || c == '<') {
            sb.append((char) c);
            c = getChar();
            if (c == '=') {
                sb.append((char) c);
                return new Token(Token.SEM, sb.toString(), file_line);
            }
            buf = (char) c;
            return new Token(Token.SEM, sb.toString(), file_line);
        } else if (c == '+') {
            sb.append((char) c);
            c = getChar();
            if (c == '=') {
                sb.append((char) c);
                return new Token(Token.SEM, sb.toString(), file_line);
            }
            buf = (char) c;
            return new Token(Token.SEM, sb.toString(), file_line);
        } else if (c == '-') {
            sb.append((char) c);

            if(isl){
                isl = false;
                return new Token(Token.SEM, sb.toString(), file_line);
            }

            c = getChar();
            if (c == '=') {
                sb.append((char) c);
                return new Token(Token.SEM, sb.toString(), file_line);
            }else if (isNum(c)) {
                sb.append((char) c);
                return new Token(Token.INTEGER, sb.toString(), file_line);
            }
            buf = (char) c;
            return new Token(Token.SEM, sb.toString(), file_line);
        } else if (isLP(c)) {
            sb.append((char) c);
            return new Token(Token.LP, sb.toString(), file_line);
        } else if (isLR(c)) {
            sb.append((char) c);
            return new Token(Token.LR, sb.toString(), file_line);
        } else if (c == ';') return new Token(Token.END, "" + ((char) c), file_line);
        else if (c == '\n') {
            file_line += 1;
            return new Token(Token.LINE, "", file_line);
        } else {
            throw new CompileException("Unknown lex in file " + file_name + "(lines:" + file_line + "): >>" + ((char) c) + "<<", file_name);
        }
    }

    public ArrayList<Token> getTokens() {
        ArrayList<Token> tokens = new ArrayList<>();
        Token t;
        try {
            while ((t = lex()) != null) tokens.add(t);
        } catch (NullPointerException e) {
            return tokens;
        }
        return tokens;
    }
}
