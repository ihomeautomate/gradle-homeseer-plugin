/*
 * Copyright 2014 iHomeAutomate - http://www.iHomeAutomate.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.ihomeautomate.gradle

import au.org.trogdor.xamarin.plugins.XamarinBuildPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class HomeSeerPlugin implements Plugin<Project> {
    static final String HOMESEER_GROUP = 'HomeSeer'
    static final String HOMESEER_DOWNLOAD_SDK = 'homeseerDownloadSdk'
    static final String HOMESEER_CLEAN_REFERENCES = 'homeseerCleanSdkReferences'
    static final String HOMESEER_COPY_REFERENCES = 'homeseerCopySdkReferences'

    void apply(Project project) {
        project.plugins.apply(XamarinBuildPlugin)

        project.extensions.create("homeseer", HomeSeerPluginExtension, project)
        configureDownloadSdk(project, getExplodedSdkDirectory(project))
        configureCopyReferences(project, getExplodedSdkDirectory(project))
        configureCleanReferences(project)
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

    private void configureCleanReferences(Project project) {
        project.afterEvaluate {
            if (project.homeseer) {
                CleanHSReferencesTask cleanHSReferencesTask = project.tasks.create(HOMESEER_CLEAN_REFERENCES, CleanHSReferencesTask)
                cleanHSReferencesTask.description = "Clean HomeSeer references from folder '${project.homeseer.sdkReferencesTargetDirectory}'."
                cleanHSReferencesTask.group = HOMESEER_GROUP
                cleanHSReferencesTask.relativeTargetDirectory = project.homeseer.sdkReferencesTargetDirectory
                cleanHSReferencesTask.configureUpToDateWhen()

                // Task dependencies... find Xamarin:clean and add dependsOn
                project.tasks.findByName("clean").dependsOn cleanHSReferencesTask
            }
        }
    }
}
