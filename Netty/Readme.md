##Netty基础
#### 1.IO概念中的同步异步、阻塞非阻塞的区别
> 阻塞&非阻塞   
>> 数据就绪（内核态->用户态）之前，是否需要等待；阻塞就是指无数据可读时，当前线程会一直阻塞等待；非阻塞不会阻碍当前线程，即便在没有数据的情况下，调用读写后立即返回。      

> 同步&异步   
>> 数据就绪后，谁来操作？数据就绪后，用户态自己去读就是同步；异步是数据就绪后，内核态会将数据为用户态准备好，让用户态直接使用即可。

#### 2.BIO、NIO、AIO的区别以及适用场景
> BIO(同步阻塞)计算型密集项目：例如连接数少，且每一次传输需要经过CPU大量计算才得出的结果    
> NIO(同步非阻塞)IO密集型项目：例如连接数多，计算少的推送服务器，需要和大量的中断保持连接，但每个连接传输数据少。   
> AIO(异步非阻塞)<font color="blue">待补充</font>  

#### 3.Netty为什么废弃了BIO和AIO，仅支持NIO了？
> 在Linux上AIO和NIO性能差别不大，但底层实现结构相差较远(NIO采用Reactor，AIO采用Proactor)，维护起成本较高。

#### 4.如果Linux下AIO和NIO一样成熟，那是否代表AIO可以完全取代NIO？
> NIO和AIO有一个差异点是：AIO在建立连接后是需要预先分配内存的；NIO是用到在分配；所以在连接数多，流量小的场景下，AIO会浪费过多内存。
 
#### 5.NIO有多少种实现，Netty为什么要单独写一个NIO实现
> Common实现、Linux实现、和MaxOS/BSD实现。Common的NIO实现在Linux下也是用epoll，但Netty重写后的NIO模型相比JDK实现更加完善，实现的更好。例如Netty默认是的NIO实现是边缘触发、JDK的NIO实现默认是水平触发。

#### 6.BIO模型有半包、粘包的问题吗 
> TCP粘包问题产生的原因不在于使用BIO或NIO的线程模型上，而是在read时，从流到缓冲区会产生半包；假如边读边解析，BIO也存在半包粘包等问题；如果接收完统一解析(这里假定不感知协议格式，收到对端RST包，就对之前收到的所有包进行一次性解析)，NIO也不存在半包粘包的问题了，但实际上NIO的半包和粘包问题更加明显，原因是因为在每次操作缓冲区时，这个缓冲区可能只是中间一部分，需要结合上一次操作的缓冲区和下一次的缓冲区才有可能将完整的包解析完成，因此ReplayingDecoder可以很好的解决这个问题。

##Netty使用
#### 1.ServerBootstrap的childOption和option有什么区别?childHandler和handler有什么区别？
> * option作用于Boss线程配置，例如可以设置backlog长度；childOption作用于worker线程，例如指定keepAlive，是否支持半关闭状态，可以发现都是接收客户端Connected之后的事了。
> * handler()和childHandler()的主要区别是，handler()是发生在初始化的时候，childHandler()是发生在客户端连接之后。

#### 2.关于ChannelInboundHandlerAdapter、SimpleChannelInboundHandler和ReplayingDecoder类区别
> 1. 继承SimpleChannelInboundHandler类之后，会在接收到数据后会自动release掉数据占用的Bytebuffer资源。并且继承该类需要指定数据格式。
> 2. 继承ChannelInboundHandlerAdapter则不会自动释放，需要手动调用ReferenceCountUtil.release()等方法进行释放。继承该类不需要指定数据格式。
> 3. ReplayingDecoder：<font color="blue">待补充</font>  
> * 总结：因此一般而言，推荐服务端继承ChannelInboundHandlerAdapter，手动进行释放，防止数据未处理完就自动释放了。而且服务端可能有多个客户端进行连接，并且每一个客户端请求的数据格式都不一致，这时便可以进行相应的处理。 客户端根据情况可以继承SimpleChannelInboundHandler类。好处是直接指定好传输的数据格式，就不需要再进行格式的转换了。

#### 3.关于ByteBuf的池化和非池化的区别以及使用场景

#### 4.Bytebuf什么时候使用release

#### 5.零拷贝（Zero-Copy）
> 1. Netty Zero-Copy 是指ByteBuf在操作原始byte数组时，可以直接在原始byte数组上建立逻辑关系。从而不用另行开辟一段内存空间copy一份byte数组，因此叫做Zero-Copy。下面用代码来做说明：  
> 2. One-Copy做法：除了bytes占用的空间外，还需要再申请一段空间，然后将bytes写入到byteBuf里去，导致内存中存在两份bytes对象，以JDK NIO为例：  
```
byte[] bytes = ...
ByteBuffer buf = ByteBuffer.allocate(bytes.length);   
buf.put(bytes);
```
> 3. Zero-Copy做法：wrappedBuffer直接将bytes对象包装返回，返回的byteBuf其内部也指向了bytes数组，因此bytes和byteBuf内部的bytes实际是指向了同一个对象。 
```
byte[] bytes = ...  
ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);  
```

#### 6.ChannelHandlerContext.write和Channel.write的区别
> 

#### 7.原生JDK的NIO在空轮询后为什么会出现CPU100%的情况，而Netty又是如何解决的？
> https://www.cnblogs.com/qiumingcheng/p/9481528.html

#### Netty使用经验
> 1. IO通信读写缓冲区可以使用DirectByteBuf；后端业务消息编解码使用HeapByteBuf，这样组合可以达到性能最优。
> 2. 为了提升性能，Netty默认的IO Buffer使用直接内存DirectByteBuf（零拷贝）