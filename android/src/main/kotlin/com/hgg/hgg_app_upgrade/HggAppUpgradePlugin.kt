package com.hgg.hgg_app_upgrade

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File
import java.util.*

/** HggAppUpgradePlugin */
class HggAppUpgradePlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var mContext: Context

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        mContext = flutterPluginBinding.applicationContext;
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "hgg_app_upgrade")
        channel.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getAppInfo") {
            getAppInfo(mContext, result)
        } else if (call.method == "getApkDownloadPath") {
            result.success(mContext.getExternalFilesDir("")?.absolutePath)
        } else if (call.method == "install") {
            //安装app
            val path = call.argument<String>("path")
            path?.also {
                startInstall(mContext, it)
            }
        } else if (call.method == "getInstallMarket") {
            val packageList = getInstallMarket(call.argument<List<String>>("packages"))
            result.success(packageList)
        } else if (call.method == "toMarket") {
            val marketPackageName = call.argument<String>("marketPackageName")
            val marketClassName = call.argument<String>("marketClassName")
            val marketName = call.argument<String>("marketName")
            toMarket(mContext, marketPackageName, marketClassName, marketName)
        } else {
            result.notImplemented()
        }
    }


    /**
     * 获取app信息
     */
    private fun getAppInfo(context: Context, result: Result) {
        context.also {
            val packageInfo = it.packageManager.getPackageInfo(it.packageName, 0)
            val map = HashMap<String, String>()
            map["packageName"] = packageInfo.packageName
            map["versionName"] = packageInfo.versionName
            map["versionCode"] = "${packageInfo.versionCode}"
            result.success(map)
        }
    }


    /**
     * 安装app，android 7.0及以上和以下方式不同
     */
    private fun startInstall(context: Context, path: String) {
        val file = File(path)
        if (!file.exists()) {
            return
        }

        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //7.0及以上
            println("安装地址:$path")
            val contentUri =
                FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", file)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
            context.startActivity(intent)
        } else {
            //7.0以下
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }


    /**
     * 获取已安装应用商店的包名列表
     */
    private fun getInstallMarket(packages: List<String>?): List<String> {
        val pkgs = ArrayList<String>()
        packages?.also {
            for (i in packages.indices) {
                if (isPackageExist(mContext, packages.get(i))) {
                    pkgs.add(packages.get(i))
                }
            }
        }
        return pkgs
    }


    /**
     * 是否存在当前应用市场
     *
     */
    private fun isPackageExist(context: Context, packageName: String?): Boolean {
        val manager = context.packageManager
        val intent = Intent().setPackage(packageName)
        val infos = manager.queryIntentActivities(
            intent, 0
        )
        return infos.size > 0
    }


    /**
     * 直接跳转到指定应用市场
     *
     * @param context
     * @param packageName
     */
    private fun toMarket(
        context: Context,
        marketPackageName: String?,
        marketClassName: String?,
        marketName: String?
    ) {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val uri =
                Uri.parse("market://details?id=${packageInfo.packageName}")
            val nameEmpty = marketPackageName == null || marketPackageName.isEmpty()
            val classEmpty = marketClassName == null || marketClassName.isEmpty()
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            if (nameEmpty || classEmpty) {
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            } else {
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (marketPackageName != null && marketClassName != null) {
                    goToMarket.setClassName(marketPackageName, marketClassName)
                }
            }
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(context, "您的手机没有安装应用商店($marketName)", Toast.LENGTH_SHORT).show()
        }
    }

}
