package io.openex.compile.parser;

import io.openex.exe.node.ASTNode;
import io.openex.compile.Compiler;
import io.openex.util.CompileException;

public interface BaseParser {
    public ASTNode eval(Parser parser, Compiler compiler) throws CompileException;
}
