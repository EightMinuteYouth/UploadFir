package com.badyouth.uploadplugin.request

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.buffer


/**
 * 时间:2020/10/23
 * ----------------------
 * 描述:
 */
class ProxyRequestBody(var requestBody: MultipartBody,
                       var progressListener: (total: Long, current: Float) -> Unit)
    : RequestBody() {

    var mCurrentLength = 0.toFloat()

    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    override fun writeTo(sink: BufferedSink) {
        /// 获取总长度
        val contentLength = contentLength()

        /// 通过ForwardingSink代理的回调来获取写入的长度
        val forwardingSink = object : ForwardingSink(sink) {
            override fun write(source: Buffer, byteCount: Long) {
                mCurrentLength += byteCount
                progressListener(contentLength, mCurrentLength)
                super.write(source, byteCount)
            }
        }

        /// 转一把
        val bufferedSink = forwardingSink.buffer()
        /// 写入请求头
        requestBody.writeTo(bufferedSink)
        /// 刷新，RealConnection 连接池
        bufferedSink.flush()
    }

}