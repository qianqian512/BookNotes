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