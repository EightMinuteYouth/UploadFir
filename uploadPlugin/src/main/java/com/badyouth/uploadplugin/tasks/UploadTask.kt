package com.badyouth.uploadplugin.tasks

import com.android.build.gradle.internal.scope.VariantScope
import com.badyouth.uploadplugin.Logger
import com.badyouth.uploadplugin.model.UploadModel
import com.badyouth.uploadplugin.request.UploadRequest.requestCertificateUrl
import com.badyouth.uploadplugin.request.UploadRequest.uploadApk
import com.badyouth.uploadplugin.utils.ApkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间:2020/10/22
 * ----------------------
 * 描述:
 */
open class UploadTask : DefaultTask() {

    /// 获取当前执行项
    private lateinit var variantScope: VariantScope

    /// 获取配置文件
    private var uploadModel: UploadModel = project.extensions.getByName("uploadConfig") as UploadModel

    init {
        description = "自动上传"
        group = "upLoad"
        outputs.upToDateWhen { false }
    }

    fun setVariantScope(variantScope: VariantScope) {
        this.variantScope = variantScope
    }

    @TaskAction
    fun apply() {
        Logger.log("打包完成执行上传")
        Logger.log("uploadConfig配置输出 = $uploadModel")

        val filePath = ApkUtil.findApkFilePath(variantScope)

        Logger.log("获取到当前文件位置为 = $filePath")

        /// 获取App输出信息
        val apkData = variantScope.outputScope.mainSplit

        /// 获取到显示app的名字
        val appName = ApkUtil.getApkName(filePath, variantScope)

        /// 启动协程阻塞当前主线程
        runBlocking {

            /// 请求fir凭证
            /// withContext {}不会创建新的协程，在指定协程上运行挂起代码块，并挂起该协程直至代码块运行完成
            val certificateModel = withContext(Dispatchers.Default) {
                requestCertificateUrl(
                        variantScope.variantData.applicationId, uploadModel.apiToken)
            }

            certificateModel?.let {
                val bean = it.cert.binary

                /// 拼接上传信息
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val outTime = dateFormat.format(Date())
                val description = "打包时间: $outTime \n\n" +
                        "当前开发版本: ${apkData.baseName} \n\n" +
                        "功能描述: ${uploadModel.description}"

                uploadApk(bean.upload_url, mutableMapOf(
                        "key" to bean.key,
                        "token" to bean.token,
                        "x:name" to appName,
                        "x:version" to apkData.versionName,
                        "x:build" to apkData.versionCode.toString(),
                        "x:changelog" to description
                ), filePath)
            }
        }
    }

}