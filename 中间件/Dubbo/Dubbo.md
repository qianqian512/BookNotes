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



#### Dubbo发布过程
1.框架启动时，先实例化服务对象. 
2.铜鼓Proxy组件调用具体协议Protocol，将服务实例包装成Invoker对象. 
3.发布Invoker对象，将Invoker抓成Exporter. 
4.通过Registry将Exporter上的元数据注册到注册中心. 

#### Dubbo调用过程
1.调用API的Proxy对象，其Proxy内部也是持有一个Invoker对象. 
2.从Cluster内部中根据LoadBalance选择其中一个远端的Invoker对象. 
3.触发Invoker调用时会经历本地的InvokerChain，例如限流，计数等. 
4.Invoker底层使用NettyClient作为数据传输. 
5.使用私有协议，将数据包序列化（Serilization+Codec）. 
6.服务端接收到数据包后，Netty的workGroup线程进行反序列化. 
7.转成Request对象后分配到业务线程池处理，根据Request找到对应的Exporter. 
8.Exporter内部持有Invoker对象，这里又会进行一次InvokerChain调用。  

#### Dubbo在注册中心上4个节点类型
providers. 
consumers. 
configurations. 
Routers

#### Dubbo订阅的实现
Dubbo采用第一次启动拉取方式，后续采用监听事件重新拉取数据的方式。  
1.在客户端第一次建立连接时，会获取对应目录下的全量数据. 
2.并在订阅节点上注册一个watcher. 
3.当watcher发现数据变化时，注册中心会根据watcher找到订阅的客户端并通知. 
4.客户端收到通知后，会把节点下的全量数据全部重新拉取一遍（为什么要这么做？参考NotifyListener#notify的注释）. 

#### Dubbo注册中心缓存机制
Dubbo中AbstractRegister实现了注册中心通用的缓存机制，客户端会将注册中心上的信息在本地cache一份，并保存在Properties对象中，同时也在硬盘上持久化一份


#### Dubbo为什么要自己实现一套扩展点
1.性能优化：避免一次性实例化所有实现，Dubbo扩展点会对Class和Instance两个维度进行缓存，且只有在使用时才会实例化. 
2.SPI会吞异常：当SPI扩展点加载失败时，会吞掉真实异常，导致排查问题困难. 
3.增加了对IOC和AOP的支持. 
4.增加了Wrapper，Adaptive和Activate方式扩展

#### Dubbo扩展点特性
1.自动包装类：当ExtensionLoader在加载扩展时，如果发现要加载的扩展点的构造参数时其他扩展点时，则就会认为这个扩展点是一个Wrapper类. 
2.自动注入：ExtensionLoader在加载扩展时，如果发现扩展点中的某个成员变量是扩展点类型，并也存在对应的set方法时，会将其自动注入，类似Spring的IOC功能. 
3.自适应：通过DubboURL的参数来动态实现加载扩展点（一个扩展点的多个实现中，只能存在一个实现类标记@Adaptive）. 
4.自动激活：根据参数，激活加载多个扩展点，形成链式调用，使用@Activate

#### Dubbo中getExtension方法加载流程
1.读取SPI对应路径下的配置文件，将所有的扩展点用到的Class缓存（无初始化）. 
2.根据换入的名称初始化对应的扩展类. 
3.实现setter注入（注入容器会优先使用SpiExtensionFactory，再查找SpringExtensionLoader）. 
4.查找符合条件的Wrapper类，循环进行层层包装，最终返回Wrapper实例. 

#### Dubbo中getAdaptive方法加载流程
1.读取SPI对应路径下的配置文件，将所有的扩展点用到的Class缓存（无初始化）. 
2.动态生成Adaptive实现类代码字符串. 
3.通过ExtensionLoader获取Complier实现（默认的Complier是在class上标注@Adaptive的，因此这部分的代码不是通过Adaptive生成）. 
4.返回Adaptive实现

#### Dubbo是如何与Spring容器打通的
AdaptiveExtensionFactory作为ExtensionLoader的默认实现，内部实际是管理者Dubbo容器(SpiExtensionLoader)和Spring容器(SpringExtensionLoader)。  
在调用getExtension方法时，会通过TreeSet排序存储，SPI的排在前面，Spring的在后. 


#### Dubbo发布详细过程
1.解析加载配置，xsd文件定义了xml配置的语法结构，借助spring的handler文件+NameSpaceHandler将配置解析成Java对象，形成RootBeanDefinition。  
2.BeanDefinition解析成ServiceConfig（怎么转的，中间用到了哪些类？）. 
3.ServiceConfig根据配置中定义的export协议，依次暴露服务（参见doExport方法）. 
4.将ServiceConfig内部的配置，ProtocolConfig对象和Registry构建成dubboUrl的参数，至此会将一个即将对外暴露的Service的所有配置都会存到url上。  
5.通过ProxyFactory将ServiceConfig对象和上部组装的url，生成Invoker对象。  
6.再将Invoker对象根据协议发布成Exporter；如果存在注册中心，则获取注册中心url，并将registryUrl作为参数，之前生成的url作为registryUrl的子参数传给Exporter发布服务。   
7.【注册中心Export部分】创建NettyServer监听端口. 
8.【注册中心Export部分】创建注册中心对象，与注册中心建立TCP连接，同时监听configuration节点   
9.【注册中心Export部分】将服务注册到注册中心，告知订阅者可用. 

#### Dubbo服务调用拦截时用到的Filter，Listener都是在哪里初始化的？
参见P102页

#### Dubbo的Injvm协议是干嘛用的？

#### Dubbo的Protocol层设计理念
> 1.个人理解是收拢了RPC调用，将上层一切对象封装成Invoker，然后屏蔽了下层remoting的实现细节，这种解耦好处在于为Dubbo支持异构系统打下了基础。  
> 2.Protocol层其实定义了弹性与限制，例如DubboProtocol，下层Transporter层可支持Mina、Netty，Serilization层支持多种序列化格式，但同时也定义了限制，例如下层必须使用异步线程模型(Exchange层就是为此而存在，如果基于Protocol扩展出Socket实现，完全可以抛弃Exchange层)；又例如新增的HessianProtocol、GrpcProtocl和ThriftProtocol，Dubbo的扩展点在这些新的Protocol中几乎失效，弹性和限制完全依赖于第三方实现。

#### Dubbo消费者中是如何将多个注册中心转成一个Invoker的？

#### Exchange层存在的目的是什么？

#### Dubbo调用时传输报文的头结构
> Dubbo协议报文头一共16字节，前2字节为magicNumber，第三字节高4位是请求类型，第四位是序列化类型，第4字节是响应码，第5-12字节是RequestId，最后4字节是body长度。

#### 对比Exchange层和Transport层的差异

#### ProtocolListenerWrapper和ProtocolFilterWrapper区别

#### Dubbo中Transport和Exchange的Handler分层
> 读代码时发现这2层均有Handler实现，Transporter层以Netty为代表，关注的是解码协议和序列化，而Exchange层的实现更多是关注同步异步的转换。

#### Dubbo调用过程
发送
> Protocol层(从ReferenceConfig到DubboInvoker)：由Proxy对象发起调用，Proxy是一个实现了JDK InvocationHandler的代理类，内部会持有Invoker对象，当发起任何调用时，会将调用的目标方法以及参数包装成一个RpcInvocation对象传入给Invoker，然后由Invoker是对远端RPC调用。    
> Exchange层：DubboInvoke.doInvoke时，其内部创建一个ExchangeClient，这层是为了将同步请求转成异步请求；然后调用NettyAPI发起调用  
> Transport层：Exchange层send invocation对象时，会经历Netty定义的Handler，Netty定义的Handler主要有2个，一个是codec，一个就是Exchange层传入的Handler；codec主要是用于编解码，将上层的Invocation和Result对象转成Request和Response；而Exchange的Client+Future处理同步异步的转换  
> Serilization层：Dubbo在引用服务时，就已经在OutputHandler层面决定了序列化的类型，Exchange.send -> Transport.writeAndFlush -> Transport.EncodeHandler
接收
> Serilization层：对Response进行Decode，然后交给上层ExchangeHandler处理
> Transport层：接收是，Transport层没有特殊逻辑
> Exchange层：根据Response的Id找到Dubbo的Future对象，将Response对象放入Future后即通知线程响应数据已准备好，此时恢复线程执行即可



















