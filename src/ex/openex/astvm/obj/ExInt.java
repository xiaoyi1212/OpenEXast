package ex.openex.astvm.obj;

public class ExInt extends ExObject{
    int data;
    public ExInt(int data){
        this.data = data;
    }

    @Override
    public String getData() {
        return data+"";
    }

    @Override
    public int getType() {
        return ExObject.INTEGER;
    }

    @Override
    public String toString() {
        return "[INT]:"+data;
    }
}
