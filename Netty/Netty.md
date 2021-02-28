#### EventLoop
> What: EventLoop是一个Reactor模型的事件处理器，管理着一个I/O连接生命周期内的所有事件调度。
> Where: 
> Why:
> How:EventLoop在执行延迟任务时，会先将延迟任务放到一个PrioityQueue中，然后执行前先在PrioityQueue中找到符合条件的Task，再统一转移到TaskQueue中和普通任务一起执行。
> 内部存储结构：主要是由一个Selector和一个TaskQueue组成的

#### Channel
#### ChannelHandler
#### ByteBuf