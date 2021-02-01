##Dubbo扩展点

#### SPI机制

#### ExtensionLoader相比JDK的SPI做了哪些改进
> 1.避免一次性实例化所有实例  
> 2.避免了JDK的SPI吞异常的现象  
> 3.支持AOP与IOC  
> 4.增加了Adaptive和Activate功能

#### Dubbo的Wrapper是如何创建的，有什么用（例如ProtocolFilterWrapper）

#### Dubbo是如何做到字节码增强的

#### Dubbo的Exporter是如何发布服务的

#### Dubbo自适应机制
> 1.使用@Adaptive来标注自适应模式，注解可以作用于Class上也可以作用于Method上。  
> 2.在Class上使用，意为当前Class是接口的唯一自适应实现类，即可通过getAdatpiveExtension直接过得实现类实例对象。  
> 3.如果在调用getAdaptiveExtension时，如果Interface下的众多实现中，没有任何@Adaptive的实现类，此时会通过字节码增强模式，来生成一个自适应实现类：ExtensionLoader.createAdaptiveExtensionClass。  
注：getAdaptiveExtension生成自适应实现类还有一个前提，即Interface内部至少要有一个被@Adatpive标注的Method  
> 4.关于@Adaptive的value的使用：作为动态适配实现的一种方式，根据value定义的优先顺序，选择一个合适的实现类，需要注意的是，protocol是关键字，意为获取URL的protocol字段，其他值，都是从url的Parameter中获取。
> 5.@Adaptive对生成源码的影响，URL和Invocation参数；@Adaptive.value中包含protocol参数：同上4

#### Dubbo Inject
> 



####Dubbo发布过程
1.框架启动时，先实例化服务对象
2.铜鼓Proxy组件调用具体协议Protocol，将服务实例包装成Invoker对象
3.发布Invoker对象，将Invoker抓成Exporter
4.通过Registry将Exporter上的元数据注册到注册中心

####Dubbo调用过程
1.调用API的Proxy对象，其Proxy内部也是持有一个Invoker对象
2.从Cluster内部中根据LoadBalance选择其中一个远端的Invoker对象
3.触发Invoker调用时会经历本地的InvokerChain，例如限流，计数等
4.Invoker底层使用NettyClient作为数据传输
5.使用私有协议，将数据包序列化（Serilization+Codec）
6.服务端接收到数据包后，Netty的workGroup线程进行反序列化
7.转成Request对象后分配到业务线程池处理，根据Request找到对应的Exporter
8.Exporter内部持有Invoker对象，这里又会进行一次InvokerChain调用。

####Dubbo在注册中心上4个节点类型
providers
consumers
configurations
Routers

####Dubbo订阅的实现
Dubbo采用第一次启动拉取方式，后续采用监听事件重新拉取数据的方式。
1.在客户端第一次建立连接时，会获取对应目录下的全量数据
2.并在订阅节点上注册一个watcher
3.当watcher发现数据变化时，注册中心会根据watcher找到订阅的客户端并通知
4.客户端收到通知后，会把节点下的全量数据全部重新拉取一遍（为什么要这么做？参考NotifyListener#notify的注释）

####Dubbo注册中心缓存机制
Dubbo中AbstractRegister实现了注册中心通用的缓存机制，客户端会将注册中心上的信息在本地cache一份，并保存在Properties对象中，同时也在硬盘上持久化一份


####Dubbo为什么要自己实现一套扩展点
1.性能优化：避免一次性实例化所有实现，Dubbo扩展点会对Class和Instance两个维度进行缓存，且只有在使用时才会实例化
2.SPI会吞异常：当SPI扩展点加载失败时，会吞掉真实异常，导致排查问题困难
3.增加了对IOC和AOP的支持
4.增加了Wrapper，Adaptive和Activate方式扩展

####Dubbo扩展点特性
1.自动包装类：当ExtensionLoader在加载扩展时，如果发现要加载的扩展点的构造参数时其他扩展点时，则就会认为这个扩展点是一个Wrapper类
2.自动注入：ExtensionLoader在加载扩展时，如果发现扩展点中的某个成员变量是扩展点类型，并也存在对应的set方法时，会将其自动注入，类似Spring的IOC功能
3.自适应：通过DubboURL的参数来动态实现加载扩展点（一个扩展点的多个实现中，只能存在一个实现类标记@Adaptive）
4.自动激活：根据参数，激活加载多个扩展点，形成链式调用，使用@Activate
