package com.badyouth.uploadplugin

/**
 * 时间:2020/10/22
 * ----------------------
 * 描述: 打印工具类
 */
object Logger {

    private const val TAG = "【UploadPlugin】"

    fun log(msg: String) {
        println(wrapMsg(msg))
    }

    private fun wrapMsg(msg: String): String {
        return "$TAG:${msg}"
    }
}