hulu
====

A MVC 
一个MVC框架

## How to use

### 1. 引入依赖
现在Hulu MVC框架已经上传至Maven中央库，你可以在pom.xml中加入以下信息引入项目依赖包。

```xml
<dependency>
	<groupId>com.xiaoleilu</groupId>
	<artifactId>hulu</artifactId>
	<version>X.X.X</version>
</dependency>
```

### 2. 加入配置文件
在`src/main/resources/config/`下加入Hulu.setting文件，这个是Hulu框架的配置文件，配置内容请见[https://github.com/looly/hulu/blob/master/doc/config/hulu-example.setting](https://github.com/looly/hulu/blob/master/doc/config/hulu-example.setting)

### 3. 加入web.xml文件
按理说Servlet3之后不再需要web.xml，不过一些设置依旧需要这个文件，并且Hulu必须使用Servlet3做为其基础，在`/src/main/webapp/WEB-INF/`下放入web.xml文件，内容如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<web-app version="3.0" xmlns="http://java.sun.com/xml/ns/javaee"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
	http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd">
	
</web-app>
```

注：在Maven web项目中，必须有这个web.xml文件才会被认为是一个web项目。

### 4.添加逻辑处理类Action
在`Hulu.setting`中`action.package`设置好需要扫描的包路径（这里设置的包会被识别为Action类，用于处理用户请求的逻辑），如果你设置了`action.suffix`，那只会扫描指定后缀的逻辑处理类。

假如你的Action类叫做UserAction（扫描包后缀为Action），里面有个无参的public方法叫做create，那你请求`/user/create`时就会调用这个方法。在这个方法中你便可以使用`Request`类提取请求的参数，使用`Response`类设置响应的头和Cookie，使用`Render`返回内容给客户端。

### 5. 启动
项目写完后你可以使用`mvn package`命令打包项目并放入Servlet容器中运行，如果是测试项目功能，只需在pom.xml中引入Tomcat7插件既可使用`mvn tomcat7:run`测试运行。

```
<build>
	<plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-compiler-plugin</artifactId>
			<version>3.1</version>
			<configuration>
				<source>1.6</source>
				<target>1.6</target>
			</configuration>
		</plugin>
		<plugin>
			<groupId>org.apache.tomcat.maven</groupId>
			<artifactId>tomcat7-maven-plugin</artifactId>
			<version>2.2</version>
		</plugin>
	</plugins>
	<finalName>huludemo</finalName>
</build>
```

### 6. 框架的Demo项目
此MVC框架有一个简单的样例程序，请访问[https://github.com/looly/huludemo](https://github.com/looly/huludemo)查看。

### 7. 遇到问题？
在[https://github.com/looly/hulu/issues](https://github.com/looly/hulu/issues)给我提出意见和建议，欢迎参与讨论。
