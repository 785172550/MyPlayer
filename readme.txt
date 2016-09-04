myplayer: 作者汪航，自己写的一个本地音乐播放器

开发环境：

Android studio 2.1
gradle 2.1.0
buildToolsVersion "23.0.2"
compileSdkVersion 23


Android：4.4版本小米2S 测试无问题


1.已经实现用Service来播放音乐，broadcast 来实现activity与Service之间的信息交流控制音乐播放

2.能自动扫描本地的音乐，并用数据库缓存的音乐的信息

3.界面“所有歌曲”完成，点击就能播放，侧滑中的其余三个有待实现

4.上滑出的播放控制界面只能播放|暂停，其余按钮功能没有实现

5.NotificationManager 控制播放还没有实现