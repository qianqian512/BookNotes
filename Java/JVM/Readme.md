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

####Tomcat为什么要打破双亲委派模型，以及破坏方式
>【原因】为了隔离多个应用之间的jar包，例如webapps下部署了project1和project2，且两个项目分别应用了spring3.0和spring4.0版本，倘若不进行隔离，两个项目不同版本的spring的class文件就会相互干扰。
>【原理】Tomcat在继承ClassLoader时，实现loadClass方法时可以选择性的将class交给parent处理。基于这点Tomcat会很对每一个war包，都会自动生成一个类加载器，专门用来加载这个war包，而这个类加载器打破了双亲委派机制。

####简单介绍一下JMM
> JMM是一个标准与规范，用于屏蔽掉硬件和操作系统访问内存的差异，让Java在各个平台中都能达到一致的并发效果。下面不按顺序简单描述一下JMM大概定义的范畴：    
> JMM将内存划分为主存和工作内存  
> 每个线程都有属于自己的工作内存，线程对变量的赋值读取操作都需要在工作内存中进行，不可以直接操作主存？    
> 线程在对主存变量副本处理完成后，再刷新回主存  
> 基本类型的变量都在工作内存中，引用类型在堆中创建，但对象引用也在工作内存中。 
> 遵守happen-before规则   

####JMM是通过什么方式禁止指令重排序的
> 通过设置内存屏障(Memory Barrier)  
> Memory Barrier的语义是：① 不管是任何指令，都不能和插入有Memory Barrier的指令重新排序。② 遇到Memory Barrier时，将工作内存的副本强制刷到主存。

####synchonrized内的代码块指令是否能够重新排序？
> 临界区内的指令还是可以重新排序。  

####JVM的对象头
> Java对象头包括2部分信息：ClassType信息和MarkWord
> ClassType占用32/64位
> MarkWorld占用32/64位，主要记录锁信息、年龄、HashCode等信息

####为什么会有新生代
> 为了减少整体GC停顿时间，如果不分代，所有对象都在同一个区域，每次GC都会对全堆进行扫描，存在效率问题。分代以后可以针对不同的内存区域，使用不同的回收算法来提升效率。

####新生代为什么采用复制算法
> 新生代对象大部分都是朝生夕死，90%的对象可以被快速回收。相比标记整理算法，复制算法避免了频繁的调整内存碎片。

####新生代为什么有2个S区
> 始终保证S0和S1有一个空的，用来存储临时对象，用于交换空间的目的。如果只有一个S区，在GC填充完S区后，为了保证下次S区可用，需要再清空S区，将S区存活对象移动到Eden区，这种方式会多一次移动操作，增加了MinorGC时间。对应MinorGC而言非常频繁，因此每次增加很少的时间，宏观上也会损耗放大。

####新生代实际可用空间怎么算
新生代分为Eden+S0+S1区，实际可用空间其实是Eden+任意S区，默认情况比例是8:1:1，因此最终实际可用空间就是实际配置的90%。

####Eden区是如何加速分配内存的
> 1.bump-the-pointer：相比malloc维护的free_list，“bump-the-pointer”可以直接在尾部追加即可，这种机制得益于Eden区采用复制清除算法，没有内存碎片的场景。   
> 2.TLAB技术：在Eden区为每个线程分配一块区域，减少内存分配时的锁冲突，加快内存分配速度。

####新生代GC过程
> 1.当Eden区域被占满，会触发"young gc"或者叫"minor gc"  
> 2.查找GC Roots，将其引用的对象copy到S1区，经历本环节后，Eden区和S0区都为空，S1区保留了存活对象，如果Eden存活对象过多，S区放不下时，就会将存活对象提前晋升到Old区。   
> 3.第二次触发GC时，查找Eden区和S1区的存活对象，放到S0区，如果对象过多依旧是提前晋升到Old区，经历完本环节Eden去和S1区都是空。

####GC Roots都有哪些
> 1、虚拟机（JVM）栈中引用对象（这里往往代表着还没有执行结束的线程引用的对象）   
> 2、方法区中的类静态属性引用对象    
> 3、方法区中常量引用的对象（final 的常量值）  
> 4、本地方法栈JNI的引用对象   

####堆中的对象，是否需要copy到工作内存中修改？

####JVM中用到的垃圾回收算法
标记整理：标记->删除->整理碎片，适用于老年代。
标记复制：标记->将存活对象移动到另一段内存空间，适用于GC后存活较少的场景，例如新生代。

####Java中的垃圾回收器比较

####强、软、弱、虚引用
> 1.强引用：无论在任何时候，都不会被GC掉
> 2.软引用：当GC后发现内存仍然不足，此时会清理软引用。	
> 3.弱引用：只要发生GC就会清理掉弱引用，通过WeakReferenceQueue可以监听到被GC事件，例如用来监听Direct内存被回收的场景。使用场景：ThreadLocal中Map的Entry，Tomcat中的ConcurrentCache，Dubbo的com.alibaba.dubbo.common.bytecode.Proxy  
> 4.虚引用：

####参考资料
> JMM和底层实现原理: https://www.jianshu.com/p/8a58d8335270  