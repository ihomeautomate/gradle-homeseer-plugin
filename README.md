# Gradle HomeSeer plugin ![HomeSeer Logo](https://lh5.googleusercontent.com/-ouDt6liIFbo/AAAAAAAAAAI/AAAAAAAAAgA/f-7s9hTpPzw/photo.jpg?sz=20)

[![Build Status](https://travis-ci.org/ihomeautomate/gradle-homeseer-plugin.png?branch=master)](https://travis-ci.org/ihomeautomate/gradle-homeseer-plugin)
[![Maintainer Status](http://stillmaintained.com/ihomeautomate/gradle-homeseer-plugin.png)](http://stillmaintained.com/ihomeautomate/gradle-homeseer-plugin)
[![Download](https://api.bintray.com/packages/ihomeautomate/Gradle-Plugins/gradle-homeseer-plugin/images/download.svg) ](https://bintray.com/ihomeautomate/Gradle-Plugins/gradle-homeseer-plugin/_latestVersion)

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
            classpath 'eu.ihomeautomate.gradle:gradle-homeseer-plugin:0.4.1'
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
            classpath 'eu.ihomeautomate.gradle:gradle-homeseer-plugin:0.5.0-SNAPSHOT'
        }
    }

The plugin allows you to customize the HomeSeer SDK to download and use. By default it takes `hs3_linux_3_0_0_171` (current beta version).
Specify the target directory where the necessary HomeSeer SDK binary references are copied to by using `sdkReferencesTargetDirectory`, relative to the project directory.

    homeseer {
        // To use hslinux_zee binaries use:
        homeseerSdkDownloadUrl 'http://www.homeseer.com/updates3/hslinux_zee_3_0_0_171.tar.gz'
        // To use hs3_linux binaries use :
        //homeseerSdkDownloadUrl = 'http://www.homeseer.com/updates3/hslinux_hs3_3_0_0_171.tar.gz'
        // ... or don't specify homeseerSdkDownloadUrl at all (default)
                    
        // Target directory where the plugin copies Scheduler.dll, HSCF.dll, HomeSeerAPI.dll
        sdkReferencesTargetDirectory = 'MyReferences'
        // ... if none defined, the default 'References' is used.
    }

## Tasks

The App Engine plugin defines the following tasks:

* `homeseerDownloadSdk`: Downloads the HomeSeer SDK defined by the configuration `homeseerSdkDownloadUrl` and explodes the artifact into `~/.gradle/homeseer-sdk`.
* `homeseerCopySdkReferences`: Copies the necessary HomeSeer SDK references to the target directory specified by the configuration `sdkReferencesTargetDirectory`.
* `homeseerCleanSdkReferences`: Cleans HomeSeer SDK references from the target directory specified by the configuration `sdkReferencesTargetDirectory`.

## Convention properties

The HomeSeer plugin defines the following convention properties in the `homeseer` closure:

* `homeseerSdkDownloadUrl`: HomeSeer SDK URL to download and explode. Default is `http://www.homeseer.com/updates3/hslinux_hs3_3_0_0_171.tar.gz`.
* `sdkReferencesTargetDirectory`: Directory where the necessary SDK references will be copied to (defaults to `References`).

## Integration with other Gradle plugins
* [xamarin-build-plugin](https://bintray.com/ihomeautomate/Gradle-Plugins/xamarin-gradle-plugins/view/read) is automatically applied. You can easily run tasks `buildDebug`, `buildRelease` or `buildAll`.
  See [xamarin-build-plugin](https://github.com/ihomeautomate/xamarin-gradle-plugins) for detailed information on how to use the build tasks. The `build*` tasks are configured to depend on `homeseerCopySdkReferences` so you can have your HomeSeer-plugin-project easily compiled.
  The `clean` task is automatically configured to depend on `homeseerCleanSdkReferences` for your convenience.
  
## xamarin-build-plugin (Windows)
The `build*` tasks should also work on Windows. Be sure to customize the `xbuildPath`. See [xamarin-build-plugin](https://github.com/ihomeautomate/xamarin-gradle-plugins) for detailed information.
  
## Copyright and licensing
    
Code and documentation copyright 2014-2015 [iHomeAutomate](http://www.iHomeAutomate.com). Code released under the [Apache-2.0 license](LICENSE.txt).
    
## Social media

<!-- Please don't remove this: Grab your social icons from https://github.com/carlsednaoui/gitsocial -->

[![@ihomeautomate][1.1]][1]
[![iHomeAutomate @ Facebook][2.1]][2]
[![+IhomeautomateEu][3.1]][3]
[![github.com/ihomeautomate][6.1]][6]

<!-- links to social media icons -->
<!-- no need to change these -->

<!-- icons with padding -->

[1.1]: http://i.imgur.com/tXSoThF.png (@ihomeautomate)
[2.1]: http://i.imgur.com/P3YfQoD.png (iHomeAutomate facebook page)
[3.1]: http://i.imgur.com/yCsTjba.png (+iHomeAutomateEu)
[4.1]: http://i.imgur.com/YckIOms.png (tumblr icon with padding)
[5.1]: http://i.imgur.com/1AGmwO3.png (dribbble icon with padding)
[6.1]: http://i.imgur.com/0o48UoR.png (github.com/ihomeautomate)

<!-- icons without padding -->

[1.2]: http://i.imgur.com/wWzX9uB.png (twitter icon without padding)
[2.2]: http://i.imgur.com/fep1WsG.png (facebook icon without padding)
[3.2]: http://i.imgur.com/VlgBKQ9.png (google plus icon without padding)
[4.2]: http://i.imgur.com/jDRp47c.png (tumblr icon without padding)
[5.2]: http://i.imgur.com/Vvy3Kru.png (dribbble icon without padding)
[6.2]: http://i.imgur.com/9I6NRUm.png (github icon without padding)


<!-- links to your social media accounts -->
<!-- update these accordingly -->

[1]: http://twitter.com/ihomeautomate
[2]: https://facebook.com/pages/iHomeAutomate/218034961586842
[3]: https://plus.google.com/+IhomeautomateEu
[6]: http://github.com/ihomeautomate
[7]: https://github.com/pledbrook/lazybones/wiki/Template-developers-guide

<!-- Please don't remove this: Grab your social icons from https://github.com/carlsednaoui/gitsocial --> 