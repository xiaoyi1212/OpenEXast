package ex.openex.compile.parser;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.code.struct.WhileCode;
import ex.openex.compile.Compiler;
import ex.openex.exception.CompileException;

import java.util.ArrayList;

public class WhileParser implements BaseParser{
    ArrayList<ByteCode> bool;
    ArrayList<BaseParser> parsers;

    public WhileParser(ArrayList<ByteCode> bool,ArrayList<BaseParser> parsers){
        this.bool = bool;
        this.parsers = parsers;
    }

    @Override
    public ByteCode eval(Parser parser, Compiler compiler) throws CompileException {
        ArrayList<ByteCode> b = new ArrayList<>();
        for(BaseParser p:parsers)b.add(p.eval(parser,compiler));
        return new WhileCode(bool,b);
    }
}
