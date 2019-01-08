package com.joker.richshow

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

/**
 * MainActivity
 *
 * @author  joker
 * @date    2019/1/4
 * @since   1.0
 */
class MainActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_main)
        val richTextView = findViewById<RichTextView>(R.id.richTextView)
        richTextView.setHtml(html)
        richTextView.setOnClickListener {
            Toast.makeText(this, "被点击了", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, RichTestActivity::class.java))
            false
        }

    }
}
