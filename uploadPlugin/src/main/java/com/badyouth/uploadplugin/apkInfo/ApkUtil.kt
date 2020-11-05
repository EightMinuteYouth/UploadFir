package com.badyouth.uploadplugin.apkInfo

import com.android.utils.forEach
import com.badyouth.uploadplugin.Logger
import org.apache.commons.compress.archivers.zip.ZipFile
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Mr.Liu
 *
 * 时间:2020/10/24
 * ----------------------
 * 描述:
 */
object ApkUtil {

    fun parseApk(filePath: String) {
        var zipFile: ZipFile? = null
        try {
            zipFile = ZipFile(filePath)
            val enumeration =  zipFile.entries

            while (enumeration.hasMoreElements()){
                val zipEntry = enumeration.nextElement()

                /// 判断当前不为目录
                if (!zipEntry.isDirectory && zipEntry.name == "AndroidManifest.xml"){
                    val domfac = DocumentBuilderFactory.newInstance()
                    val domBuilder = domfac.newDocumentBuilder()
                    val doc = domBuilder.parse(zipFile.getInputStream(zipEntry))
                    val nodes = doc.documentElement.childNodes
                    nodes.forEach {
                        Logger.log("=====> ${it.nodeName}")
                    }
//                    val bufferedReader = zipFile.getInputStream(zipEntry).bufferedReader()
//
//                    val strings = bufferedReader.readLines()
//                    strings.forEach {
//                        Logger.log("=====> $it")
//                    }

                }
            }
        } catch (e: Exception){
            Logger.log("解析异常输出 ${e.message}")
        } finally {
            zipFile?.close()
        }
    }
}