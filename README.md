# FilterJs
---
21年6月份开发的一款Burp插件，该插件可以在你浏览网页的同时被动的提取网页中的接口信息，插件不会向服务器发送任何请求，方便安全的进行渗透测试。
## 该插件有以下优势
1. 过滤js文件中的接口信息，并显示接口内容在js文件中的上下文。

2. 从js文件中过滤你需要的敏感信息，且支持在配置文件中自定义检出规则。

3. 完全被动检测，不会有任何主动请求行为。防止不可控请求的产生。

---


# 更新日志
### 22-9-26 
1. 新增双击复制接口，便于粘贴对接口进行测试。
2. 完善接口匹配规则。
### 23-2-8
1. 完善接口检出规则，现在相邻的多个接口，都会被单独检测出来。
2. 完善js文件判断机制，现在能过滤出更多的js文件。
3. 修复在切换domain时空指针错误，程序挂掉的问题。
4. 以host+port区分网站，现在同一个host下的不同网站可以分别查看接口了。
### 23-2-9
1. 自动对接口列表和domain(点击检索)列表进行排序。
2. 美化敏感信息页面。
3. 新增regex.txt文件，用户可以在不修改代码的情况下替换为自己的API接口匹配规则。
### 23-2-9
1. 增加鼠标点击任意接口三次，复制所有接口功能。
### 23-9-26
1. 优化了接口匹配规则。
### 23-12-4
1. 修改配置文件保存目录。
### 23-12-27
1. 添加清空功能，可以清空当前过滤结果。


本项目仅用于学习研究使用，请勿用于非法用途。
详细插件使用方式如下:https://mp.weixin.qq.com/s/t7k5mm5G9vObwEmCwQca8Q 。
其他工具可关注我的微信公众号"地表最强伍迪哥"。
