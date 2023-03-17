package ex.openex.astvm.code.struct;

import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.exe.Executor;
import ex.openex.astvm.obj.ExObject;
import ex.openex.exception.VMRuntimeException;

import java.util.ArrayList;

public class WhileCode extends StructCode{
    ArrayList<ByteCode> bool,group;
    public WhileCode(ArrayList<ByteCode> bool,ArrayList<ByteCode> group){
        this.bool = bool;
        this.group = group;
    }
    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        while (true){
            for(ByteCode b:bool){
                b.executor(executor);
            }


            ExObject obj = executor.pop();

            if(!Boolean.parseBoolean(obj.getData()))break;

            for(ByteCode b:group){
                if(b instanceof BackCode)return;

                b.executor(executor);
            }
        }
    }
}
