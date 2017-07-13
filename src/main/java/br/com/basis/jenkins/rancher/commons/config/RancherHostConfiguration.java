package br.com.basis.jenkins.rancher.commons.config;

import br.com.basis.jenkins.rancher.commons.descriptors.RancherHostConfigurationDescriptor;
import hudson.Extension;
import hudson.model.Describable;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

public class RancherHostConfiguration implements Describable<RancherHostConfiguration> {

    @Nonnull
    private String name;
    @Nonnull
    private String rancherUrl;
    @Nonnull
    private String accessKey;
    @Nonnull
    private String privateKey;

    @DataBoundConstructor
    public RancherHostConfiguration(@Nonnull String name, @Nonnull String rancherUrl, @Nonnull String accessKey, @Nonnull String privateKey) {
        this.name = name;
        this.rancherUrl = rancherUrl;
        this.accessKey = accessKey;
        this.privateKey = privateKey;
    }

    @Nonnull
    public String getName() {
        return name;
    }
    @Nonnull
    public String getRancherUrl() {
        return rancherUrl;
    }
    @Nonnull
    public String getAccessKey() {
        return accessKey;
    }
    @Nonnull
    public String getPrivateKey() {
        return privateKey;
    }

    @Override
    public RancherHostConfigurationDescriptor getDescriptor() {
        final Jenkins instance = Jenkins.getInstance();
        if (instance != null) {
            return instance.getDescriptorByType(Descriptor.class);
        }
        return null;
    }

    @Override
    public String toString() {
        return "RancherHostConfiguration{" +
                "name='" + name + '\'' +
                ", rancherUrl='" + rancherUrl + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                '}';
    }

    @Extension
    public static class Descriptor extends RancherHostConfigurationDescriptor {
        //
    }
}
