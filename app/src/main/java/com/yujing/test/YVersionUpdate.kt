package com.yujing.test

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import com.yujing.url.YUrlAndroid
import com.yujing.url.contract.YUrlDownloadFileListener
import com.yujing.utils.*
import com.yujing.utils.YNoticeDownload.DownLoadComplete
import java.io.File
import kotlin.system.exitProcess

/**
 * **
 * 更新APP
 *
 * @author yujing 2020年4月30日11:03:00
 * 1.首先创建res/xml/file_paths.xml
 * 内容：
 * <?xml version="1.0" encoding="UTF-8"?>
 * <resources>
 * <paths>
 * <external -path path="" name="download"/>
 * </paths>
 * </resources>
 * 2.再在AndroidManifest.xml  中的application加入
 * <provider
 * android:name="androidx.core.content.FileProvider"
 * android:authorities="${applicationId}.provider"
 * android:exported="false"
 * android:grantUriPermissions="true">
 * <meta-data
 * android:name="android.support.FILE_PROVIDER_PATHS"
 * android:resource="@xml/file_paths" />
 * </provider>
 */
@Suppress("MemberVisibilityCanBePrivate", "FunctionName", "unused")
class YVersionUpdate {
    var activity: Activity? = null
    var serverCode //服务器版本
            = 0
    var isForceUpdate //是否强制更新
            = false
    var downUrl //下载路径
            : String? = null
    private var yNoticeDownload: YNoticeDownload? = null
    private var downLoadCompleteListener //下载成功回调
            : DownLoadComplete? = null
    var isUseNotificationDownload = true //是否使用通知栏下载

    private var yInstallApk: YInstallApk? = null

    fun setDownLoadCompleteListener(downLoadCompleteListener: DownLoadComplete?) {
        this.downLoadCompleteListener = downLoadCompleteListener
    }

    constructor() {}
    constructor(activity: Activity?, serverCode: Int, forceUpdate: Boolean, DownUrl: String?) {
        this.activity = activity
        this.serverCode = serverCode
        isForceUpdate = forceUpdate
        downUrl = DownUrl
        yInstallApk = YInstallApk(activity)
    }

    /**
     * 检查更新
     */
    fun checkUpdate() {
        if (serverCode > YUtils.getVersionCode(activity, activity!!.packageName)) {
            needUpdate()
        } else {
            noNeedUpdate()
        }
    }

    /**
     * 只提示更新
     */
    fun promptUpdate() {
        if (serverCode > YUtils.getVersionCode(activity, activity!!.packageName)) {
            needUpdate()
        }
    }

    /**
     * 不用更新
     */
    private fun noNeedUpdate() {
        val verCode = YUtils.getVersionCode(activity, activity!!.packageName) // 获取当前APK的版本值
        val verName =
            YUtils.getVersionName(activity, activity!!.packageName) // 获取当前APK的版本名称
        val sb = """
            当前版本名:$verName
            当前版本号:$verCode
            服务器版本号:$serverCode
            已是最新版,无需更新!
            """.trimIndent()
        val dialog =
            AlertDialog.Builder(activity).setTitle("软件更新").setMessage(sb) // 设置内容
                .setPositiveButton("确定", null).create() // 创建
        // 显示对话框
        if (Build.VERSION.SDK_INT >= 19 && (activity!!.isDestroyed || activity!!.isFinishing)) {
            return
        }
        dialog.show()
    }

    /**
     * 需要更新
     */
    private fun needUpdate() {
        val verCode = YUtils.getVersionCode(activity, activity!!.packageName)
        val verName = YUtils.getVersionName(activity, activity!!.packageName)
        val sb = """
            当前版本名:$verName
            当前版本号:$verCode
            服务器版本号:$serverCode
            发现新版本，是否更新?
            """.trimIndent()
        val dialog =
            AlertDialog.Builder(activity).setTitle("软件更新").setCancelable(!isForceUpdate)
                .setMessage(sb) // 设置内容
                .setPositiveButton(
                    "更新"  // 设置确定按钮
                ) { _, _ ->
                    if (isUseNotificationDownload) {
                        notifyDownApkFile()
                    } else {
                        yUrlDownApkFile()
                    }
                }
                .setNegativeButton(
                    if (isForceUpdate) "退出" else "暂不更新"
                ) { dialog, _ ->
                    // 判断强制更新
                    if (isForceUpdate) {
                        // 点击"取消"按钮之后退出程序
                        dialog.dismiss()
                        activity!!.finish()
                        exitProcess(0)
                    } else {
                        // 点击"取消"按钮之后退出程序
                        dialog.dismiss()
                    }
                }.create() // 创建
        // 显示对话框
        if (Build.VERSION.SDK_INT >= 19 && (activity!!.isDestroyed || activity!!.isFinishing)) {
            return
        }
        dialog.show()
    }

    /**
     * 自己下载器
     */
    private fun yUrlDownApkFile() {
        YShow.show(activity, "正在下载")
        YShow.setMessageOther("请稍候...")
        YShow.setCancel(isForceUpdate)
        val saveApkName = downUrl!!.substring(downUrl!!.lastIndexOf("/") + 1)
        val file =
            File(YPath.getFilePath(activity) + "/download/" + saveApkName)
        //下载
        YUrlAndroid.create().downloadFile(downUrl, file, object : YUrlDownloadFileListener {
            override fun progress(downloadSize: Int, fileSize: Int) {
                var progress = 100.0 * downloadSize / fileSize
                progress = (progress * 100).toInt().toDouble() / 100
                if (activity!!.isFinishing) {
                    YShow.finish()
                    return
                }
                val success = if (downloadSize > 1048576) {
                    "已下载:" + YNumber.showNumber(downloadSize / 1048576.0, 1) + "MB"
                } else {
                    "已下载:" + downloadSize / 1024 + "KB"
                }
                YShow.setMessage("下载进度$progress%")
                YShow.setMessageOther(success)
            }

            override fun success(file: File) {
                if (!activity!!.isFinishing) YShow.finish()
                YShow.setMessageOther("下载完成")
                if (downLoadCompleteListener != null) {
                    downLoadCompleteListener!!.complete(null, file)
                } else {
                    yInstallApk!!.install(file)
                }
            }

            override fun fail(value: String) {
                YShow.setMessage("下载失败")
                YShow.setMessageOther(null)
                YShow.setCancel(true)
                return
            }
        })
    }


    /**
     * 下载APK
     */
    private fun notifyDownApkFile() {
        YShow.show(activity, "正在下载")
        YShow.setMessageOther("请稍候...")
        //通知栏下载
        if (yNoticeDownload == null) yNoticeDownload = YNoticeDownload(activity)
        yNoticeDownload!!.url = downUrl
        yNoticeDownload!!.isAPK = true
        yNoticeDownload!!.setDownLoadComplete { uri, file ->
            if (!activity!!.isFinishing) YShow.finish()
            if (downLoadCompleteListener != null) {
                downLoadCompleteListener!!.complete(uri, file)
            } else {
                yInstallApk!!.install(uri)
            }
        }
        yNoticeDownload!!.setDownLoadFail {
            if (!activity!!.isFinishing) {
                YShow.setMessage("下载失败")
                YShow.setMessageOther(null)
            }
        }
        yNoticeDownload!!.setDownLoadProgress { downloaded, total, progress ->
            if (progress != 100.0) {
                val success = if (downloaded > 1048576) {
                    "已下载:" + YNumber.showNumber(downloaded / 1048576.0, 1) + "MB"
                } else {
                    "已下载:" + downloaded / 1024 + "KB"
                }
                if (!activity!!.isFinishing) {
                    YShow.setMessage("下载进度$progress%")
                    YShow.setMessageOther(success)
                }
            } else {
                if (!activity!!.isFinishing) {
                    YShow.setMessage("下载进度$progress%")
                    YShow.setMessageOther("下载完成")
                }
            }
        }
        yNoticeDownload!!.start()
    }

    fun onResume() {
        if (yNoticeDownload != null) yNoticeDownload!!.onResume()
    }

    //需要调用
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        yInstallApk!!.onActivityResult(requestCode, resultCode, data)
    }

    //需要调用
    fun onDestroy() {
        //注销广播
        if (yNoticeDownload != null) yNoticeDownload!!.onDestroy()
    }
}
