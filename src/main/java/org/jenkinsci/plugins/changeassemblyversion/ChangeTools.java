package org.jenkinsci.plugins.changeassemblyversion;

import hudson.FilePath;
import hudson.model.BuildListener;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeTools {

    private static final String ASSEMBLY_VERSION_REGEX = "\\[\\s*assembly\\s*:\\s*AssemblyVersion\\s*\\(\\s*\"(.+?)\"\\s*\\)\\s*\\]";
    private static final String ASSEMBLY_FILE_VERSION_REGEX = "\\[\\s*assembly\\s*:\\s*AssemblyFileVersion\\s*\\(\\s*\"(.+?)\"\\s*\\)\\s*\\]";

	/**
	 * Call this method passing the filepath list from the build machine, version to set and listener to log.
	 * @param listener
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void fixVersion(FilePath workspaceDir, Collection<FilePath> fileList, int buildNumber, BuildListener listener) throws IOException, InterruptedException {
        assert fileList != null;
        int shortLimit = 2 << 16;
        int build = buildNumber / shortLimit;
        int revision = buildNumber % shortLimit;

		if(fileList.size() > 0){
            int offset = workspaceDir.getRemote().length() + 1;
			for(FilePath file : fileList){
                listener.getLogger().println(String.format("> Updating file: %s", file.getRemote().substring(offset)));
				String content = file.readToString();
				content = this.modifyAssemblyFileContent(content, build, revision, listener);
				file.write(content, "UTF-8");
			}
		}									
	}

    private String modifyAssemblyFileContent(String content, int build, int revision, BuildListener listener) {

        listener.getLogger().println("  Searching for AssemblyVersion attribute...");
        Pattern versionPattern = Pattern.compile(ASSEMBLY_VERSION_REGEX);
        Matcher versionMatcher = versionPattern.matcher(content);
        if (versionMatcher.find()) {
            String currentVersion = versionMatcher.group(1);
            String newVersion = this.modifyVersion(currentVersion, build, revision);
            listener.getLogger().println(String.format("  Found AssemblyVersion attribute. New version: %s", newVersion));

            Pattern fileVersionPattern = Pattern.compile(ASSEMBLY_FILE_VERSION_REGEX);
            Matcher fileVersionMatcher = fileVersionPattern.matcher(content);
            if (fileVersionMatcher.find()) {
                listener.getLogger().println("  Replacing AssemblyFileVersion attribute...");
                String replacement = "[assembly: AssemblyFileVersion(\"" + newVersion + "\")]";
                return fileVersionMatcher.replaceAll(replacement);
            } else {
                StringBuffer sb = new StringBuffer(content);
                listener.getLogger().println(String.format("  Appending AssemblyFileVersion attribute..."));
                sb.append("\r\n[assembly: AssemblyFileVersion(\"");
                sb.append(newVersion);
                sb.append("\")]\r\n");
                return sb.toString();
            }
        } else {
            listener.getLogger().println("  AssemblyVersion attribute not found. Nothing to change.");
        }

        return content;
    }

    private String modifyVersion(String currentVersion, int build, int revision) {
        AssemblyVersion ver = new AssemblyVersion(currentVersion);
        ver.setBuild(build);
        ver.setRevision(revision);
        return ver.toString(4);
    }
}
