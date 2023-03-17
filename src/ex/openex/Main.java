package ex.openex;

import java.util.HashSet;

public class Main {
    public static final String compile_version = "OpenEX_ScriptCompile_AbstractSyntaxTree_v0.1.2";
    public static final String runtime_version = "OpenEX_ScriptRuntime_AbstractSyntaxTree_v0.1.0";
    static ScriptOutputStream output = new ScriptOutputStream();
    static HashSet<String> s = new HashSet<>();
    static {
        s.add("function");
        s.add("value");
        s.add("local");
        s.add("global");
        s.add("if");
        s.add("else");
        s.add("while");
        s.add("return");
        s.add("false");
        s.add("true");
        s.add("include");
        s.add("this");
        s.add("null");
        s.add("back");
    }
    public static boolean isKey(String ss){
        return s.contains(ss);
    }

    public static ScriptOutputStream getOutput() {
        return output;
    }

    public static void main(String[] args) {
        args = new String[]{"-filename:main.exf","-filename:yueyue.exf"};
        CommandManager.command(args);
    }
}
