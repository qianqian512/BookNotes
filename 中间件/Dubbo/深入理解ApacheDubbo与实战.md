### 第四章 Dubbo扩展点加载机制

##### 4.1 加载机制概述
> 基于Dubbo的SPI机制，为整个框架接口与实现的解耦，奠定了良好的扩展基础。在Dubbo中，几乎所有的组件都支持SPI扩展机制实现的。除了兼容Java默认的SPI机制，Dubbo还自己形成了一套全系的扩展加载点体系，即ExtensionLoader，后面所有的扩展点加载都是基于Extensionloader实现的。

##### 4.1.1 Java SPI
> Java中的SPI全称是Service Provider Interface，初衷是给第三方厂商扩展做插件开发的，使用策略模式实现，在程序中定义接口，通过约定俗成的配置文件路径，来指定真正实现类（默认配置路径是 META-INF/services/#interface.path#），在使用时，可以通过java.util.ServiceLoader来加载真正的实现类。

##### 4.1.2 扩展点加载机制的改进
> Dubbo的ExtensionLoader相比Jdk自带的SPI机制，从以下几个方面进行了改进：  
> 1.避免了一次初始化所有的SPI实例，即便没有使用也会加载，导致浪费资源，ExtensionLoader可以做到随用随加载。  
> 2.JavaSPI加载失败时会吞掉异常，导致排查问题成本较高，ExtensionLoader会抛出异常。  
> 3.ExtensionLoader增加了依赖注入的功能，当加载类包含构造参数的构造方法、或包含setXXX方法时，ExtensionLoader会从容器内部找到匹配类型的实例自动注入进去，构造一个完整的实例对象。  
> 4.支持Wrapper，对实现增强；以及通过Adapter和Activate注解动态返回实例对象。

#### 4.1.3 扩展点的配置规范
> 在META-INF/dubbo/、META-INF/services/、META-INF/dubbo/internal目录下放置对应的SPI文件，文件名称为接口的全路径名称。文件内容以key=[接口实现的全路径名]进行配置。  
>> <font color="blue">dubbo 2.6.5版本中的com.alibaba.dubbo.rpc.Protocol中打破了k-v的格式，此时没有key的实现如何通过ExtensionLoader加载呢？</font>

#### 4.1.4 扩展点的分类与缓存
> ExtensionLoader中内置几个缓存对象，可以分为Class缓存和实例缓存，进而又可以再区分出普通缓存、Adaptive缓存、Wrapper缓存等。

#### 4.1.5 扩展点特性
> 1.自动包装：ExtensionLoader在加载扩展时，如果发现要加载的类，需要其他扩展点作为构造参数时，会认为这是一个Wrapper类，会自动将其注入到构造方法中。   
>> com.alibaba.dubbo.rpc.Protocol如果实例化Wrapper类时，内部已经存在2个同类型的实例时，那么将哪个实例传入Wrapper的构造方法呢？</font>   
> 2.自动加载：当实例化一个对象时，如果存在set方法的参数类型也在ExtensionLoader容器内，则也会将其自动注入。   
> 3.自适应：自适应可以通过dubbo的url中的参数动态确定要使用的实例   
> 4.自动激活：@Adaptive只能激活一个具体实现类，如果需要根据条件激活多个则借助于@Activate激活多个实现类。

#### 4.2.1 扩展点注解：@SPI
> @SPI注解一般标记在interface上，意为这是一个SPI接口，SPI.value对应的名称是默认扩展点实现，通过ExtensionLoader.getDefaultExtension可以获得

#### 4.2.2 扩展点自适应注解：@Adaptive
> @Adaptive注解可以标记在class或method上，修饰在class上作为默认的Adaptive实现，通过ExtensionLoader.getAdaptiveExtension获得，在整个Dubbo框架中，只有2个实现被标注在class上，分别是AdaptiveExtensionFactory和AdaptiveComplier。  
>> AdaptiveExtensionFactory作为ExtensionFactory的统一入口，内置将SpiExtensionFactory和SpringExtensionFactory整合，达到打通两个容器的目的（具体可参见ExtensionFactory.injectExtension方法）。
>> AdaptiveCompiler整合了JavassistCompiler和JdkCompiler，并支持setDefaultCompiler扩展，可以设置默认编译实现。  
>> (仔细思考，其实上面2个自适应class级别的实现，完全可以用SPI的默认功能代替，不知道作者为什么要单独为这2个实现设计出class级别的自适应，且用的过程中也并没有发现自适应的特性，导致对@Adaptive理解起来多一个class级别的维度)   
>