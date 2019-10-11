package com.yzw.skinuidemo

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView

/**
 * Create by yinzhengwei on 2019-09-24
 * @Function
 */
class Skinfactory : LayoutInflater.Factory2 {

    //所有控件的前缀
    val types = arrayOf("android.widget.", "android.view.", "android.webkit.")

    //定义装载需要换肤的容器
    val skinList = mutableListOf<SkinView>()

    override fun onCreateView(
        parent: View?,
        name: String?,
        context: Context?,
        attrs: AttributeSet?
    ): View? {
        var view: View? = null
//        for (index in 0 until attrs?.attributeCount!!) {
//            Log.d("Skinfactory", "$name == " + attrs.getAttributeName(index))
//        }
        //如果是自定义控件，则直接去实例化
        if (name!!.contains(".")) {
            try {
                view = LayoutInflater.from(context).createView(name, null, attrs)
            } catch (e: Exception) {
            }
        } else {
            //如果是不带包名的系统控件，则将全路径拼全
            types.forEach {
                try {
                    view = LayoutInflater.from(context).createView("$it$name", null, attrs)
                } catch (e: Exception) {
                }
                if (view != null) {
                    Log.d("Skinfactory", "view = $it$name")
                    return@forEach
                }
            }
        }
        //判断这个控件是否是要换肤的控件
        if (view != null) {
            parseView(view!!, attrs!!)
        }
        return view
    }

    /**
     * 实例化控件
     */
    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
        var view: View? = null
        try {
            //先根据包名找到改控件类
            val loadClass = context!!.classLoader.loadClass(name)
            //获取构造方法
            val constructor =
                loadClass.getConstructor(Context::class.java, AttributeSet::class.java)
            //执行构造方法
            view = constructor.newInstance(context, attrs) as View
        } catch (e: Exception) {
            //e.printStackTrace()
        }
        return view
    }

    /**
     * 每条属性的封装对象
     * name：属性名字，例如android:background="@color/colorAccent"中的background
     * typeName：属性值的类型，例如android:background="@color/colorAccent"中的color
     * entryName：属性值的名字，例如android:background="@color/colorAccent"中的colorAccent
     * resId：资源ID，例如编译后android:background="@12232424"中的12232424
     */
    class SkinItem(var name: String, var typeName: String, var entryName: String, var resId: Int)

    /**
     * view：控件
     * skinItems：属性集合
     */
    class SkinView(var view: View, var skinItems: MutableList<SkinItem>) {
        //执行换肤操作
        fun changeSkin() {
            skinItems.forEach {
                when (it.name) {
                    "textColor" -> {
                        if (view is TextView) {
                            (view as TextView).setTextColor(SkinManager.getColor(it.resId))
                        } else if (view is Button) {
                            (view as Button).setTextColor(SkinManager.getColor(it.resId))
                        }
                        //其他控件自行定义
                    }
                    /**
                     * 例如：
                     * android:background="@color/colorAccent"、
                     * android:background="@drawable/ic_launch"、
                     * android:background="@mipmap/ic_launch"
                     *
                     * 这里的typeName就是color或drawable
                     */
                    "background" -> {
                        when (it.typeName) {
                            "color" -> view.setBackgroundColor(SkinManager.getColor(it.resId))
                            "drawable", "mipmap" -> view.background =
                                SkinManager.getDrawable(it.resId)
                            // ....
                            else -> {
                            }
                        }
                    }
                    //src、textSize等等属性标记
                }
            }
        }
    }

    /**
     * 判断这个控件是否是要换肤的控件
     * 如果符合要求，则保存起来
     */
    fun parseView(view: View, attrs: AttributeSet) {
        val list = mutableListOf<SkinItem>()
        for (index in 0 until attrs.attributeCount) {
            //获取属性名字
            val attributeName = attrs.getAttributeName(index)
            //获取属性的属性值,eg: @12232424
            val attributeValue = attrs.getAttributeValue(index)

            //将带有标记换肤属性的控件装载起来
            if (attributeName.contains("textColor") || attributeName.contains("background")) {
                //资源ID
                val resId = attributeValue.substring(1).toInt()
                //获取属性值的类型
                val typeName = view.resources.getResourceTypeName(resId)
                //获取属性的值的名字
                val entryName = view.resources.getResourceEntryName(resId)

                //装载
                list.add(SkinItem(attributeName, typeName, entryName, resId))
            }
        }

        //将获取到的要换肤的控件和对应的属性集合保存起来
        if (list.isNotEmpty()) {
            skinList.add(SkinView(view, list))
        }
        Log.d("Skinfactory", "list =${skinList.size}")
    }

    //执行换肤
    fun changeSkin() {
        if (SkinManager.resources == null) {
            return
        }
        skinList.forEach {
            it.changeSkin()
        }
    }

}