package ex.openex.astvm.code.opcode;

import ex.openex.astvm.exe.Executor;
import ex.openex.astvm.obj.ExBool;
import ex.openex.astvm.obj.ExObject;
import ex.openex.astvm.obj.ExValue;
import ex.openex.astvm.obj.ExVarName;
import ex.openex.astvm.thread.ThreadManager;
import ex.openex.exception.VMRuntimeException;

public class LessCode extends OpCode{
    @Override
    public void executor(Executor executor) throws VMRuntimeException {
        ExObject obj = valueof(executor.pop(),executor);
        ExObject obj1 = valueof(executor.pop(),executor);


        if(obj.getType()==ExObject.STRING||obj1.getType()==ExObject.STRING) throw new VMRuntimeException("字符串不能参与比较运算",executor.getThread());
        if(obj.getType()==ExObject.BOOLEAN||obj1.getType()==ExObject.BOOLEAN)executor.push(new ExBool(false));

        if(obj.getType()==ExObject.DOUBLE||obj1.getType()==ExObject.DOUBLE) {
            executor.push(new ExBool(Double.parseDouble(obj1.getData()) < Double.parseDouble(obj.getData())));
        }else if(obj.getType()==ExObject.INTEGER&&obj1.getType()==ExObject.INTEGER){
            executor.push(new ExBool(Integer.parseInt(obj1.getData()) < Integer.parseInt(obj.getData())));
        }
    }

    private ExObject valueof(ExObject o,Executor executor) throws VMRuntimeException {
        if(o instanceof ExVarName){
            ExValue buf = null;
            for(ExValue v: ThreadManager.getValues()){
                if(v.getData().equals(o.getData())){
                    buf = v;
                    break;
                }
            }
            for(ExValue v:executor.getExecuting().getValues()){
                if(v.getData().equals(o.getData())){
                    buf = v;
                    break;
                }
            }
            if(buf == null)throw new VMRuntimeException("找不到指定变量:"+o.getData(),executor.getThread());

            if(buf.getType()==ExObject.ARRAY){
                o = buf;
            }else o = buf.getVar();
            return o;
        }else return o;
    }
}
