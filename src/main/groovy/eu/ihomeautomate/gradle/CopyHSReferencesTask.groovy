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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class CopyHSReferencesTask extends DefaultTask {

    String homeseerSdkDownloadUrl
    File explodedSdkDirectory
    File targetDirectory

    @TaskAction
    void start() {
        assert getHomeseerSdkDownloadUrl() != null, 'homeseerSdkDownloadUrl must not be null'
        assert getExplodedSdkDirectory() != null, 'explodedSdkDirectory must not be null'
        assert getTargetDirectory() != null, 'targetDirectory must not be null'

        File sourceFolder = getSdkHome()
        if (!sourceFolder.exists()) {
            throw new GradleException("Could not find exploded HomeSeer SDK directory in ${sourceFolder.canonicalPath}")
        }

        project.copy {
            from ("${sourceFolder.canonicalPath}") {
                include 'HomeSeerAPI.dll'
                /*include 'HomeSeerUtil.dll'*/
                include 'Scheduler.dll'
                include 'HSCF.dll'
            }
            into "${targetDirectory}"
        }
    }

    String getBaseName(String filename) {
        filename.tokenize(".")[0]
    }

    String getFilenameFromDownloadUrl() {
        getHomeseerSdkDownloadUrl().tokenize("/")[-1]
    }

    File getSdkHome() {
        def filename = getFilenameFromDownloadUrl()
        def sourceFolderName = getBaseName(filename)
        return new File("${getExplodedSdkDirectory()}/${sourceFolderName}")
    }

    boolean isUpToDate() {
        File sourceFolder = getSdkHome()

        File homeSeerAPITarget = new File("${targetDirectory.canonicalPath}/HomeSeerAPI.dll")
        File schedulerTarget = new File("${targetDirectory.canonicalPath}/Scheduler.dll")
        File hscfTarget = new File("${targetDirectory.canonicalPath}/HSCF.dll")
        File homeSeerAPISource = new File("${sourceFolder.canonicalPath}/HomeSeerAPI.dll")
        File schedulerSource = new File("${sourceFolder.canonicalPath}/Scheduler.dll")
        File hscfSource = new File("${sourceFolder.canonicalPath}/HSCF.dll")

        if (!homeSeerAPITarget.exists() || !schedulerTarget.exists() || !hscfTarget.exists()) {
            return false
        }

        ant.checksum(file: homeSeerAPITarget.canonicalPath, property: homeSeerAPITarget.canonicalPath)
        ant.checksum(file: schedulerTarget.canonicalPath, property: schedulerTarget.canonicalPath)
        ant.checksum(file: hscfTarget.canonicalPath, property: hscfTarget.canonicalPath)
        ant.checksum(file: homeSeerAPISource.canonicalPath, property: homeSeerAPISource.canonicalPath)
        ant.checksum(file: schedulerSource.canonicalPath, property: schedulerSource.canonicalPath)
        ant.checksum(file: hscfSource.canonicalPath, property: hscfSource.canonicalPath)

//        println "$homeSeerAPITarget.canonicalPath Checksum: ${ant.antProject.properties[homeSeerAPITarget.canonicalPath]}"
//        println "$schedulerTarget.canonicalPath Checksum: ${ant.antProject.properties[schedulerTarget.canonicalPath]}"
//        println "$hscfTarget.canonicalPath Checksum: ${ant.antProject.properties[hscfTarget.canonicalPath]}"
//        println "$homeSeerAPISource.canonicalPath Checksum: ${ant.antProject.properties[homeSeerAPISource.canonicalPath]}"
//        println "$schedulerSource.canonicalPath Checksum: ${ant.antProject.properties[schedulerSource.canonicalPath]}"
//        println "$hscfSource.canonicalPath Checksum: ${ant.antProject.properties[hscfSource.canonicalPath]}"

        return ant.antProject.properties[homeSeerAPITarget.canonicalPath] == ant.antProject.properties[homeSeerAPISource.canonicalPath] &&
                ant.antProject.properties[schedulerTarget.canonicalPath] == ant.antProject.properties[schedulerSource.canonicalPath] &&
                ant.antProject.properties[hscfTarget.canonicalPath] == ant.antProject.properties[hscfSource.canonicalPath]
    }

    void configureUpToDateWhen() {
        outputs.upToDateWhen {
            return isUpToDate()
        }
    }
}