# Gradle HomeSeer plugin ![HomeSeer Logo](https://lh5.googleusercontent.com/-ouDt6liIFbo/AAAAAAAAAAI/AAAAAAAAAgA/f-7s9hTpPzw/photo.jpg?sz=20)

The plugin provides tasks for downloading [HomeSeer SDK](http://homeseer.com/support/homeseer/HS3/SDK/default.htm) versions and managing HomeSeer (plugin) projects.

## Usage

To use the HomeSeer plugin, include in your build script:

    apply plugin: 'eu.ihomeautomate.gradle.homeseer'

The plugin JAR needs to be defined in the classpath of your build script. It is directly available on [BinTray](https://bintray.com/ihomeautomate/Gradle-Plugins/gradle-homeseer-plugin/view).
Alternatively, you can download it from GitHub and deploy it to your local repository. The following code snippet shows an
example on how to retrieve it from jcenter():

    buildscript {
        repositories {
            jcenter()
        }

        dependencies {
            classpath 'eu.ihomeautomate.gradle:gradle-homeseer-plugin:0.2.0'
        }
    }

In case you want to use SNAPSHOT builds add the [JFrog OSS snapshot repository](https://oss.jfrog.org/oss-snapshot-local/) to your repositories:
    
    buildscript {
        repositories {
            maven {
                    name 'JFrog OSS snapshot repository'
                    url  'https://oss.jfrog.org/oss-snapshot-local/'
            }
            jcenter()
        }

        dependencies {
            classpath 'eu.ihomeautomate.gradle:gradle-homeseer-plugin:0.3.0-SNAPSHOT'
        }
    }

The plugin allows you to customize the HomeSeer SDK to download and use. By default it takes `hs3_linux_3_0_0_94` (current version).
Specify the target directory where the necessary HomeSeer SDK binary references are copied to by using `sdkReferencesTargetDirectory`, relative to the project directory.

    homeseer {
        // To use hslinux_zee binaries use:
        homeseerSdkDownloadUrl 'http://www.homeseer.com/updates3/hslinux_zee_3_0_0_106.tar.gz'
        // To use hs3_linux binaries use :
        //homeseerSdkDownloadUrl = 'ftp://homeseer.com/pub/hs3_linux_3_0_0_94.tar.gz'
        // ... or don't specify homeseerSdkDownloadUrl at all (default)
                    
        // Target directory where the plugin copies Scheduler.dll, HSCF.dll, HomeSeerAPI.dll and HomeSeerUtil.dll
        sdkReferencesTargetDirectory = 'MyReferences'
        // ... if none defined, the default 'References' is used.
    }

## Tasks

The App Engine plugin defines the following tasks:

* `homeseerDownloadSdk`: Downloads the HomeSeer SDK defined by the configuration `homeseerSdkDownloadUrl` and explodes the artifact into `~/.gradle/homeseer-sdk`.
* `homeseerCopySdkReferences`: Copies the necessary HomeSeer SDK references to the target directory specified by the configuration `sdkReferencesTargetDirectory`.

## Convention properties

The HomeSeer plugin defines the following convention properties in the `homeseer` closure:

* `homeseerSdkDownloadUrl`: HomeSeer SDK URL to download and explode. Default is `ftp://homeseer.com/pub/hs3_linux_3_0_0_94.tar.gz`.
* `sdkReferencesTargetDirectory`: Directory where the necessary SDK references will be copied to (defaults to `References`).

## Integration with other Gradle plugins
* [xamarin-build-plugin](https://bintray.com/ihomeautomate/Gradle-Plugins/xamarin-gradle-plugins/view/read) is automatically applied. You can easily run tasks `buildDebug`, `buildRelease` or `buildAll`.
  See [xamarin-build-plugin](https://github.com/ihomeautomate/xamarin-gradle-plugins) for detailed information on how to use the build tasks.