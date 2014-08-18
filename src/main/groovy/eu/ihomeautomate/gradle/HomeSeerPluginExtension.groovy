package eu.ihomeautomate.gradle

import org.gradle.api.Project

class HomeSeerPluginExtension {
    //String homeseerSdkDownloadUrl = "http://www.homeseer.com/updates3/hslinux_zee_3_0_0_106.tar.gz"
    String homeseerSdkDownloadUrl = "http://www.homeseer.com/updates3/hs3_linux_3_0_0_94.tar.gz"
    String sdkReferencesTargetDirectory = "References"

    private Project project

    HomeSeerPluginExtension(Project project) {
        this.project = project
    }
}