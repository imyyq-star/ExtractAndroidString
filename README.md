# ExtractAndroidString
抽取Android中java文件和layout文件中的中文字符串到strings.xml中


##需求
在Android开发的时候，无论在java文件中编辑还是在xml文件中编辑，遇到有字符串的地方，往往都需要停下来，将其抽取到strings.xml。

比如在编写登录功能的时候，当账号不符合规则的时候，需要提示用户：

    Toast.makeText(context, "账号错误", Toast.LENGTH_LONG).show();
    
如果是用AS开发，把光标放到双引号括起来的字符串上，左侧会出现一个灯泡，可以抽取字符串到strings.xml中。

![示例](http://cl.ly/2B213l3N0w1y/download/5(5WNS7TQUYS601582XBA9D.png))

当然，抽取的时候需要输入对应的英文，个别英文不好的（比如我），此时就需要打开翻译软件翻译后再填入输入框中。

个人觉得这个过程是费时费力的，于是开发了一款小工具，在网上搜了一下，还没人做（也许是我搜不到），就抽空搞了个出来。

##思路
扫描Activity或Fragment文件和layout文件，通过正则取出希望抽取的字符串，通过第三方翻译服务，翻译后转成自己想要的格式，通过界面的方式显示出来，当然，有些字符串可能用户不想抽取到strings.xml中，此时就可以手动取消勾选或修改，然后一键完成。

有了这样一种工具，在Android开发的时候，就无需考虑字符串的问题了，不用在编码的过程中停下来去抽取字符串到strings.xml中，可以说节省了不少时间。

##预览
![示例](http://cl.ly/2B213l3N0w1y/download/5(5WNS7TQUYS601582XBA9D.png))




##建议
1. **在使用此工具之前，请使用Git保证工作区是Clean的，然后再使用，这样子一旦发现哪里不对，可以通过"git checkout -- ."命令清除修改。**
2. **此工具不保证完全通用，编码也不保证可读性好，第一个版本1天构思1天编码就搞出来了，有什么不好的地方请Issues。**
3. **工具中默认使用的第三方翻译是百度翻译，其APIKey是我自己的，有长期使用的请自行到 **
[BAIDU翻译API](http://developer.baidu.com/wiki/index.php?title=%E5%B8%AE%E5%8A%A9%E6%96%87%E6%A1%A3%E9%A6%96%E9%A1%B5/%E7%99%BE%E5%BA%A6%E7%BF%BB%E8%AF%91/%E7%BF%BB%E8%AF%91API)
 申请。

##联系我
邮箱：imyyq.star@gmail.com