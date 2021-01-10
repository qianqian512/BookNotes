####HashMap CPU 100%的问题

####synchronized优化内容，是如何升级的？
> JDK1.5时，synchonrized只有通过操作系统的mutex实现并发加锁；在JDK1.6以后借助Java对象头，引入了偏向锁、轻量级锁、重量级锁，用来减少锁竞争带来的上下文切换。    
> 偏向锁优化目的：优化同一线程多次申请同一个锁的竞争，在某些情况下，大部分时间都是同一个线程竞争锁资源    
> 轻量级锁优化目的：使用CAS方式替换mutex竞争锁，减少从用户态切换到内核态带来的开销(轻量级锁一般都是配合自旋方式使用)。  
> 自旋锁的目的：避免轻量级锁抢占失败直接升级成mutex锁，而是通过一定数量的轮询来占用CPU时间片尝试抢占锁，如果达到次数仍未拿到则再使用mutex锁。(值得一提的是：-XX:PreBlockSpin只对synchronized的自旋锁有效，对于JUC包下的CAS实现的不受管控)  
> 锁消除：当JVM检测到不可能存在共享数据竞争，就会对锁进行消除处理。JVM检测到单线程内使用StringBuffer或Vector等工具时，会判断对象是否逃逸，未能逃逸出方法则会对锁进行消除。  
> 锁粗化：将多个连续加锁解锁的操作连在一起，形成一个大段的临界区，减少加解锁带来的性能损耗。  
> 升级过程：偏向锁 -> 轻量级锁 -> 重量级锁(配合自适应自旋锁)   

####ThreadLocal内部结构
> ThreadLocalMap作为Thread的一个成员变量，与Thread一起自生自灭，这样就保证了ThreadLocal的变量和Thread生命周期一致性。  
> ThreadLocalMap内部是一个继承自WeakReference的Entry数组，Key是ThreadLocal对象，value负责存值。

####ThreadLocal里的Entry为什么会被定义成WeakReference
> 目的是：ThreadLocal对象不再使用之后，ThreadLocal对象及其指向的T对象都应该可以被回收。  
> <font color="blue">待补充</font>  
> 参考：https://blog.csdn.net/cyxinda/article/details/93709143

####Thread.sleep(0)的作用是什么
> 可以重新触发OS分片时间的操作，是平衡CPU控制权的一种方式。

####Java泛型擦除
> 编译器通过泛型对类型进行校验，但运行期会对泛型擦除，不记录类型信息

####如何进行反射，如何提高反射的性能
> 1.创建实例时，Class.forName比较耗时，避免每次构造对象都使用Class.forName构建ClassType实例；
> 2.反射调用方法时，method.setAccessible(true)，可以提高将近一半效率，原因是在调用Invoke方法时会执行安全检查，这个操作会比较耗时；  
> 3.直接饮用第三方类库，使用字节码增强机制。  

####suspend和resume为什么会被废弃
> 相比替代方案wait和notify而言，suspend和resume最大问题是非线程安全的，只能适用于没有synchronized的场景。

```
	public static void main(String[] args) throws IOException {

		Object lock = new Object();

		Thread t1 = new Thread(() -> {
			synchronized (lock) {
				System.out.println("wait message...");
				// 调用线程的suspend方法，并不会导致lock对象释放锁，这样线程2就永远无法进入临界区
				Thread.currentThread().suspend();
				System.out.println("received message");
			}
		});

		t1.start();

		new Thread(() -> {
			synchronized (lock) {
				// 线程2永远也无法进入这里
				System.out.println("enter");
				t1.resume();
				System.out.println("sended message");
			}
		}).start();

		System.in.read();
	}
```

####如何使用interrupt方法
> interrupt并不能直接中断线程运行，而是设置了一个中断标记，然后在线程执行的过程中来进行check，自行确定是否要中断。使用volatile的状态的标记也可以作为替代方案。	

```
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Thread t1 = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
			}
			System.out.println("t1 exit");
		});
		t1.start();
		
		new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			t1.interrupt();
			System.out.println("t2 interrupt t1");
		}).start();
	}
```

####java中线程的状态机
>![](JavaThreadState.jpeg)  