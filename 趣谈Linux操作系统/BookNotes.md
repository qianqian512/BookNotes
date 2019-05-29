## 第一部分：Linux操作系统综述
#### ls -l
> 权限位：第一组代表所有者权限；第二组代表所属用户组权限；第三组代表其他组权限。  
> 硬链接数目：<font color="blue">待补充</font>

#### chown & chgrp
> 前者改变文件所属用户；后者改变所属用户组。

#### 软件管理
>（1）linux软件分为两大体系：CentOS和Ubuntu，前者用rpm，后者用deb，例如：rpm -i jdk-xxxx-linux-x64_bin.rpm和dpkg -i jdk-xxxx-linux-x64_bin.deb（-i表示install的意思）  
>（2）查看已安装软件列表：rpm -qa和dpkg -l.  
>（3）删除已安装程序：rpm -e和dpkg -r.  
>（4）软件管理命令：yum和apt-get，卸载可以用yum erase java-11-openjdk.x86_64和apt-get purge openjdk-9-jdk.  
>（5）应用服务器地址：sources.list  

#### nohup
> nohup command > outfile 2>&1 & （2>&1 是将标准出错重定向到标准输出，这里的标准输出已经重定向到了out.file文件，即将标准出错也输出到out.file文件中）  

#### systemctl
> 设置开机启动 systemctl enable xxx

#### kill
> ps -ef | grep "xxxx" | awk '{print $2}' | xargs kill -9

#### fork
> linux通过fork来创建新的进程，当parent进程fork子进程时，会将parnet进程的所有数据结构也都copy给子进程。

#### 内存
> 1.对于内存而言，放进程运行中产生数据的这部分，叫代码段（Data Segment）。其中局部变量也是朝生夕死（运行时变量），而长时间保存的对象，或说是需要指明才销毁的，这部分称为堆（Heap）。  
>2.brk分配内存：当需要分配内存的数据量需要较小时，使用brk分配，回和原来堆的数据连在一起；  
>3.mmap分配内存：当需要分配内存的数据量较大时，使用mmap分配，会划分出一整块新的区域使用。  

#### 文件系统
> 一切皆为文件

#### 异常处理机制
> linux提供异常信号机制来处理异常情况，例如Ctrl+C代表中断信号，用户进程通过kill函数来终止进程。

#### 进程间通信
> 消息队列机制：msgget、msgsnd、msgrcv等命令  
> 共享内存机制：shmget创建一块共享内存、然后通过shmat将共享内存映射到自己的内存上，后续就可进行读写了。（共享读写就会存在一个征用的问题，这时就是用信号量Semphore来处理，sem_wait来竞争或等待，sem_post来释放信号量，以便其他人再次访问）

#### 机器之间的通信
> 遵循TCP/IP协议，基于Socket实现网络通信。

#### Glibc
> Glibc 为程序员提供丰富的 API，除了例如字符串处理、数学运算等用户态服务之外，最重要的是封装了操作系统提供的系统服务，即系统调用的封装。

## 第二部分：系统初始化
#### 