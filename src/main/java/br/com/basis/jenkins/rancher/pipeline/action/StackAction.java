package br.com.basis.jenkins.rancher.pipeline.action;

import br.com.basis.jenkins.rancher.pipeline.build.StackFilterBuild;
import io.rancher.Rancher;
import io.rancher.service.StackService;
import io.rancher.type.Stack;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.springframework.stereotype.Component;

@Component
public class StackAction extends AbstractAction<StackService, Stack> {

    public StackAction(Rancher rancher, CpsScript cpsScript) {
        super(rancher, io.rancher.service.StackService.class, cpsScript);
    }

    public Stack findOne(StackFilterBuild filterBuild) {
        log(String.format("Finding stack : [%s]", filterBuild));
        return findOne(getService().list(filterBuild.getFilters()));
    }
}
