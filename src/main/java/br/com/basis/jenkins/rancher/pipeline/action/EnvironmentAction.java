package br.com.basis.jenkins.rancher.pipeline.action;

import br.com.basis.jenkins.rancher.pipeline.build.EnvironmentFilterBuild;
import io.rancher.Rancher;
import io.rancher.service.ProjectService;
import io.rancher.type.Project;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentAction extends AbstractAction<ProjectService, Project> {

    public EnvironmentAction(Rancher rancher, CpsScript cpsScript) {
        super(rancher, ProjectService.class, cpsScript);
    }

    public Project findOne(EnvironmentFilterBuild filterBuild) {
        log(String.format("Finding environment : [%s]", filterBuild));
        return findOne(getService().list(filterBuild.getFilters()));
    }

}
