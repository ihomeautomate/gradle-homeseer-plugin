/*
 * Copyright 2014 iHomeAutomate - http://www.iHomeAutomate.eu
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

    void configureUpToDateWhen() {
        File sourceFolder = getSdkHome()

        inputs.files "${sourceFolder.canonicalPath}}/HomeSeerAPI.dll",
                     /*"${sourceFolder.canonicalPath}}/HomeSeerUtil.dll",*/
                     "${sourceFolder.canonicalPath}}/Scheduler.dll",
                     "${sourceFolder.canonicalPath}}/HSCF.dll"

        outputs.files "${targetDirectory.canonicalPath}/HomeSeerAPI.dll",
                      /*"${targetDirectory.canonicalPath}/HomeSeerUtil.dll",*/
                      "${targetDirectory.canonicalPath}/Scheduler.dll",
                      "${targetDirectory.canonicalPath}/HSCF.dll"
    }
}