package com.badyouth.uploadplugin.model

/**
 * 时间:2020/10/22
 * ----------------------
 * 描述:
 */
open class UploadModel {

    /// 上传描述
    var description = ""

    /// fir上传需要的token
    var apiToken = ""

    override fun toString(): String {
        return "UploadExtensions{\n" +
                "description=$description" +
                "\tapiToken=$apiToken"+
                "\n}"
    }
}