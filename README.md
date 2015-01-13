# Change assembly version plugin

This jenkins plugin is a spin-off of the [change-assembly-version-plugin](https://github.com/jenkinsci/change-assembly-version-plugin).

The algorithm:
* Search the workspace for files specified in the configuration (defaults to AssemblyInfo.cs)
* For each matching file:
** Find the AssemblyVersion attribute and parse the version
** Update the revision number to JENKINS_BUILD_NUMBER % 2^16
** Update the build number to JENKINS_BUILD_NUMBER / 2^16
** Append/Replace AssemblyFileVersion attribute to the same file filling it with changed version.
