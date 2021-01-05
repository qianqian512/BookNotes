####Java中的类加载机制
> JVM在装在class时共分为5步：  
> ① 加载：将class的数据结构在「方法区」初始化；并在Heap中创建Class对象  
> ② 验证：class装载到内存后，JVM会对其进行验证，其中包括「文件格式验证」 、「元数据验证」、「字节码验证」和「符号引用验证」
> ③ 准备：初始化成员变量的零值以及常量数据。  
> ④ 解析：将符号引用替换成直接应用 <font color="red">(待补充)</font>  
> ⑤ 初始化：初始化成员变量，执行构造方法。

####Java中都有哪些类加载器
> Java中默认一共有3个ClassLoader，分别是BootStrapClassLoader、ExtensionClassLoader和ApplicationClassLoader。  
> BootStrapClassLoader：最上层加载器，加载rt.jar  
> ExtensionClassLoader：加载<JAVA_HOME>/lib/ext目录的jar  
> ApplicationClassLoader：加载用户路径的jar  
> 3个ClassLoader主要是分工不一样，各自负责不同的区域，另外也是为了实现双亲委托模型。

####Java中的双亲委派模型有什么用
> ClassLoader在加载class时，会优先让上层加载器来加载，如果上层无法加载，则再移交给下层处理。这样有一个好处就是能保证JVM运行稳定，例如用户自己定义了一个java.lang.Object，但真正的java.lang.Object实际在rt.jar中，因此无论是使用哪个加载器，最终都是返回的rt.jar中的Object，从而保证整个加载体系稳定。

####破坏双亲委派模型

####Tomcat是如何利用双亲委派模型的

####简单介绍一下JMM

####JVM中用到的垃圾回收算法

####Java中的垃圾回收器比较

####强、软、弱、虚引用

####排查OOM的思路

####调优JVM的思路