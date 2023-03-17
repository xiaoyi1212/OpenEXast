# OpenEXScript
* EX编程语言全新AST系列

<hr>

<p>我们使用GPLv2.0协议进行开源,源代码供参考和学习使用. OpenEX是一个编译解释一体化的脚本语言，其目的并不是为了嵌入式开发或者程序设计，而是供于初学者参考学习：如何自制一个编程语言,我们不建议您使用该语言写程序</p>

<hr>

## OpenEX使用更改
* 新版本名称规则更改:
> 解释器与编译器版本分离:
>> 解释器OpenEX_ScriptRuntime_AbstractSyntaxTree_v(版本号)\
>> 编译器OpenEX_ScriptCompile_AbstractSyntaxTree_v(版本号)

* 新版指令参数:
> -filename:文件名 '添加一个脚本文件'
* 新版语法升级:
```js
value local name:"增加了函数+算式的初始值" = exe.system.memory()+1;
value local arrays:"将OpenEXJE的list列表改为数组" = ["H",123,true,null,name];

//新版函数调用
system.print(exe.system.memory()*2,3*(23+4));

//新版修复了多级else嵌套BUG
//新版return变为万能退出语句,你可以终止if else块 while块以及function块的执行
return 值;
```
* 新版外部库API更换
> 主类继承<code>ex.exvm.plugin.NativePlugin</code>抽象类并实现其方法\
> 函数实现直接继承<code>ex.exvm.lib.NativeFunction</code>抽象类并实现其方法\
> 配置文件格式不变

<hr>

## OpenEX内部更改

### 编译器
* 修改了Parser语法解析器架构,将OpenEXJE的BasicParser拆成很多小Parser对单一语句进行解析
* 修改了算式解析器的架构,方便处理函数参数,变量参数初始值的新版语法
* 删除了原先字节码的概念，由解释器直接执行语法树
* 编译器整体被归到<code>ex.openex.compile</code>包下

### 解释器
* Executor架构更新,直接执行语法树
* Lib加载器架构重写
* 解释器整体被归到<code>ex.openex.astvm</code>包下

<hr>

## 历史更新

* v0.1.0-将原OpenEXJE代码移植,并更新项目
* v0.1.2-修复若干解析BUG，并增加了type array string库
