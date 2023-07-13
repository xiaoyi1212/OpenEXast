# OpenEXScript
* EX编程语言全新AST系列

<hr>

<p>我们使用GPLv2.0协议进行开源,源代码供参考和学习使用. OpenEX是一个编译解释一体化的脚本语言，其目的并不是为了嵌入式开发或者程序设计，而是供于初学者参考学习：如何自制一个编程语言,我们不建议您使用该语言写程序</p>

<hr>

> 注:当程序是由万载县幻梦互联网服务工作室所使用时,将改为遵循 MIT 开源协议
> 
> 例如:
> 
> xX7912Xx 遵循mit协议闭源编写了个软件,以xX7912Xx所发行软件则视为违反本项目所要求的GPL2.0的开源协议。
> 
> 但是 xX7912Xx 是以 万载幻梦互联网服务工作室 的身份所发行的软件,则视为有效的。
>
> 当用户使用由万载幻梦互联网服务工作室所发行的软件中,其下载了本程序作为依赖,该程序不需要遵守GPL2.0协议,但是你如果修改了这个依赖程序(例如:自己打包替换),这种情况就视为GPL2.0了(主程序下载的与您使用的不一致。),这种情况下你必须开源你所修改的依赖程序并遵循GPL2.0协议。
>
> 下面是使用了 OpenEXScript 的例子:
>
> DotCS(Apache License2.0协议) 是由 万载县幻梦互联网服务工作室 所开发的一款软件,这里的版本在没说明的情况下均是万载县幻梦互联网服务工作室所说明的。
> 此软件使用了 OpenEXScript ,由于发行者是 万载县幻梦互联网服务工作室,因此在DotCS中下载 OpenEXScript 后是合法的。
> 但是当这个dotcs是由用户自己编译时,由于DotCS 本身是 Apache License2.0协议,与 OpenEXScript 的 GPLv2.0 冲突,因此你不能在 DotCS 中调用,下载,复制,上传等行为,哪怕你在DotCS中使用的是万载县幻梦互联网服务工作室的官方下载源都不行。您必须移除有关 OpenEXScript 的代码。或 遵循 GPLv2.0 开源 以及原先的 Apache License2.0协议的要求。


## OpenEX使用更改
* 新版本名称规则更改:
> 解释器与编译器版本分离:
>> 解释器OpenEX_ScriptRuntime_AbstractSyntaxTree_v(版本号)\
>> 编译器OpenEX_ScriptCompile_AbstractSyntaxTree_v(版本号)

* 新版指令参数:
> -filename 文件名 '添加一个脚本文件'
* 新版语法升级:
```js
value name:"增加了函数+算式的初始值" = system.memory()+1;
value arrays:"将OpenEXJE的list列表改为数组" = ["H",123,true,null,name];

//新版函数调用
system.print(system.memory()*2,3*(23+4));

//新版修复了多级else嵌套BUG
//新版return变为万能退出语句,你可以终止if else块 while块以及function块的执行
return 值;
```
* 新版外部库API更换
> 主类继承<code>io.openex.plugin.NativePlugin</code>抽象类并实现其方法\
> 函数实现直接继承<code>io.openex.exe.lib.NativeFunction</code>抽象类并实现其方法\
> 配置文件格式不变

<hr>

## OpenEX内部更改

### 编译器

* 包格式更新,由<code>ex.openex.compile</code>更改为<code>io.openex.compile<code>

<hr>

## 历史更新

* v0.1.0-将原OpenEXJE代码移植,并更新项目
* v0.1.2-修复若干解析BUG，并增加了type array string库
* v0.1.3-优化了部分代码
* v0.1.4-加入了Statement语句分割机制
* v0.1.5-优化了一些表达式分割机制
* v0.1.5-因语句分割BUG过多，裁掉并采用原语法语义解析方法,并将该机制移到Plus版本
* v0.1.6-优化了数组值获取
* v0.1.7-废除<code>global local</code>作用域,变量默认为<code>local</code>
* v0.1.8-彻底废除语法中的<code>global local</code>,但将其作为保留字处理
* v0.1.9-优化了表达式处理,沿用了Plus版算术与逻辑混合的表达式架构
* v0.2.0-更改大部分语法,优化了编译器以及解释器的执行效率
* v0.2.1-修复了表达式错误解析BUG
* v0.2.2-更改了包的结构
* v0.2.3-优化了比较执行节点的执行效率
* v0.2.4-修复了函数调用的错误解析
* v0.2.5-加入了WebSocket通信协议网络库
* v0.2.6-采用joptsimple做为命令行解析
* v0.2.7-加入了数组语法糖
* v0.2.8-修复了`<` 和 `>`无效的BUG
* v0.2.9-加入了JAR本地实现库的加载

## 致谢
<p>感谢你使用OpenEX-AST系列版本,在<code>v0.2.9</code>版本是OpenEXAST系列的最后一个版本，我们即将过渡到OpenEX-JavaEditionPlus版本，感谢您的使用.如果后续出现重大漏洞也不会进行修复，请使用JEP版本解决您的问题</p>

