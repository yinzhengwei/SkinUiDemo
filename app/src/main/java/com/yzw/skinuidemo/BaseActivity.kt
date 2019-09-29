package com.yzw.skinuidemo

import android.app.Activity
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.util.Xml
import org.xmlpull.v1.XmlPullParser


/**
 * Create by yinzhengwei on 2019-09-24
 * @Function
 */
open class BaseActivity : Activity() {

    var skinfactory: Skinfactory? = null

    var attachResID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SkinManager.context = this

        skinfactory = Skinfactory()

        LayoutInflaterCompat.setFactory2(layoutInflater, skinfactory!!)
    }

    fun changeSkin() {
        skinfactory!!.changeSkin()
    }

    override fun onResume() {
        super.onResume()
        changeSkin()
    }

    override fun onDestroy() {
        super.onDestroy()
        skinfactory!!.skinList.clear()
    }
}