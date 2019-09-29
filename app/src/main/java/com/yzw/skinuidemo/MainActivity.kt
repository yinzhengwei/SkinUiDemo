package com.yzw.skinuidemo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View

class MainActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        attachResID = R.layout.activity_main
        setContentView(attachResID)
    }

    fun onClick(view: View) {
        startActivity(Intent(this, TwoActivity::class.java))
    }


}
