package io.openex.compile;

public class Token {
    public static final int INTEGER = 0,
            DOUBLE = 1,
            SEM = 2,
            STRING = 3,
            KEY = 4,
            NAME = 5,
            TEXT = 6,
            LP = 7,
            LR = 8,
            LINE = 9,
            END = 10,
            EXP = 11;
    int type;
    String data;
    int line;

    public Token(){}

    public Token(int type, String data, int line) {
        this.type = type;
        this.data = data;
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public int getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return type + "|" + data;
    }
}