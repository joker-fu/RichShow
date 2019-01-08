package com.joker.richshow

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class RichTestActivity : AppCompatActivity() {

    private val html = """
  <p style="text-align: center; "><span style="font-size: 18px; background-color: rgb(255, 255, 255); color: rgb(255, 0, 0); font-weight: bold; font-style: italic;">任务描述</span></p>
  <p style="text-align: left;"><span style="font-size: 18px; background-color: rgb(255, 255, 255); color: rgb(255, 0, 0); font-weight: bold; font-style: italic;">任务描述任务描述</span></p>
  <p style="text-align: right;"><span style="font-size: 18px; background-color: rgb(255, 255, 255); color: rgb(255, 0, 0); font-weight: bold; font-style: italic;">任务描述任务描述任务描述</span></p>
  <p style="text-align: center; "><img src="http://www.bjdalisi.com/uploads/flash/1804101056357681.jpg"></p>
  <p style="text-align: center; ">hjj</p>
  <p style="text-align: left;">bjju<i>hjjj</i>
  <embed src="http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4" />
  <p style="text-align: left;">bjju<i>hjjj</i>
  <img src="http://www.bjdalisi.com/uploads/flash/1804101056357681.jpg"></p>
"""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rich_test)
        val richTextView = findViewById<RichTextView>(R.id.richTextView)
        richTextView.setHtml(html)
    }
}
