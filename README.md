# Change assembly version plugin

This is a jenkins plugin which changes the AssemblyFileVersion of all files named AssemblyInfo.cs (or other inserted) under the workspace folder.

This jenkins plugin is a spin-off of the jenkins' [change-assembly-version-plugin](https://wiki.jenkins-ci.org/display/JENKINS/Change+Assembly+Version).
It was simplified and adjusted to custom needs.
There is only one configuration field called FileName
which allows comma-separated file names to look for (for example: `AssemblyInfo.cs,BuildInfo.cs`)

The algorithm:
* Search the workspace for files specified in the configuration (defaults to AssemblyInfo.cs)
* For each matching file:
    * Find the AssemblyVersion attribute and parse the version
    * Update the revision number to `$(BUILD_NUMBER) % 2^16`
    * Update the build number to `$(BUILD_NUMBER) / 2^16`
    * Append/Replace AssemblyFileVersion attribute to the same file filling it with changed version.
