package com.joker.richshow

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * RichTextView
 *
 * @author  joker
 * @date    2019/1/4
 * @since   1.0
 */
class RichTextView : WebView {

    private var startClickTime: Long = 0
    private var onClickListener: ((View) -> Boolean)? = null
    private var cacheText: String? = ""

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, privateBrowsing: Boolean) : super(context, attrs, defStyleAttr, privateBrowsing)

    init {
        scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY//取消滚动条
        settings.setSupportZoom(false)//不支持缩放功能
        @SuppressLint("SetJavaScriptEnabled")
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        addJavascriptInterface(PreViewJs(), "PreViewJs")
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startClickTime = System.currentTimeMillis()
                }
                MotionEvent.ACTION_UP -> {
                    if (onClickListener == null) return@setOnTouchListener false
                    val clickDuration = System.currentTimeMillis() - startClickTime
                    if (clickDuration < 200) {
                        return@setOnTouchListener onClickListener!!.invoke(this@RichTextView)
                    }
                }
            }
            false
        }
    }

    /**
     * RichTextView 点击事件监听
     * @param listener 监听 返回true拦截事件
     **/
    fun setOnClickListener(listener: (View) -> Boolean) {
        onClickListener = listener
    }

    fun setHtml(text: String?) {
        if (text == null) return
        cacheText = text
        val doc = Jsoup.parse(text)
        fixImg(doc)
        fixA(doc)
        fixEmbed(doc)
        loadDataWithBaseURL(null, doc.toString(), "text/html", "UTF-8", null)
    }

    fun getHtml(): String {
        return cacheText ?: ""
    }

    /** 修复 img 标签 **/
    private fun fixImg(doc: Document) {
        //使用 jsoup 修改 img 的属性:
        val images = doc.getElementsByTag("img")
        for (i in 0 until images.size) {
            //宽度最大100%，高度自适应
            images[i].attr("style", "max-width: 100%; height: auto;")
                    //.attr("onclick", """PreViewJs.onTagClick(this.src, this.getAttribute('data-filename'))""")
                    .attr("onclick", """PreViewJs.onTagClick(this.src,'富文本图片.jpg')""")
        }
    }

    /** 修复 a 标签 **/
    private fun fixA(doc: Document) {
        //使用 jsoup 修改 img 的属性:
        val `as` = doc.getElementsByTag("a")
        for (i in 0 until `as`.size) {
            val tempA = `as`[i]
            tempA.attr("onclick", """PreViewJs.onTagClick('${tempA.attr("href")}','LINK')""")
                    .attr("href", "javascript:void(0)")
                    .attr("style", "word-break: break-word")
        }
    }

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

    private inner class PreViewJs {

        //@JavascriptInterface注解方法，js端调用，4.2以后安全
        //4.2以前，当JS拿到Android这个对象后，就可以调用这个Android对象中所有的方法，包括系统类（java.lang.Runtime 类），从而进行任意代码执行。
        @JavascriptInterface
        fun onTagClick(url: String, info: String) {
            //TODO 这里可以做你需要的操作
            println(url)
        }
    }

}