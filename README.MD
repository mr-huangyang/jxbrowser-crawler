### 数据抓取设置
- 略过前几行
- 略过后几行
- 翻页JS  (用于后台执行自动翻页的JS)
- Ajax (表示Table数据是同步加载还是异步加载。默认是同步加载)
- 翻页数 (整个过程需要翻页的次数，总页数减1)
- 翻页间隔 （每次翻页间隔的时间。异步加载的网站这个配置可能需要设置的长一些，需要等待数据加载）
- 文件名称 （保存抓取数据的excel的名称）
 ###操作
 页面加载完成以后：比如抓取http://202.96.245.182/xxcx/yp.jsp?lm=4 。点击table内的任一元素，在界面上方会显示该元素所在的html 结点树，选中该元素的父table元素，点击抓取按钮