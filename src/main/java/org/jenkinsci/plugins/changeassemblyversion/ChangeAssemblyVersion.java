package org.jenkinsci.plugins.changeassemblyversion;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:leonardo.kobus@hbsis.com.br">Leonardo Kobus</a>
 */
public class ChangeAssemblyVersion extends Builder {

    private static final String ENV_BUILD_NUMBER = "BUILD_NUMBER";
    private String assemblyFile;

    @DataBoundConstructor
    public ChangeAssemblyVersion(String assemblyFile) {
        this.assemblyFile = assemblyFile;
    }

    public String getAssemblyFile() {
        return this.assemblyFile;
    }

    /**
     *
     * The perform method is gonna search all the file named "Assemblyinfo.cs"
     * in any folder below, and after found will change the version of
     * AssemblyVersion and AssemblyFileVersion in the file for the inserted
     * version (task property value).
     *
     *
     * OBS: The inserted value can be some jenkins variable like ${BUILD_NUMBER}
     * just the variable alone, but not implemented to treat
     * 0.0.${BUILD_NUMBER}.0 I think this plugin must be used with Version
     * Number Plugin.
     *
     *
     */
    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        this.log(listener, "========== Changing assembly version =========");
        try {
            String fileName = this.getAssemblyFile();
            if (this.assemblyFile == null || this.assemblyFile.equals("")) {
                fileName = "AssemblyInfo.cs";
            }

            EnvVars envVars = build.getEnvironment(listener);
            FilePath workspaceDir = build.getWorkspace().child(envVars.get("WORKSPACE"));

            this.log(listener, "Listing files matching %s name(s)...", fileName);
            List<FilePath> fpList = this.getFiles(workspaceDir, fileName.split(","));
            int count = fpList.size();
            this.log(listener, "%d file(s) found.", count);
            if (count > 0) {
                int buildNumber = Integer.parseInt(envVars.expand(String.format("${%s}", ENV_BUILD_NUMBER)));
                this.log(listener, "Changing the revision number to %d", buildNumber);
                ChangeTools tools = new ChangeTools();
                tools.fixVersion(workspaceDir, fpList, buildNumber, listener);
                this.log(listener, "Done.");
            } else {
                this.log(listener, "Nothing to do.");
            }

        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            listener.getLogger().println(sw.toString());
            return false;
        }
        this.log(listener, "=============================================");
        return true;
    }

    private List<FilePath> getFiles(FilePath rootDir, String[] fileNames) throws IOException, InterruptedException{
        List<FilePath> result = new ArrayList<FilePath>();
        for (FilePath file : rootDir.list()) {
            if (!file.getName().startsWith(".")) {
                if (file.isDirectory()) {
                    result.addAll(this.getFiles(file, fileNames));
                } else{
                    for(String fileName : fileNames){
                        if(file.getRemote().trim().endsWith(fileName.trim())){
                            result.add(file);
                        }
                    }

                }
            }
        }
        return result;
    }

    @Extension
    public static class Descriptor extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Change Assembly Version";
        }
    }

    private void log(BuildListener listener, String message, Object... args) {
        listener.getLogger().println(String.format(message, args));
    }

}
