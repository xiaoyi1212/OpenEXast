package io.openex.compile;

import io.openex.exe.node.struct.GroupASTNode;

public class TokenX extends Token {
    GroupASTNode bc;
    public TokenX(GroupASTNode bc){
        this.bc = bc;
    }

    public GroupASTNode getBc() {
        return bc;
    }

    @Override
    public int getType() {
        return Token.EXP;
    }

    @Override
    public String toString() {
        return bc.toString();
    }
}
