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
import org.gradle.api.tasks.TaskAction

class CleanHSReferencesTask extends DefaultTask {

    String relativeTargetDirectory

    @TaskAction
    void start() {
        assert getRelativeTargetDirectory() != null, 'relativeTargetDirectory must not be null'

        project.delete(project.fileTree(dir:getRelativeTargetDirectory(), includes: ['HomeSeerAPI.dll', 'Scheduler.dll', 'HSCF.dll', 'HomeSeerUtil.dll']))
    }

    boolean isThereSomethingToCleanUp() {
        return project.file("${relativeTargetDirectory}/HomeSeerAPI.dll").exists() ||
               project.file("${relativeTargetDirectory}/Scheduler.dll").exists() ||
               project.file("${relativeTargetDirectory}/HSCF.dll").exists() ||
               project.file("${relativeTargetDirectory}/HomeSeerUtil.dll").exists()
    }

    void configureUpToDateWhen() {
        outputs.upToDateWhen {
            return !isThereSomethingToCleanUp()
        }
    }
}