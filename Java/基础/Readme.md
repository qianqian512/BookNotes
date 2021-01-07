####HashMap CPU 100%的问题

####synchronized优化内容，是如何升级的？
> JDK1.5时，synchonrized只有通过操作系统的mutex实现并发加锁；在JDK1.6以后借助Java对象头，引入了偏向锁、轻量级锁、重量级锁，用来减少锁竞争带来的上下文切换。  
> 升级过程：偏向锁 -> 轻量级锁 -> 重量级锁   
> 偏向锁优化目的：优化同一线程多次申请同一个锁的竞争，在某些情况下，大部分时间都是同一个线程竞争锁资源  
> 轻量级锁优化目的：

####ThreadLocal内部结构
> ThreadLocalMap作为Thread的一个成员变量，与Thread一起自生自灭，这样就保证了ThreadLocal的变量和Thread生命周期一致性。  
> ThreadLocalMap内部是一个继承自WeakReference的Entry数组，Key是ThreadLocal对象，value负责存值。

####ThreadLocal里的Entry为什么会被定义成WeakReference
> 目的是：ThreadLocal对象不再使用之后，ThreadLocal对象及其指向的T对象都应该可以被回收。

####Thread.sleep(0)的作用是什么
> 可以重新触发OS分片时间的操作，是平衡CPU控制权的一种方式。

####Java泛型擦除
> 编译器通过泛型对类型进行校验，但运行期会对泛型擦除，不记录类型