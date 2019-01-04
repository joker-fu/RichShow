

![](https://user-gold-cdn.xitu.io/2019/1/4/168182203dd7cf89?w=363&h=622&f=png&s=329683)

由于项目中需要使用到富文本显示和编辑，这方面手机还真是不如前端，一查富文本编辑几乎都是前端的，目前富文本显示主要有3种方案，先简单介绍下：

###### 1. 使用Html.fromHtml
 - Html.fromHtml解析
 - TextView显示解析结果
 - 标签及样式支持较少，图片显示得单独处理（前端富文本框架创建的内容，这种方式就不是很适用了，标签样式太多）
```koltin
 tvAttachTask.text = Html.fromHtml("负责任务<font color=\"#479CD9\">${it[1]}</font>个，已完成<font color=\"#479CD9\">${it[0]}</font>个")
```

###### 2. 自行解析html标签
 - 针对具体的标签样式进行解析
 - 用Span或者原生控件组合显示
 - 需要自己做解析处理，没解析到的就显示不了

当然，开源大法好[github](https://github.com/zzhoujay/RichText)上已经有人做了

###### 3. WebView加载
- 简单快捷
- 标签基本都支持
- 需要做一些处理
  感觉性能开销大（特别是像我们项目中连评论回复都是富文本）

这里大家择优选用吧，这里介绍下第三种方案....

-----

###################我是漂亮的分割线###################

-----

#### 具体实现

###### 1 核心方法
```kotlin
//据说这种方式有问题，待验证
loadData(html, "text/html", "UTF-8")
//实际使用这种方式没问题
loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
```
###### 2 基本使用
```kotlin
    val html = """
  <p style="text-align: center; "><span style="font-size: 18px; background-color: rgb(255, 255, 255); color: rgb(255, 0, 0); font-weight: bold; font-style: italic;">任务描述</span></p>
  <p style="text-align: left;"><span style="font-size: 18px; background-color: rgb(255, 255, 255); color: rgb(255, 0, 0); font-weight: bold; font-style: italic;">任务描述任务描述</span></p>
  <p style="text-align: right;"><span style="font-size: 18px; background-color: rgb(255, 255, 255); color: rgb(255, 0, 0); font-weight: bold; font-style: italic;">任务描述任务描述任务描述</span></p>
  <p style="text-align: center; "><img src="http://www.bjdalisi.com/uploads/flash/1804101056357681.jpg"></p>
  <p style="text-align: center; "><br></p>
  <p style="text-align: center; ">jjj</p>
  <p style="text-align: center; ">hjj</p>
  <p style="text-align: center; "><br></p>
  <p style="text-align: center; "><br></p>
  <p style="text-align: center; "><br></p>
  <p style="text-align: left;">bjju<i>hjjj</i>
  <embed src="http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4" />
  <img src="http://www.bjdalisi.com/uploads/flash/1804101056357681.jpg"></p>
"""

    val webView = findViewById(R.id.webView)
    //取消滚动条
    webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY)
    //不支持缩放功能
    webView.getSettings().setSupportZoom(false)
    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
```

###### 3 显示问题处理
上面的html在Chrome上显示，好像没有什么问题，但是在手机上显示，居然可以左右滑动，这样肯定交不了差的。
```gradle
implementation 'org.jsoup:jsoup:1.11.3'
```
- 处理链接不能换行问题
```kotlin
    /** 修复 a 标签 **/
    private fun fixA(doc: Document) {
        //这里我们使用 jsoup 修改 a 的属性:
        val `as` = doc.getElementsByTag("a")
        for (i in 0 until `as`.size) {
            val tempA = `as`[i]
            tempA.attr("style", "word-break: break-word")
        }
    }
```
- 处理图片过大拖动问题
```kotlin
    /** 修复 img 标签 **/
    private fun fixImg(doc: Document) {
        //使用 jsoup 修改 img 的属性:
        val images = doc.getElementsByTag("img")
        for (i in 0 until images.size) {
            //宽度最大100%，高度自适应
            images[i].attr("style", "max-width: 100%; height: auto;")
        }
    }
```
- 处理视频问题
```kotlin
    /** 修复 embed 标签 **/
    private fun fixEmbed(doc: Document) {
        //使用 jsoup 修改 embed 的属性:
        val embeds = doc.getElementsByTag("embed")
        for (element in embeds) {
            //宽度最大100%，高度自适应
            element.attr("style", "max-width: 100%; height: auto;")
                    .attr("controls", "controls")
        }
        //webview 无法正确识别 embed 为视频时，需要这个标签改成 video 手机就可以识别了
        doc.select("embed").tagName("video")
    }
```
###### 4 点击问题
经过前一个步骤的处理，富文本显示是没什么问题了，至少我在使用上没什么问题了，但是点击链接直接就跳转到浏览器了，图片点击不能方法预览呢？产品大大找麻烦怎么办？不做了还是做不了？别急，so easy...

上面我们引入了jsoup，再配合我们原生交互起来就O啦！具体操作如下：

- 注入方法
```kotlin
    private inner class PreViewJs {
        //@JavascriptInterface注解方法，js端调用，4.2以后安全
        @JavascriptInterface
        fun onTagClick(url: String, info: String) {
        }
    }
    ...
    webView.settings.javaScriptEnabled = true
    webView.settings.domStorageEnabled = true
    ...
    addJavascriptInterface(PreViewJs(), "PreViewJs")
```
- jsoup修改html

这里仅用img标签处理做示范：
```kotlin
    /** 修复 img 标签 **/
    private fun fixImg(doc: Document) {
        //使用 jsoup 修改 img 的属性:
        val images = doc.getElementsByTag("img")
        for (i in 0 until images.size) {
            //宽度最大100%，高度自适应
            images[i].attr("style", "max-width: 100%; height: auto;")
                    //点击调用原生传参
                    .attr("onclick", """PreViewJs.onTagClick(this.src,'富文本图片.jpg')""")
        }
    }
```

详见demo....

**附：**

1. 本文demo：https://github.com/joker-fu/RichShow

2. 富文本编辑：https://github.com/joker-fu/RichTextEditor