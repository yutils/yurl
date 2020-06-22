package com.yujing.test

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.yujing.test.text.FarmersInfo
import com.yujing.test.text.YResponse
import com.yujing.url.YUrlAndroid
import com.yujing.url.contract.YObjectListener
import com.yujing.url.contract.YUrlDownloadFileListener
import com.yujing.url.contract.YUrlListener
import com.yujing.utils.YShow
import com.yujing.utils.YToast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun init() {
        //var a=findViewById<Button>(R.id.button1)
        button1.text = "yUrl网络请求"
        button1.setOnClickListener { net1() }
        button2.text = "测试post"
        button2.setOnClickListener { net2() }
        button3.text = "RequestProperty"
        button3.setOnClickListener { net3() }
        button4.text = "44444444"
        button4.setOnClickListener { net4() }
        button5.text = "登录获取session"
        button5.setOnClickListener { net5() }
        button6.text = "通过session获取信息"
        button6.setOnClickListener { net6() }
        button7.text = "App更新"
        button7.setOnClickListener { update() }
        button8.text = "文件下载"
        button8.setOnClickListener { downLoad() }
    }

    private fun net2() {
        var url = "http://192.168.1.120:10007/api/SweepCode/JjdTwoDownload"
//         url = "http://www.baidu.com"
        var p =
            "{\"DeviceNo\":\"868403023178079\",\"BatchNum\":\"54511002\",\"Command\":112,\"MsgID\":1}"
        YUrlAndroid.create().post(url, p, object : YUrlListener {
            override fun success(bytes: ByteArray?, value: String?) {
            }

            override fun fail(value: String?) {

            }
        })
    }

    private fun net1() {
        val map: MutableMap<String, Any> = HashMap()
        map["Command"] = 1
        map["MsgId"] = 0
        map["DeviceNo"] = "HJWV1X7SEL"
        map["CardId"] = "6214572180001408813"
        val url = "http://192.168.1.170:10136/api/SelfPrint/FarmersInfo"
        YShow.show(this, "网络请求")
        YUrlAndroid.create()
            .post(url, Gson().toJson(map), object : YObjectListener<YResponse<FarmersInfo>>() {
                override fun success(bytes: ByteArray?, value: YResponse<FarmersInfo>?) {
                    runOnUiThread {
                        YShow.finish()
                        YToast.show(App.get(), value?.data?.Name)
                    }
                }
                override fun fail(value: String) {
                    runOnUiThread {
                        YShow.finish()
                        YToast.show(App.get(), value)
                    }
                }
            })
    }


    private fun net3() {
        val url = "https://creator.douyin.com/aweme/v1/creator/data/billboard?billboard_type=1"
        YUrlAndroid.create().addRequestProperty("Referer", url).get(url, object : YUrlListener {
            override fun fail(value: String?) {
                text2.text = "失败：$value"
            }

            override fun success(bytes: ByteArray?, value: String?) {
                text2.text = "成功：$value"
            }
        })
    }

    private fun net4() {

    }

    var session = ""
    private fun net5() {
        val url = "http://crash.0-8.top:8888/crash/user/login"
        val hashMap: HashMap<String, Any> = hashMapOf("name" to "yujing", "password" to "wtugeqh")
        YUrlAndroid.create().setSessionListener { sessionId ->
            session = sessionId
            runOnUiThread { text1.text = "sessionId：$sessionId" }
        }.post(url, hashMap, object : YUrlListener {
            override fun success(bytes: ByteArray?, value: String?) {
                text2.text = "成功：$value"
            }
            override fun fail(value: String?) {
                text2.text = "失败：$value"
            }
        })
    }

    private fun net6() {
        val url = "http://crash.0-8.top:8888/crash/"
        YUrlAndroid.create().setSessionId(session).get(url, object : YUrlListener {
            override fun success(bytes: ByteArray?, value: String?) {
                text2.text = "成功：$value"
            }
            override fun fail(value: String?) {
                text2.text = "失败：$value"
            }
        })
    }


    private fun update() {
        val url = "http://dldir1.qq.com/qqfile/qq/QQ8.9.2/20760/QQ8.9.2.exe"
        val yVersionUpdate = YVersionUpdate(this, 100, false, url)
        yVersionUpdate.isUseNotificationDownload = false
        yVersionUpdate.checkUpdate()
    }

    private fun downLoad() {
        val url = "http://dldir1.qq.com/qqfile/qq/QQ8.9.2/20760/QQ8.9.2.exe"
        var f = File(getFilePath(this, "cs") + "/BB.exe")

        YUrlAndroid.create().downloadFile(url, f, object : YUrlDownloadFileListener {
            override fun progress(downloadSize: Int, fileSize: Int) {
                text1.text = "$downloadSize/$fileSize"
                var progress = 100.0 * downloadSize / fileSize
                progress = (progress * 100).toInt().toDouble() / 100
                text2.text = "进度：$progress%"
            }

            override fun success(file: File) {}
            override fun fail(value: String) {}
        })
    }


    fun getFilePath(context: Context, dir: String): String? {
        val directoryPath: String?
        directoryPath =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) { //判断外部存储是否可用
                try {
                    context.getExternalFilesDir(dir)!!.absolutePath
                } catch (e: Exception) {
                    context.filesDir.toString() + File.separator + dir
                }
            } else { //没外部存储就使用内部存储
                context.filesDir.toString() + File.separator + dir
            }
        val file = File(directoryPath)
        if (!file.exists()) { //判断文件目录是否存在
            file.mkdirs()
        }
        return directoryPath
    }
}
