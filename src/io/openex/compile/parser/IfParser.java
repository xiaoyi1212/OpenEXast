package io.openex.compile.parser;

import io.openex.astvm.code.ByteCode;
import io.openex.astvm.code.struct.IfCode;
import io.openex.compile.Compiler;
import io.openex.util.CompileException;

import java.util.ArrayList;

public class IfParser implements BaseParser{
    ArrayList<ByteCode> bool;
    ArrayList<BaseParser> group;
    ArrayList<BaseParser> else_group;

    public IfParser(ArrayList<ByteCode> bool,ArrayList<BaseParser> group,ArrayList<BaseParser> else_group){
        this.bool = bool;
        this.group = group;
        this.else_group = else_group;
    }


    @Override
    public ByteCode eval(Parser parser, Compiler compiler) throws CompileException {
        ArrayList<ByteCode> groups = new ArrayList<>(),else_groups = new ArrayList<>();
        for(BaseParser bp:group)groups.add(bp.eval(parser, compiler));
        for(BaseParser bp:else_group)else_groups.add(bp.eval(parser, compiler));

        return new IfCode(bool,groups,else_groups);
    }
}
