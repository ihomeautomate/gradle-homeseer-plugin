package eu.ihomeautomate.gradle

import au.org.trogdor.xamarin.plugins.XamarinBuildPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class HomeSeerPlugin implements Plugin<Project> {
    static final String HOMESEER_GROUP = 'HomeSeer'
    static final String HOMESEER_DOWNLOAD_SDK = 'homeseerDownloadSdk'
    static final String HOMESEER_COPY_REFERENCES = 'homeseerCopySdkReferences'

    void apply(Project project) {
        project.plugins.apply(XamarinBuildPlugin)

        project.extensions.create("homeseer", HomeSeerPluginExtension, project)
        configureDownloadSdk(project, getExplodedSdkDirectory(project))
        configureCopyReferences(project, getExplodedSdkDirectory(project))
    }

    static File getExplodedSdkDirectory(Project project) {
        new File(project.gradle.gradleUserHomeDir, 'homeseer-sdk')
    }

    private void configureDownloadSdk(Project project, File explodedSdkDirectory) {
        project.afterEvaluate {
            if (project.homeseer) {
                DownloadSdkTask downloadSdkTask = project.tasks.create(HOMESEER_DOWNLOAD_SDK, DownloadSdkTask)
                downloadSdkTask.description = 'Downloads and sets HomeSeer SDK.'
                downloadSdkTask.group = HOMESEER_GROUP
                downloadSdkTask.explodedSdkDirectory = explodedSdkDirectory
                downloadSdkTask.homeseerSdkDownloadUrl = project.homeseer.homeseerSdkDownloadUrl
                downloadSdkTask.configureUpToDateWhen()
            }
        }
    }

    private void configureCopyReferences(Project project, File explodedSdkDirectory) {
        project.afterEvaluate {
            if (project.homeseer) {
                DownloadSdkTask downloadSdkTask = project.tasks.findByName(HOMESEER_DOWNLOAD_SDK)

                CopyHSReferencesTask copyHSReferencesTask = project.tasks.create(HOMESEER_COPY_REFERENCES, CopyHSReferencesTask)
                copyHSReferencesTask.description = 'Copies necessary HomeSeer references.'
                copyHSReferencesTask.group = HOMESEER_GROUP
                copyHSReferencesTask.dependsOn downloadSdkTask

                copyHSReferencesTask.explodedSdkDirectory = explodedSdkDirectory
                copyHSReferencesTask.homeseerSdkDownloadUrl = project.homeseer.homeseerSdkDownloadUrl
                copyHSReferencesTask.targetDirectory = project.file(project.homeseer.sdkReferencesTargetDirectory)
                copyHSReferencesTask.configureUpToDateWhen()

                // Task dependencies... find all Xamarin:build* tasks and add dependsOn
                project.tasks.findAll { task -> task.name.startsWith('build') && task.group == XamarinBuildPlugin.TASK_GROUP }
                .each { task ->
                    task.dependsOn copyHSReferencesTask
                }
            }
        }
    }
}
