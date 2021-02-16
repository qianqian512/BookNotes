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
> "自适应"的含义：通过DubboUrl的参数动态获得实现，如果没有匹配，则使用SPI默认实现。

#### 4.2.3 扩展点自动激活注解：@Activate
> @Activate可以放在interface、class、method和enum上，主要用于需要多个扩展点时，可以根据不同的条件激活(value或group)

#### 4.3.1 ExtensionLoader的工作原理
> ExtensionLoader获取扩展点的逻辑入口有3个：getExtension、getAdaptiveExtension和getActivateExtension，其中getExtension和getActivateExtension类似，共用代码居多，而getAdaptiveExtension则相对独立。  
> getExtension工作流程：
>> 1.加载SPI目录下的配置文件，并根据配置加载并缓存所有class，但并不会实例化操作  
>> 2.根据传入的name实例化对应的class  
>> 3.实例化符合条件的Wrapper类，其中分为2种情况，一个是包含扩展点的setter方法；一个是包含扩展点的构造方法；  
>> 4.返回实例对象。

> getActivateExtension工作流程：  
>> 1.和getExtension一样，先加载SPI目录下的配置文件，并根据配置加载并缓存所有class，但并不会实例化操作。  
>> 2.生成自适应class的代码字符串  
>> 3.获取到编译器(Compiler)实现，将代码字符串编译创建出自适应的实例对象
>> 4.返回实例对象。

#### 4.3.2 getExtension的实现原理
> * 调用getExtension传入name如果是字符串"true"时，则会返回同getDefaultExtension相同的实现。  
> 1.先尝试从cache中获得实现对应的class  
> 2.如果cache中不存在，则查找DubboSpi的3个目录中，是否有可以匹配的实现类全称，例如com.alibaba.dubbo.common.extensionloader.activate.impl.GroupActivateExtImpl。  
> 3.通过反射class.forName获得到扩展点的Class对象  
> 4.针对步骤3反射出class对象进行初始化，需要区分3种情况：  
>> 4.1 class上是否有@Adaptive注解，如果存在则缓存到AdaptiveClassCache中  
>> 4.2 class是否是一个Wrapper类(判断方式为是否包含参数是自身接口类型的构造方法)，如果是Wrapper类型则缓存到WrapperClassCache中    
>> 4.3 判断class上是否包含@Activate注解，如果包含的话则缓存到ActivateClassCache中，并缓存到extensionClassCache中一份（因为Activate也是普通的扩展点中的一种）  
> (经历上面4个步骤，扩展点的class对象已经加载完成，接下来就是通过class构建出扩展点的实例对象)  
> 5.根据扩展点class找出扩展点实例缓存(ExtensionInstanceCache)中是否存在，如果不存在则通过class.newInstance创建出一个  
> 6.依赖注入扩展点实例的setter方法  
> 7.通过WrapperClassCache判断扩展点是否存在Wrapper的可能，如果存在，则开始循环构建Wrapper对象，并最终返回。

#### 4.3.3 getAdaptiveExtension实现原理
> * getAdaptiveExtension和getExtension一个重要的区别就是需要生成自适应class  
> 生成自适应class的代码逻辑如下：1.生成package、import、className等头部信息，className格式为className$Adaptive  
> 2.遍历所有方法，生成方法内容主体，其中包括：参数校验不能为空，根据url获得参数作为extensionName查找扩展点实现  
> 3.将动态生成的代码使用编译器进行编译，生成一个新的class对象。
> 4.将上面的class使用newInstance创建出实例对象  
> 5.复用getExtension的injectExtension方法，对对象进行注入，但不会进行wrapper。  
> (Adaptive生成的代码可以参考UserService$Adaptive.java)

#### 4.3.4 getActivate实现原理
> 

#### 4.3.5 ExtensionFactory实现原理
> ExtensionFactory扩展点有3个实现，分别是SPI、Spring和Adaptive版本，其中Adaptive的class上使用@Adaptive修饰，意为ExtensionFactory默认的自适应实现。而AdaptiveExtensionFactory内部动态维护了其他ExtensionFactory扩展点的实现，在getExtension时会根据name遍历查找每个容器，从而将Dubbo和Spring打通。  
> Spring容器是什么时候被初始化的呢？其实是依赖Dubbo中的ServiceBean和ReferenceBean的事件初始化的，即一个服务被export或被reference的时候，会被Spring上下文保存到容器中。  
> 在ExtensionFactory加载扩展点时，会先从SPI容器中查找，然后再从Spring中查找。

#### 4.4 扩展点动态编译实现
> Dubbo通过动态生成代码+动态编译器(javassist)实现了自适应扩展点功能。

#### 4.4.1 总体结构
> Dubbo的Compiler在类上用@SPI修饰，其中SPI默认值是"javassist"作为默认编译器。  
> Compiler的3个实现类分别是JavassistCompiler、JdkCompiler和AdaptiveCompiler，与ExtensionFactory类似的是，AdaptiveCompiler也同样是作为总管理入口，内部维护着其他Compiler实现。  
> 如果需要改变默认编译器，可以通过<dubbo:application compiler="jdk" />标签改变，其内部会调用AdaptiveCompiler的setDefaultCompiler方法改变默认编译器。

#### 4.4.2 Javassist动态代码编译
> 1.通过ClassPool获得Javassist类池  
> 2.传入字符串类型的参数，作为要生成的className，调用ClassPool的makeClass生成CtClass    
> 3.使用CtNewMethod.make生成方法代码，并通过CtClass的addMethod与之关联起来  
> 4.最终可以通过ctClass.toClass生成最终的Class对象，通过newInstance就可以获得对象实例了。

> javassist的原理就是不断的通过正则表达式匹配不同位置的代码，会调用不同的Api生成不同结构的代码(以class为例，按照JVM规范拼写16进制代码，最终生成byte数组)，最终构造出一个完整的Class对象。

#### 4.4.3 JDK动态代理编译
> JDK动态编译代码主要依托于下面3个class完成：  
> 1.JavaFileObject：字符串代码会被包装成一个文件对象，主要用于获取二进制流接口。  
> 2.JavaFileManager：主要管理文件读取和输出位置，由于JDK中没有可以直接使用的实现类，作为唯一实现的ForwardingJavaFileManager又是protected类型，因此Dubbo又自己定义了一个实现类。  
> 3.JavaCompiler，可以将JavaFileObject编译成具体的Class对象。


### 第五章：Dubbo启停原理解析 
#### 5.1 配置解析
> Dubbo框架提供了3种配置方式：XML配置、注解和属性文件(ymal或properties)

#### 5.1.1 基于schema设计解析
> 1.Dubbo的xml配置约束文件在dubbo-config/dubbo-config-spring/src/main/resources/dubbo.xsd文件中定义。  
> 2.Dubbo配置与Spring的XML文件集成原理：Spring在解析到自定义的Namespace标签时，会查找对应的spring.schemas和spring.handlers文件，最终触发Dubbo的DubboNamespaceHandler来进行解析和初始化。其中xsd文件约定了xml配置规范，handlers文件定义了xml标签的解析类。  
> 3.Dubbo的Schema也是经过分层设计，参考2.7.3版本：  
> 3.1 最上层是一个抽象度最高的配置类，只记载了id和prefix，即xml标签的前缀，例如"dubbo:service"  
> 3.2 第二层分支较多：例如ApplicationConfig、ProtocolConfig、RegistryConfig、AbstractMethodConfig等    
> 3.3 第三层分支只有AbstractMethodConfig的子类，又细分出了Provider和Consumer、Service和Reference  
> * 思考：这到底是根据什么分的层，没搞明白
> 4.关于对Dubbo二次开发时需要注意：不仅要对新增的字段在xsd中定义，且还要在对用的dubbo-config-api中Config类增加相应的字段，并提供get/set方法，这样用户在配置xml后才会自动注入这个值。

#### 5.1.2 基于XML配置原理解析
> dubbo.xsd定义了xml的格式与约束，而主要代码逻辑解析在DubboNamespaceHandler中完成，而DubboNamespaceHandler主要职责是将不同的标签关联到对应的解析类中  
> XML配置解析过程：  
>> (略) 感觉很繁琐但并不复杂，大概思路就是将xml标签转成RootBeanDefinition

#### 5.1.3 基于注解配置原理解析
> dubbo在早期基于AnnotationBean实现注解配置，其底层主要是依赖于Spring的BeanPostProcessor实现，存在以下几个问题：  
>> 1.注解还是依赖XML，开启注解需要配置XML：<dubbo:annotation />  
>> 2.@ServiceBean不支持AOP  
>> 3.@ReferenceBean不支持字段继承  

> 在重构后，底层使用SpringBoot配置方式。  

#### 5.2 服务暴露的实现原理
#### 5.2.1 配置承载初始化
> 1.JVM的-D参数优先级最高    
> 2.XML配置优先级其次  
> 3.properties置文件优先级最低  
> 4.配置中的consumer配置优先级高于provider，例如timeout，retries等。 

#### 5.2.2 远程服务的暴露机制
> 从框架角度看：  
>> 将ServiceConfig+服务实例，通过ProxyFactory转成Invoker，Invoker通过Protocol转成Exporter。    TODO

> 从源码角度看：
>> 1.


### 第8章：Dubbo扩展点
#### 8.1.2 扩展点整体架构
> 在整个Dubbo框架中，可扩展的point逻辑分层如下：  
>> Proxy层：ProxyFactory  
>> Registry层：RegistryFactory
>> Cluster层：Cluster、RouterFactory、LoadBalance、ConfigurationFactory、Merger  
>> Protocol层：Protocol、Filter、ExporterListener、InvokerListener  
>> Exchange层：Exchanger  
>> Transport层：Transport、Dispatcher、Codec2、ChannelHandler、ThreadPool  
>> Serilize层：Serialization

#### 8.2.1 Proxy层扩展点

#### 8.2.2 Registry层扩展点

#### 8.2.3 Cluster层扩展点
##### Cluster
##### RouterFactory
##### LoadBalance
##### ConfigurationFactory
##### Merger

#### 8.3.1 Protocol扩展点
#### Filter扩展点
#### ExporterListener、InvokerListener 扩展点

#### 8.3.2 Exchange层扩展点

#### 8.3.3 Transport层扩展点
##### Dispatcher扩展接口
##### Codec2扩展接口
##### ThreadPool扩展接口

#### 8.3.4 Serilize层扩展点

#### 8.4 其他扩展点
> TelnetHandler  
> StatusChecker  
> Container  
> CacheFactory  
> Validation  
> LoggerAdapter  
> Compiler  


















