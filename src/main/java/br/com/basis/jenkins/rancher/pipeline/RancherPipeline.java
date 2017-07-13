package br.com.basis.jenkins.rancher.pipeline;

import br.com.basis.jenkins.rancher.commons.RancherPlugin;
import br.com.basis.jenkins.rancher.commons.config.RancherHostConfiguration;
import br.com.basis.jenkins.rancher.pipeline.action.ServiceAction;
import io.rancher.Rancher;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.cps.CpsScript;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class RancherPipeline implements Serializable {

    private Rancher rancher;
    private CpsScript cpsScript;

    public RancherPipeline(CpsScript cpsScript) {
        this.cpsScript = cpsScript;
    }

    RancherPipeline config(String name) throws MalformedURLException {
        final RancherHostConfiguration configuration = Jenkins.getInstance()
                .getDescriptorByType(RancherPlugin.Descriptor.class)
                .getConfiguration(name);
        this.rancher = new Rancher(new Rancher.Config(new URL(configuration.getRancherUrl()), configuration.getAccessKey(), configuration.getPrivateKey()));
        return this;
    }

    ServiceAction service(){
        return new ServiceAction(this.rancher, this.cpsScript);
    }
}