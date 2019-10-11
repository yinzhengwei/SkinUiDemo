package com.yzw.skinuidemo

import android.util.Log
import android.view.View
import android.widget.Toast

/**
 * Create by yinzhengwei on 2019-10-10
 * @Function 利用hook大法，对点击事件进行拦截
 */
object Utils {

    fun hookOnClickListener(view: View) {
        try {
            // 得到 View 的 ListenerInfo 对象
            val getListenerInfo = View::class.java!!.getDeclaredMethod("getListenerInfo")
            getListenerInfo.isAccessible = true
            val listenerInfo = getListenerInfo.invoke(view)

            // 得到 原始的 OnClickListener 对象
            val listenerInfoClz = Class.forName("android.view.View\$ListenerInfo")
            val mOnClickListener = listenerInfoClz.getDeclaredField("mOnClickListener")
            mOnClickListener.isAccessible = true
            val originOnClickListener = mOnClickListener.get(listenerInfo) as View.OnClickListener

            // 创建自定义的OnClickListener，埋点数据
            val hookedOnClickListener = HookedOnClickListener(originOnClickListener)

            //用自定义的 OnClickListener 替换原始的 OnClickListener
            mOnClickListener.set(listenerInfo, hookedOnClickListener)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class HookedOnClickListener(val origin: View.OnClickListener) : View.OnClickListener {
        override fun onClick(v: View) {
            Toast.makeText(v.context, "hook click", Toast.LENGTH_SHORT).show()
            Log.i("bindService", "Before click, do what you want to to.")
            origin.onClick(v)
            Log.i("bindService", "After click, do what you want to to.")
        }
    }
}