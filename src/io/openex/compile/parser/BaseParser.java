package io.openex.compile.parser;

import io.openex.astvm.code.ByteCode;
import io.openex.compile.Compiler;
import io.openex.util.CompileException;

public interface BaseParser {
    public ByteCode eval(Parser parser, Compiler compiler) throws CompileException;
}
