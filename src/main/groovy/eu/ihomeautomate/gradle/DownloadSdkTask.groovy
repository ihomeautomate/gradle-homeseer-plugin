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
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskAction

class DownloadSdkTask extends DefaultTask {

    String homeseerSdkDownloadUrl
    File explodedSdkDirectory

    @TaskAction
    void start() {
        assert getHomeseerSdkDownloadUrl() != null, 'homeseerSdkDownloadUrl must not be null'
        assert getExplodedSdkDirectory() != null, 'explodedSdkDirectory must not be null'

        if (isUpToDate()) {
            println "${getSdkHomePath()} available."
            return
        }

        if (!getExplodedSdkDirectory().exists()) {
            boolean success = getExplodedSdkDirectory().mkdirs()

            if (!success) {
                throw new GradleException("Could not create exploded HomeSeer SDK directory in ${getExplodedSdkDirectory().canonicalPath}")
            }
        }

        def filename = getFilenameFromDownloadUrl()
        def downloadTargetFilePath = "${getExplodedSdkDirectory()}/${filename}"
        def downloadTargetFile = new File(downloadTargetFilePath)

        if (!downloadTargetFile.exists()) {
            println "Downloading '${getHomeseerSdkDownloadUrl()}' to '${downloadTargetFile.canonicalPath}'"
            ant.get(src: getHomeseerSdkDownloadUrl(), dest: downloadTargetFilePath, verbose: true, usetimestamp: true)
        }

        try {
            project.copy {
                from project.tarTree(downloadTargetFilePath)
                into getSdkHomePath()
            }

            // Cleanup
            if (downloadTargetFile.exists()) {
                if (!downloadTargetFile.delete()) {
                    logger.warn("Oops, failed to delete downloaded file '${downloadTargetFile.canonicalPath}'")
                }
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
                logger.info("First try expanding TAR failed, maybe file extension is incorrect. Let's try renaming to .tar and re-try.")

                // Let's try to copy, we've experienced weird locks when renaming file @ windows :-(.
                project.copy {
                    from "${getExplodedSdkDirectory()}"
                    into "${getExplodedSdkDirectory()}"
                    include "${filename}"
                    rename "${filename}", "${filename}.tar"
                }

                def newTarTarget = "${downloadTargetFilePath}.tar"
                def newTarTargetFile = new File(newTarTarget)
                if (newTarTargetFile.exists()) {
                    logger.debug("Rename to ${newTarTarget} succeeded. Ready for second untar try.")
                    project.copy {
                        from project.tarTree(newTarTarget)
                        into getSdkHomePath()
                    }

                    logger.debug("Deleting downloaded file '${newTarTargetFile.canonicalPath}'")
                    if (newTarTargetFile.delete()) {
                        logger.debug("Deleted downloaded file '${newTarTargetFile.canonicalPath}'")
                    } else {
                        logger.warn("Failed to remove downloaded file '${newTarTargetFile.canonicalPath}'")
                    }

                    if (downloadTargetFile.exists()) {
                        logger.debug("Deleting downloaded file '${downloadTargetFile.canonicalPath}'")
                        if (downloadTargetFile.delete()) {
                            logger.debug("Deleted downloaded file '${downloadTargetFile.canonicalPath}'")
                        } else {
                            logger.warn("Failed to remove downloaded file '${downloadTargetFile.canonicalPath}'")
                        }
                    }

                } else {
                    logger.warn("Rename to ${newTarTarget} failed :-(.")

                    if (downloadTargetFile.exists()) {
                        logger.debug("Deleting downloaded file ${downloadTargetFile.canonicalPath}")
                        if (downloadTargetFile.delete()) {
                            logger.debug("Deleted downloaded file ${downloadTargetFile.canonicalPath}")
                        } else {
                            logger.warn("Failed to delete downloaded file '${downloadTargetFile.canonicalPath}'")
                        }
                    }

                    throw zip;
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

    boolean isUpToDate() {
        def explodedSdkFolder = "${getExplodedSdkDirectory()}/${getSdkHomeDirectoryName()}"
        def cacheFolder = new File(explodedSdkFolder)
        return cacheFolder.exists() && cacheFolder.isDirectory() && (cacheFolder.list().length > 0)
    }

    void configureUpToDateWhen() {
        outputs.upToDateWhen {
            return isUpToDate()
        }
    }
}