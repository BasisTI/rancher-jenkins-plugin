package br.com.basis.jenkins.rancher.commons;

import br.com.basis.jenkins.rancher.commons.descriptors.RancherPluginDescriptor;
import hudson.Extension;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import jenkins.model.Jenkins;

public class RancherPlugin extends Notifier {

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public Descriptor getDescriptor() {
        final Jenkins instance = Jenkins.getInstance();
        if (instance != null) {
            return instance.getDescriptorByType(Descriptor.class);
        }
        return null;
    }

    @Extension
    public static class Descriptor extends RancherPluginDescriptor {
        public Descriptor() {
            super();
        }
    }
}
