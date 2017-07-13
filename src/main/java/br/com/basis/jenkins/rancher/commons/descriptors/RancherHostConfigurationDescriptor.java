package br.com.basis.jenkins.rancher.commons.descriptors;

import br.com.basis.jenkins.rancher.commons.config.RancherHostConfiguration;
import hudson.model.Descriptor;

public class RancherHostConfigurationDescriptor extends Descriptor<RancherHostConfiguration> {
    @Override
    public String getDisplayName() {
        return "Rancher Host Configuration";
    }
}
