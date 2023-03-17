package ex.openex.astvm.obj;

public class ExDouble extends ExObject{
    double data;
    public ExDouble(double data){
        this.data = data;
    }
    @Override
    public String getData() {
        return data+"";
    }

    @Override
    public int getType() {
        return ExObject.DOUBLE;
    }

    @Override
    public String toString() {
        return "[DOUBLE]"+data;
    }
}
