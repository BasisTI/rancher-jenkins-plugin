package br.com.basis.jenkins.rancher.commons.descriptors;

import br.com.basis.jenkins.rancher.commons.RancherPlugin;
import br.com.basis.jenkins.rancher.commons.config.RancherHostConfiguration;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.CopyOnWriteList;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.util.List;

public class RancherPluginDescriptor extends BuildStepDescriptor<Publisher> {

    public RancherPluginDescriptor() {
        super(RancherPlugin.class);
        load();
    }

    private final CopyOnWriteList<RancherHostConfiguration> hostConfigurations = new CopyOnWriteList<>();

    public List<RancherHostConfiguration> getHostConfigurations() {
        return hostConfigurations.getView();
    }

    /**
     * Add a Host Configuration to the list of configurations.
     *
     * @param configuration Host Configuration to add. The common configuration will be automatically set.
     */
    public void addHostConfiguration(final RancherHostConfiguration configuration) {
        hostConfigurations.add(configuration);
    }

    /**
     * Removes the given named Host Configuration from the list of configurations.
     *
     * @param name The Name of the Host Configuration to remove.
     */
    public void removeHostConfiguration(final String name) {
        RancherHostConfiguration configuration = getConfiguration(name);
        if (configuration != null) {
            hostConfigurations.remove(configuration);
        }
    }

    public boolean configure(final StaplerRequest request, final JSONObject formData) {
        final List<RancherHostConfiguration> newConfigurations = request.bindJSONToList(RancherHostConfiguration.class,
                formData.get("instance"));
        hostConfigurations.replaceBy(newConfigurations);
        save();
        return true;
    }

    public RancherHostConfiguration getConfiguration(String name) {
        for (RancherHostConfiguration configuration : hostConfigurations) {
            if (configuration.getName().equals(name)) {
                return configuration;
            }
        }
        return null;
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "Rancher Plugin";
    }

    public RancherHostConfigurationDescriptor getRancherHostConfigurationDescriptor() {
        final Jenkins instance = Jenkins.getInstance();
        if (instance != null) {
            return instance.getDescriptorByType(RancherHostConfiguration.Descriptor.class);
        }
        return null;
    }
}
