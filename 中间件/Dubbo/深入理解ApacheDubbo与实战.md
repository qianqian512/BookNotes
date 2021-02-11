### 第四章 Dubbo扩展点加载机制

##### 加载机制概述
> 基于Dubbo的SPI机制，为整个框架接口与实现的解耦，奠定了良好的扩展基础。在Dubbo中，几乎所有的组件都支持SPI扩展机制实现的。除了兼容Java默认的SPI机制，Dubbo还自己形成了一套全系的扩展加载点体系，即ExtensionLoader，后面所有的扩展点加载都是基于Extensionloader实现的。

#### 