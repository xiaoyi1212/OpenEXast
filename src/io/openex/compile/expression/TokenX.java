package io.openex.compile.expression;

import io.openex.astvm.code.struct.GroupByteCode;
import io.openex.compile.LexicalAnalysis;

public class TokenX extends LexicalAnalysis.Token {
    GroupByteCode bc;
    public TokenX(GroupByteCode bc){
        this.bc = bc;
    }

    public GroupByteCode getBc() {
        return bc;
    }

    @Override
    public int getType() {
        return LexicalAnalysis.EXP;
    }
}
