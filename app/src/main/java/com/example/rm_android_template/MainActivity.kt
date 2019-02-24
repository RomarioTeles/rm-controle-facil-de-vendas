package com.example.rm_android_template

import android.os.Bundle
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity_body.text = "You Are Here: MainActivity"
    }
}
