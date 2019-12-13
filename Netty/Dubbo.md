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