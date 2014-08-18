package eu.ihomeautomate.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskAction

class DownloadSdkTask extends DefaultTask {

    String homeseerSdkDownloadUrl
    File explodedSdkDirectory

    @TaskAction
    void start() {
        assert getHomeseerSdkDownloadUrl() != null, 'homeseerSdkDownloadUrl must not be null'
        assert getExplodedSdkDirectory() != null, 'explodedSdkDirectory must not be null'

        if (!getExplodedSdkDirectory().exists()) {
            boolean success = getExplodedSdkDirectory().mkdirs()

            if (!success) {
                throw new GradleException("Could not create exploded HomeSeer SDK directory in ${getExplodedSdkDirectory().canonicalPath}")
            }
        }

        def filename = getFilenameFromDownloadUrl()
        def downloadTargetFilePath = "${getExplodedSdkDirectory()}/${filename}"

        if (!new File(downloadTargetFilePath).exists()) {
            println "Downloading '${getHomeseerSdkDownloadUrl()}' to '${downloadTargetFilePath}'"
            ant.get(src: getHomeseerSdkDownloadUrl(), dest: downloadTargetFilePath, verbose: true, usetimestamp: true)
        }

        try {
            project.copy {
                from project.tarTree(downloadTargetFilePath)
                into getSdkHomePath()
            }

            // Cleanup
            if (new File(downloadTargetFilePath).exists()) {
                project.delete(downloadTargetFilePath)
            }

            // Check for unnecessary directory structure(s).
            def sdkHomeDir = new File(getSdkHomePath())
            if (sdkHomeDir.listFiles().size() == 1 && sdkHomeDir.listFiles()[0].isDirectory()) {
                // promoting single directory to our new sdkHome.
                sdkHomeDir.listFiles()[0].renameTo("${getSdkHomePath()}.tmp")
                sdkHomeDir.delete()
                new File("${getSdkHomePath()}.tmp").renameTo(getSdkHomePath())
            }

        } catch (InvalidUserDataException zip) {
            if (zip.getMessage().startsWith("Cannot expand TAR")) {
                //println "HomeSeer can't seem to gzip properly? :-(. Let's try untar."

                def newTarTarget = "${getExplodedSdkDirectory()}/${filename}.tar"
                new File(downloadTargetFilePath).renameTo(newTarTarget)
                project.copy {
                    from project.tarTree(newTarTarget)
                    into getSdkHomePath()
                }

                if (new File(newTarTarget).exists()) {
                    project.delete(newTarTarget)
                }
            }
        }
    }

    String getBaseName(String filename) {
        filename.tokenize(".")[0]
    }

    String getFilenameFromDownloadUrl() {
        getHomeseerSdkDownloadUrl().tokenize("/")[-1]
    }

    String getSdkHomeDirectoryName() {
        String filename = getFilenameFromDownloadUrl()
        return getBaseName(filename)
    }

    String getSdkHomePath() {
        return "${getExplodedSdkDirectory().canonicalPath}/${getSdkHomeDirectoryName()}"
    }

    void configureUpToDateWhen() {
        outputs.upToDateWhen {
            def explodedSdkFolder = "${getExplodedSdkDirectory()}/${getSdkHomeDirectoryName()}"
            def cacheFolder = new File(explodedSdkFolder)

            return cacheFolder.exists() && cacheFolder.isDirectory() && (cacheFolder.list().length > 0)
        }
    }
}