package com.badyouth.uploadplugin

import com.android.build.gradle.internal.scope.VariantScope
import com.badyouth.uploadplugin.internal.getVariantManager
import com.badyouth.uploadplugin.model.UploadModel
import com.badyouth.uploadplugin.tasks.UploadTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 时间:2020/10/22
 * ----------------------
 * 描述:
 */
class UploadPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        /// 判断是否在app的build下面
        checkApplicationPlugin(project)

        /// 加载上传配置
        project.extensions.create("uploadConfig", UploadModel::class.java)
        project.afterEvaluate {
            getVariantManager(project).variantScopes.forEach { scope ->
                createUploadTask(project, scope)
            }
        }
    }

    private fun createUploadTask(project: Project, scope: VariantScope) {
        val variantName = scope.variantData.name.capitalize()
        val bundleTaskName = "bundle$variantName"
        if (project.tasks.findByName(bundleTaskName) == null) {
            return
        }

//        Logger.log("variantName = $variantName")
//        Logger.log("bundleTaskName = $bundleTaskName")

        /// 将要生成task的名字
        val taskName = "upload${variantName}Apk"
        Logger.log("生成键值名 = $taskName")

        /// 创建一个上传的task
        val uploadTask = project.tasks.create(taskName, UploadTask::class.java)
        uploadTask.setVariantScope(scope)
        uploadTask.dependsOn("assemble$variantName")
    }

    private fun checkApplicationPlugin(project: Project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw  GradleException("Android Application plugin required")
        }
    }
}