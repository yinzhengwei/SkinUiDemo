package com.yzw.skinuidemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import java.lang.Exception
import kotlin.reflect.full.createInstance

/**
 * Create by yinzhengwei on 2019-09-24
 * @Function
 */
@SuppressLint("StaticFieldLeak")
object SkinManager {

    //每个界面的上下文
    var context: Context? = null

    //获取到的资源对象
    var resources: Resources? = null

    //获取插件的包名
    var packageName = ""

    /**
     * 加载要换肤的插件
     *
     * 最终得到resource对象
     */
    fun loadApk(path: String) {
        val manager = context!!.packageManager
        //获取皮肤插件的包信息类
        val archiveInfo = manager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)

        //获取包名
        packageName = archiveInfo.packageName
        try {
            val assetManager = AssetManager::class.createInstance()

            //得到替换加载路径的方法
            val declaredMethod =
                assetManager!!.javaClass.getDeclaredMethod("addAssetPath", String::class.java)

            //执行方法
            declaredMethod.invoke(assetManager, path)

            //创建皮肤插件的资源对象
            resources = Resources(
                assetManager,
                context!!.resources.displayMetrics,
                context!!.resources.configuration
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 去插件apk中获取颜色的资源
     *
     * resId:主app的color资源ID
     */
    fun getColor(resId: Int): Int {
        if (resources == null) {
            return resId
        }
        //获取属性值的名字
        val entryName = context!!.resources!!.getResourceEntryName(resId)
        //获取属性值的类型
        val typeName = context!!.resources.getResourceTypeName(resId)

        //根据资源的类型以及名字，去皮肤插件apk中寻找
        val id = resources!!.getIdentifier(entryName, typeName, packageName)

        //如果插件中没有找到资源，则将原资源ID返回
        if (id == 0) {
            return resId
        }
        return resources!!.getColor(id)
    }

    /**
     * 去插件apk中获取颜色的资源
     *
     * resId:主app的drawable资源ID
     *
     */
    fun getDrawable(resId: Int): Drawable? {
        if (resources == null) {
            return ContextCompat.getDrawable(context!!, resId)
        }
        //获取属性值的名字
        val entryName = context!!.resources!!.getResourceEntryName(resId)
        //获取属性值的类型
        val typeName = context!!.resources.getResourceTypeName(resId)

        //根据资源的类型以及名字，去皮肤插件apk中寻找
        val id = resources!!.getIdentifier(entryName, typeName, packageName)

        //如果插件中没有找到资源，则将原资源ID返回
        if (id == 0) {
            return ContextCompat.getDrawable(context!!, resId)
        }
        return resources!!.getDrawable(id)
    }


}