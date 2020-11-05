package com.badyouth.uploadplugin.apkInfo

/**
 * Mr.Liu
 *
 * 时间:2020/10/24
 * ----------------------
 * 描述:
 */
data class ApkInfo (
        /// app名字
        var appName: String,
        /// 需要用到的权限列表
        var usesPermissions: MutableList<String>
)