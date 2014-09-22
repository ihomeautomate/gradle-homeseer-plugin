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

import org.gradle.api.Project

class HomeSeerPluginExtension {
    String homeseerSdkDownloadUrl = "ftp://ftp.homeseer.com/updates/Beta/hs3_linux_3_0_0_128.tar.gz"
    String sdkReferencesTargetDirectory = "References"

    private Project project

    HomeSeerPluginExtension(Project project) {
        this.project = project
    }
}