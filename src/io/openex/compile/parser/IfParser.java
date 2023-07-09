package io.openex.compile.parser;

import io.openex.exe.node.ASTNode;
import io.openex.exe.node.struct.IfNode;
import io.openex.compile.Compiler;
import io.openex.util.CompileException;

import java.util.ArrayList;

public class IfParser implements BaseParser{
    ArrayList<ASTNode> bool;
    ArrayList<BaseParser> group;
    ArrayList<BaseParser> else_group;

    public IfParser(ArrayList<ASTNode> bool, ArrayList<BaseParser> group, ArrayList<BaseParser> else_group){
        this.bool = bool;
        this.group = group;
        this.else_group = else_group;
    }


    @Override
    public ASTNode eval(Parser parser, Compiler compiler) throws CompileException {
        ArrayList<ASTNode> groups = new ArrayList<>(),else_groups = new ArrayList<>();
        for(BaseParser bp:group)groups.add(bp.eval(parser, compiler));
        for(BaseParser bp:else_group)else_groups.add(bp.eval(parser, compiler));

        return new IfNode(bool,groups,else_groups);
    }
}

