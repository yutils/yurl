package com.yujing.test

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import com.yujing.url.YUrlAndroid
import com.yujing.url.contract.YUrlDownloadFileListener
import com.yujing.utils.*
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

    //服务器版本
    var serverCode = 0

    //是否强制更新
    var isForceUpdate = false

    //下载路径
    var downUrl: String? = null
    private var yNoticeDownload: YNoticeDownload? = null

    //是否使用通知栏下载
    var isUseNotificationDownload = true

    //安装apk
    private var yInstallApk: YInstallApk? = null

    //私有默认构造函数
    private constructor()

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
        if (serverCode > YUtils.getVersionCode(activity)) needUpdate()
        else noNeedUpdate()
    }

    /**
     * 需要更新时候才弹出，否则不弹出
     */
    fun promptUpdate() {
        if (serverCode > YUtils.getVersionCode(activity)) needUpdate()
    }

    /**
     * 不用更新
     */
    private fun noNeedUpdate() {
        val sb = """
            当前版本名:${YUtils.getVersionName(activity)}
            当前版本号:${YUtils.getVersionCode(activity)}
            服务器版本号:$serverCode
            已是最新版,无需更新!
            """.trimIndent()
        val dialog = AlertDialog.Builder(activity).setTitle("软件更新").setMessage(sb) // 设置内容
            .setPositiveButton("确定", null).create() // 创建
        // 显示对话框
        if (activity!!.isDestroyed || activity!!.isFinishing)
            return
        dialog.show()
    }

    /**
     * 需要更新
     */
    private fun needUpdate() {
        val sb = """
            当前版本名:${YUtils.getVersionName(activity)}
            当前版本号:${YUtils.getVersionCode(activity)}
            服务器版本号:$serverCode
            发现新版本，是否更新?
            """.trimIndent()
        val dialog = AlertDialog.Builder(activity).setTitle("软件更新").setCancelable(!isForceUpdate)
            .setMessage(sb).setPositiveButton("更新") { _, _ ->
                if (isUseNotificationDownload) notifyDownApkFile() else yUrlDownApkFile()
            }.setNegativeButton(
                if (isForceUpdate) "退出" else "暂不更新"
            ) { dialog, _ ->
                // 判断强制更新
                if (isForceUpdate) {
                    dialog.dismiss()
                    activity!!.finish()
                    exitProcess(0)
                } else dialog.dismiss()
            }.create() // 创建
        // 显示对话框
        if (activity!!.isDestroyed || activity!!.isFinishing)
            return
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
        val file = File(YPath.getFilePath(activity) + "/download/" + saveApkName)
        //下载
        YUrlAndroid.create().downloadFile(downUrl, file, object : YUrlDownloadFileListener {
            override fun progress(downloadSize: Int, fileSize: Int) {
                if (!activity!!.isFinishing) {
                    val progress = YNumber.showNumber0(100.0 * downloadSize / fileSize)
                    YShow.setMessage("下载进度$progress%")
                    YShow.setMessageOther(
                        if (downloadSize > 1048576) "已下载:" +
                                YNumber.showNumber(downloadSize / 1048576.0, 1) + "MB"
                        else "已下载:" + downloadSize / 1024 + "KB"
                    )
                } else YShow.finish()
            }

            override fun success(file: File) {
                if (!activity!!.isFinishing) {
                    if (!activity!!.isFinishing) YShow.finish()
                    YShow.setMessageOther("下载完成")
                    yInstallApk!!.install(file)
                }
            }

            override fun fail(value: String) {
                if (!activity!!.isFinishing) {
                    YShow.setMessage("下载失败")
                    YShow.setMessageOther(null)
                    YShow.setCancel(true)
                }
            }
        })
    }

    /**
     * 通知栏下载APK
     */
    private fun notifyDownApkFile() {
        YShow.show(activity, "正在下载")
        YShow.setMessageOther("请稍候...")
        if (yNoticeDownload == null) yNoticeDownload = YNoticeDownload(activity)
        yNoticeDownload!!.url = downUrl
        yNoticeDownload!!.isAPK = true
        yNoticeDownload!!.setDownLoadComplete { uri, _ ->
            if (!activity!!.isFinishing) YShow.finish()
            else yInstallApk!!.install(uri)
        }
        yNoticeDownload!!.setDownLoadFail {
            if (!activity!!.isFinishing) {
                YShow.setMessage("下载失败")
                YShow.setMessageOther(null)
            }
        }
        yNoticeDownload!!.setDownLoadProgress { downloaded, _, progress ->
            if (!activity!!.isFinishing) {
                if (progress != 100.0) {
                    YShow.setMessage("下载进度$progress%")
                    YShow.setMessageOther(
                        if (downloaded > 1048576)
                            "已下载:" + YNumber.showNumber(downloaded / 1048576.0, 1) + "MB"
                        else "已下载:" + downloaded / 1024 + "KB"
                    )
                } else {
                    YShow.setMessage("下载进度$progress%")
                    YShow.setMessageOther("下载完成")
                }
            } else YShow.finish()
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

    //需要调用,注销广播
    fun onDestroy() {
        if (yNoticeDownload != null) yNoticeDownload!!.onDestroy()
    }
}