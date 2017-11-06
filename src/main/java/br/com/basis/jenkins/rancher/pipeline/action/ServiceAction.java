package br.com.basis.jenkins.rancher.pipeline.action;

import br.com.basis.jenkins.rancher.exception.RancherRuntimeException;
import br.com.basis.jenkins.rancher.pipeline.build.EnvironmentFilterBuild;
import br.com.basis.jenkins.rancher.pipeline.build.ServiceFilterBuild;
import br.com.basis.jenkins.rancher.pipeline.build.StackFilterBuild;
import io.rancher.Rancher;
import io.rancher.service.ServiceService;
import io.rancher.type.*;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.io.IOException;

@Component
public class ServiceAction extends AbstractAction<ServiceService, Service> {

    private EnvironmentAction environmentBusiness;
    private StackAction stackBusiness;
    private int timeout = 4000;

    public ServiceAction(Rancher rancher, CpsScript script) {
        super(rancher, ServiceService.class, script);
        this.environmentBusiness = new EnvironmentAction(rancher, script);
        this.stackBusiness = new StackAction(rancher, script);
    }

    private Service findOne(ServiceFilterBuild filterBuild) {
        log(String.format("Finding service : [%s]", filterBuild));
        return findOne(getService().list(filterBuild.getFilters()));
    }

    public void upgrade(String serviceID) {
        Service service = findById(serviceID);
        finishPreviousUpgrade(service);
        ServiceUpgrade serviceUpgrade = createServiceUpgrade(service);
        log(String.format("Starting upgrade of service [%s]", service.getName()));
        Response<Service> response = execute(getService().upgrade(service.getId(), serviceUpgrade));
        log(String.format("Rancher response upgrade service [%s] [%s]", service.getName(), response));
        logErrorResponse(service, response);
        finishUpgrade(response.body(), 0);
    }

    private void logErrorResponse(Service service, Response<Service> response) {
        if(response.errorBody() != null){
            try {
                log(String.format("Rancher response upgrade service [%s] [%s]", service.getName(), response.errorBody().string()));
            } catch (IOException e) {}
        }
    }

    public void upgrade(String environmentName, String stackName, String serviceName) {
        Project project = environmentBusiness.findOne(new EnvironmentFilterBuild().withName(environmentName));
        Stack stack = stackBusiness.findOne(new StackFilterBuild().withAccountId(project.getId()).withName(stackName));
        Service service = findOne(new ServiceFilterBuild().withStackId(stack.getId()).withName(serviceName));
        upgrade(service.getId());
    }

    public void upgrade(String environmentName, String stackName, String serviceName, int timeout) {
        setTimeout(timeout);
        upgrade(environmentName, stackName, serviceName);
    }

    private void finishPreviousUpgrade(Service service) {
        if (StatusRancher.isEqual(StatusRancher.UPGRADED, service.getState())) {
            log(String.format("Finishing previous upgrade of service [%s]", service.getName()));
            finishUpgrade(service, 0);
        }
    }

    private void finishUpgrade(Service service, int timeoutCount) {
        log(String.format("Waiting for the upgrade to finish [%s] - [%s]", service.getName(), service.getState()));
        if (StatusRancher.isEqual(StatusRancher.ACTIVE, service.getState())) {
            return;
        }
        if (timeoutCount > 10) {
            throw new RancherRuntimeException(String.format("Timeout - Service could not be upgraded. Current status: [%s]", service.getState()));
        }
        if (StatusRancher.isEqual(StatusRancher.UPGRADED, service.getState())) {
            Response<Service> execute = execute(getService().finishupgrade(service.getId()));
            if (!execute.isSuccessful()) {
                throw new RancherRuntimeException("Service could not be upgraded.");
            }
        }
        sleep(this.timeout);
        finishUpgrade(findById(service.getId()), ++timeoutCount);
    }

    private Service findById(String id) {
        log(String.format("Finding service by [%s]", id));
        Service service = execute(getService().get(id)).body();
        if (service == null) {
            throw new RancherRuntimeException(String.format("Service [%s] not found!", id));
        }
        log(String.format("Found [%s] [%s]", service.getId(), service.getName()));
        return service;
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {}
    }

    private ServiceUpgrade createServiceUpgrade(Service service) {
        ServiceUpgrade serviceUpgrade = new ServiceUpgrade();
        InServiceUpgradeStrategy inServiceUpgradeStrategy = new InServiceUpgradeStrategy();
        inServiceUpgradeStrategy.setStartFirst(true);
        inServiceUpgradeStrategy.setBatchSize(1);
        inServiceUpgradeStrategy.setIntervalMillis(2000);
        inServiceUpgradeStrategy.setLaunchConfig(service.getLaunchConfig());
        inServiceUpgradeStrategy.setSecondaryLaunchConfigs(service.getSecondaryLaunchConfigs());
        serviceUpgrade.setInServiceStrategy(inServiceUpgradeStrategy);
        return serviceUpgrade;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout/10;
    }
}
