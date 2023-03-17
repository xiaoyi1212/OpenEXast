package ex.openex.compile.parser;

import ex.openex.astvm.code.ByteCode;
import ex.openex.compile.Compiler;
import ex.openex.exception.CompileException;

public interface BaseParser {
    public ByteCode eval(Parser parser, Compiler compiler) throws CompileException;
}
