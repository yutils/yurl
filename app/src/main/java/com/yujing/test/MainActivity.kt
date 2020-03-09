package com.yujing.test

import android.content.Context
import android.os.Environment
import com.yujing.url.YUrlAndroid
import com.yujing.url.contract.YUrlDownloadFileListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun init() {
        //var a=findViewById<Button>(R.id.button1)
        button1.setOnClickListener { show("123") }
        button2.setOnClickListener { }
        button3.setOnClickListener { }
        button4.setOnClickListener { }
        button5.setOnClickListener { }
        button6.setOnClickListener { }
        button7.text="App更新"
        button7.setOnClickListener { update()}
        button8.text="文件下载"
        button8.setOnClickListener { downLoad() }
    }

    private fun update() {
        val url = "http://dldir1.qq.com/qqfile/qq/QQ8.9.2/20760/QQ8.9.2.exe"
        val yVersionUpdate = YVersionUpdate(this,2,false,url)
        yVersionUpdate.isUseNotificationDownload=false
        yVersionUpdate.checkUpdate()
    }

    private fun downLoad() {
        val url = "http://dldir1.qq.com/qqfile/qq/QQ8.9.2/20760/QQ8.9.2.exe"
        var f = File(getFilePath(this, "cs") + "/BB.exe")

        YUrlAndroid.create().downloadFile(url, f, object : YUrlDownloadFileListener {
            override fun progress(downloadSize: Int, fileSize: Int) {
                text1.text= "$downloadSize/$fileSize"
                var progress = 100.0 * downloadSize / fileSize
                progress = (progress * 100).toInt().toDouble() / 100
                text2.text= "进度：$progress%"
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
