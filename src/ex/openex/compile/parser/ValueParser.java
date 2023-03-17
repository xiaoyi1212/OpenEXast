package ex.openex.compile.parser;

import ex.openex.Main;
import ex.openex.astvm.code.ByteCode;
import ex.openex.astvm.code.opcode.PushCode;
import ex.openex.astvm.code.struct.GroupByteCode;
import ex.openex.astvm.code.struct.LoadArrayCode;
import ex.openex.astvm.code.struct.LoadVarCode;
import ex.openex.astvm.obj.ExBool;
import ex.openex.astvm.obj.ExNull;
import ex.openex.compile.CompileManager;
import ex.openex.compile.Compiler;
import ex.openex.compile.LexicalAnalysis;
import ex.openex.compile.expression.ExpressionParsing;
import ex.openex.exception.CompileException;

import java.util.ArrayList;

public class ValueParser implements BaseParser{

    ArrayList<LexicalAnalysis.Token> tds;
    private int index = 0;

    private LexicalAnalysis.Token getTokens(){
        if(index > tds.size())return null;
        LexicalAnalysis.Token td = tds.get(index);
        index += 1;
        return td;
    }


    public ValueParser(ArrayList<LexicalAnalysis.Token> tds){
        this.tds = tds;
    }

    @Override
    public ByteCode eval(Parser parser, Compiler compiler) throws CompileException {
        index = 0;
        String text;ArrayList<ByteCode> bcs = new ArrayList<>();
        LexicalAnalysis.Token name;
        LexicalAnalysis.Token td = getTokens();
        int type11;

        if(!(td.getType()==LexicalAnalysis.KEY))throw new CompileException("变量的访问权限修饰符不正确",td, parser.filename);
        if(td.getData().equals("local"))type11 = 1;
        else if(td.getData().equals("global"))type11 = 0;
        else throw new CompileException("未知的访问修饰符",td, parser.filename);

        td = getTokens();
        if(!(td.getType()==LexicalAnalysis.NAME))throw new CompileException("变量名类型不正确.",td, parser.filename);
        name = td;td = getTokens();
        if(!(td.getType()==LexicalAnalysis.SEM&&td.getData().equals(":")))throw new CompileException("未知的变量注释",td, parser.filename);
        td = getTokens();
        if(!(td.getType()==LexicalAnalysis.STRING)) throw new CompileException("未知的备注类型",td, parser.filename);
        text = td.getData();

        if(compiler.value_names.contains(name))throw new CompileException("不能定义具有相同名称的变量！.",name,parser.filename);

        td = getTokens();
        ArrayList<ByteCode> var_bc = new ArrayList<>();

        if(td.getType()==LexicalAnalysis.END){
            var_bc.add(new PushCode(new ExNull()));
        }else {
            if (!(td.getType() == LexicalAnalysis.SEM))
                throw new CompileException("变量初始值需要‘=’号", name, parser.filename);
            td = getTokens();


            if (td.getType() == LexicalAnalysis.KEY) {
                if (td.getData().equals("true")) var_bc.add(new PushCode(new ExBool(true)));
                else if (td.getData().equals("false")) var_bc.add(new PushCode(new ExBool(false)));
                else if (td.getData().equals("null")) var_bc.add(new PushCode(new ExNull()));
                else throw new CompileException("未知的初始值定义", td, parser.filename);
            } else if (td.getType() == LexicalAnalysis.END) {
                var_bc.add(new PushCode(new ExNull()));
            }else if(td.getType()==LexicalAnalysis.LP){
                boolean isend = true,isf = true;
                ArrayList<GroupByteCode> b = new ArrayList<>();
                do {
                    ArrayList<LexicalAnalysis.Token> tds = new ArrayList<>();
                    do {
                        td = getTokens();
                        if (td.getType() == LexicalAnalysis.LR && td.getData().equals("]")){
                            isend = false;
                            if(tds.size()==0&&isf){
                                compiler.value_names.add(name.getData());
                                return new LoadArrayCode(name.getData(),new ArrayList<>(),type11,0);
                            }
                            break;
                        }
                        if (td.getType() == LexicalAnalysis.SEM && td.getData().equals(",")) break;
                        tds.add(td);
                    } while (true);
                    ExpressionParsing p = new ExpressionParsing(tds,parser,compiler);
                    b.add(new GroupByteCode(p.calculate(p.transitSuffix())));
                    isf = false;
                }while (isend);
                compiler.value_names.add(name.getData());
                return new LoadArrayCode(name.getData(),b,type11,-1);
            } else {
                ArrayList<LexicalAnalysis.Token> t = new ArrayList<>();
                t.add(td);
                do {
                    td = getTokens();
                    if (td.getType() == LexicalAnalysis.END) break;
                    t.add(td);
                } while (true);
                ExpressionParsing p = new ExpressionParsing(t, parser, compiler);
                var_bc.addAll(p.calculate(p.transitSuffix()));
            }
        }

        compiler.value_names.add(name.getData());
        if(type11 == 0) CompileManager.value_names.add(name.getData());

        return new LoadVarCode(name.getData(),text,type11,var_bc);
    }
}
