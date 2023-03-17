package ex.openex.astvm.obj;

public class ExNull extends ExObject{
    @Override
    public String getData() {
        return "null";
    }

    @Override
    public int getType() {
        return ExObject.NULL;
    }

    @Override
    public String toString() {
        return "[NULL]";
    }
}
