package com.badyouth.uploadplugin.request

import com.badyouth.uploadplugin.Logger
import com.badyouth.uploadplugin.model.CertificateModel
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit


/**
 * 时间:2020/10/23
 * ----------------------
 * 描述: 上传请求
 */
object UploadRequest {

    /// 获取上传凭证
    private const val uploadCertificate = "http://api.bq04.com/apps"


    private val client by lazy {
        OkHttpClient().newBuilder()
            /// 设置连接超时时间
            .connectTimeout(30, TimeUnit.SECONDS)
            /// 设置读取超时时间
            .readTimeout(30, TimeUnit.SECONDS)
            /// 设置写入超时时间
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /// 获取fir上传凭证内容
    fun requestCertificateUrl(bundleId: String, apiToken: String): CertificateModel? {
        var model: CertificateModel? = null

        try {
            val request = Request.Builder()
                .url(uploadCertificate)
                .post(
                    FormBody.Builder()
                        .add("type", "android")
                        .add("bundle_id", bundleId)
                        .add("api_token", apiToken)
                        .build()
                )
                .build()

            val response = client.newCall(request).execute() //获取Response对象
            model = response.body?.let {
                Gson().fromJson(it.string(), CertificateModel::class.java)
            }

        } catch (e: Exception) {
            Logger.log("异常 输出 ${e.message}")
        } finally {
            return model
        }

    }

    fun uploadApk(uploadUrl: String, params: MutableMap<String, String>, filePath: String) {

        val file = File(filePath)

        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", file.name,
                file.asRequestBody("application/octet-stream".toMediaType())
            )

        params.forEach {
            builder.addFormDataPart(it.key, it.value)
        }

        /// 建立一个代理获取输出流操作
        val requestBody = ProxyRequestBody(builder.build()) { total, current ->
            run {
                Logger.log(
                    "当前上传进度 =  ${DecimalFormat("0.00%")
                        .format(current / total)}"
                )
            }
        }


        try {
            val request = Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute() //获取Response对象
            Logger.log("当前code = ${response.code} msg = ${response.message}")
            if (response.code == 200) {
                Logger.log("提交完成")
                return
            }

            Logger.log("提交错误 = ${response.message}")
        } catch (e: Exception) {
            Logger.log("异常 输出 ${e.message}")
        }
    }
}
