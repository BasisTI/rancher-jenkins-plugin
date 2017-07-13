package br.com.basis.jenkins.rancher.pipeline;

import groovy.lang.Binding;
import hudson.Extension;
import hudson.util.StreamTaskListener;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.jenkinsci.plugins.workflow.cps.GlobalVariable;

import javax.annotation.Nonnull;

@Extension
public class RancherDSL extends GlobalVariable {

    @Nonnull
    @Override
    public String getName() {
        return "rancher";
    }

    @Nonnull
    @Override
    public Object getValue(@Nonnull CpsScript script) throws Exception {
        Binding binding = script.getBinding();
        Object rancher;
        if (binding.hasVariable(getName())) {
            rancher = binding.getVariable(getName());
        } else {
            rancher = script.getClass().getClassLoader()
                    .loadClass("br.com.basis.jenkins.rancher.pipeline.RancherPipeline")
                    .getConstructor(CpsScript.class).newInstance(script);
            binding.setVariable(getName(), rancher);
        }
        return rancher;
    }
}