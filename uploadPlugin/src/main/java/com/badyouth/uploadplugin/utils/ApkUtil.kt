package com.badyouth.uploadplugin.utils

import com.android.SdkConstants
import com.android.build.gradle.internal.res.getAapt2FromMavenAndVersion
import com.android.build.gradle.internal.scope.VariantScope
import com.badyouth.uploadplugin.Logger
import org.gradle.api.file.ConfigurableFileCollection
import java.io.File

/**
 * 时间:2020/10/26
 * ----------------------
 * 描述:
 */
object ApkUtil {

    /**
     * 查找已经打包的apk路径
     */
    fun findApkFilePath(variantScope: VariantScope): String {
        /// 拿到当前打包所在文件夹
        /// 如 apk/debug
        return variantScope.apkLocation.walk().filter {
            it.path.endsWith(".apk")
        }.first().path
    }

    /**
     * 获取主项目 AndroidManifest.xml 里面的App名字
     */
    fun getManifestPath(variantScope: VariantScope): String? {
        val manifestProcessorTask = variantScope.taskContainer.processManifestTask?.orNull
                ?: return null

        return manifestProcessorTask.outputs.files.asFileTree
                /// 只获取merged_manifests合并过清单文件
                .filter { it.path.contains("merged_manifests") }
                .filter { it.extension == "xml" }
                .asPath
    }

    /**
     * 返回一个apk程序的信息
     */
    fun getApkName(apkPath: String, variantScope: VariantScope): String {
        /// 获取当前编译的aapt
        val (aapt2FromMaven, _) = getAapt2FromMavenAndVersion(variantScope.globalScope)
        val aaptPath = File(aapt2FromMaven.singleFile, SdkConstants.FN_AAPT2).toPath().toString()

        val process: Process = ProcessBuilder(aaptPath, "d", "badging", apkPath).redirectErrorStream(true).start()
        val list = process.inputStream.bufferedReader().readLines()
        val label = list.first { it.startsWith("application-label") }
        return label.substring(label.indexOf("'") + 1, label.length - 1)
    }
}