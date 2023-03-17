package ex.openex;

public class ScriptOutputStream {
    public void info(String info){
        System.out.println(info);
    }
    public void warn(String info){
        System.out.println("[WARN]"+info);
    }
    public void error(String error){
        System.err.println(error);
    }
}
