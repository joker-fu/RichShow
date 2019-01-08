package com.joker.richshow

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class RichTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rich_test)
        val richTextView = findViewById<RichTextView>(R.id.richTextView)
        richTextView.setHtml("")
    }
}
