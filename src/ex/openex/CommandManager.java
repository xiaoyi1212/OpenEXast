package ex.openex;

import ex.openex.compile.CompileManager;

import java.util.ArrayList;

public class CommandManager {
    static ArrayList<String> filename = new ArrayList<>();
    public static void command(String[] args){

        if(args.length == 0){
            Main.getOutput().info("-filename:<文件名> //编译一个脚本\n" +
                    "CompilerVersion:"+Main.compile_version+"\n" +
                    "RuntimeVersion:"+Main.runtime_version);
            return;
        }

        for(String str:args){
            if(str.contains("-filename:")){
                filename.add(str.split(":")[1]);
            }
        }
        if(filename.size() > 0) CompileManager.compile(filename);
    }
}
