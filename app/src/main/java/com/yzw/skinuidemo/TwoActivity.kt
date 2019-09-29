package com.yzw.skinuidemo

import android.content.Context
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.util.AttributeSet
import android.util.Log
import android.util.Xml
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest

class TwoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        attachResID = R.layout.activity_two
        setContentView(attachResID)
    }

    fun onClick(view: View) {
        if (!isOpenPermisson(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                100
            )
            return
        }
        if (!isOpenPermisson(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
            return
        }
        //获取换肤的apk插件
        SkinManager.loadApk("${Environment.getExternalStorageDirectory()}/skin.apk")


        val viewGroup = window.decorView as ViewGroup

        skinfactory!!.parseView(viewGroup.getChildAt(0), skinfactory!!.attributeSet!!)

//        if (viewGroup.getChildAt(0) is ViewGroup) {
//            val parser = resources.getXml(attachResID)
//            val attrs = Xml.asAttributeSet(parser) as AttributeSet
////            val view = viewGroup.getChildAt(0)
////            Log.d("Skinfactory", viewGroup.resources.getResourceTypeName())
//
//            for (index in 0 until parser.attributeCount) {
//                Log.d("Skinfactory", parser.getAttributeName(index))
//            }
//        }
        //开始执行换肤操作
        changeSkin()
    }

    //判断某个权限是否已打开
    fun isOpenPermisson(context: Context, permission: String): Boolean {
        /**
         * 检查权限是否获取（android6.0及以上系统可能默认关闭权限，且没提示）
         *
         * 一般android6以下会在安装时自动获取权限,但在小米机上，可能通过用户权限管理更改权限,checkSelfPermission会始终是true，
         * targetSdkVersion<23时 即便运行在android6及以上设备 ContextWrapper.checkSelfPermission和Context.checkSelfPermission失效
         * 返回值始终为PERMISSION_GRANTED,此时必须使用PermissionChecker.checkSelfPermission
         */
        return if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED &&
            context.packageManager.checkPermission(
                permission,
                context.packageName
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            PermissionChecker.checkPermission(
                context,
                permission,
                Binder.getCallingPid(),
                Binder.getCallingUid(),
                context.packageName
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
    }


}
