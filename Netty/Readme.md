#### 1.ServerBootstrap的childOption和option有什么区别?childHandler和handler有什么区别？


#### 2.关于ChannelInboundHandlerAdapter、SimpleChannelInboundHandler和ReplayingDecoder类区别
> 1. 继承SimpleChannelInboundHandler类之后，会在接收到数据后会自动release掉数据占用的Bytebuffer资源。并且继承该类需要指定数据格式。
> 2. 继承ChannelInboundHandlerAdapter则不会自动释放，需要手动调用ReferenceCountUtil.release()等方法进行释放。继承该类不需要指定数据格式。
> 3. ReplayingDecoder  
> * 总结：因此一般而言，推荐服务端继承ChannelInboundHandlerAdapter，手动进行释放，防止数据未处理完就自动释放了。而且服务端可能有多个客户端进行连接，并且每一个客户端请求的数据格式都不一致，这时便可以进行相应的处理。 客户端根据情况可以继承SimpleChannelInboundHandler类。好处是直接指定好传输的数据格式，就不需要再进行格式的转换了。

#### 3.关于ByteBuf的池化和非池化的区别以及使用场景

#### 4.Bytebuf什么时候使用release