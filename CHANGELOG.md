# Changelog

## 1.5.3

### 新特性

* 更新路径逻辑，使之支持Action类上的注解
* View添加toString
* View增加File的返回值
* 升级依赖以及Maven插件
* Render增加JSON对象支持
* 增加demo
* 重构View部分和Render类
* Action方法增加参数注入支持
* 自定义启动提示消息
* 添加Action类返回值String前缀支持
* HttpRequest注入添加是否忽略注入错误选项

### Bug修复

* 修复DefaultRender丢失内容的错误
* 修复Action方法中参数注入问题
* 修复500错误页面格式问题
* 请求完成清理ThreadLocal，防止内存溢出