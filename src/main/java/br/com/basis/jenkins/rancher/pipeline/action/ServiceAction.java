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

@Component
public class ServiceAction extends AbstractAction<ServiceService, Service> {

    private EnvironmentAction environmentBusiness;
    private StackAction stackBusiness;

    public ServiceAction(Rancher rancher, CpsScript script) {
        super(rancher, ServiceService.class, script);
        this.environmentBusiness = new EnvironmentAction(rancher, script);
        this.stackBusiness = new StackAction(rancher, script);
    }

    private Service findOne(ServiceFilterBuild filterBuild) {
        return findOne(getService().list(filterBuild.getFilters()));
    }

    public void upgrade(String serviceID) {
        Service service = findById(serviceID);
        finishPreviousUpgrade(service);
        ServiceUpgrade serviceUpgrade = createServiceUpgrade(service);
        log("Starting upgrade of service "+service.getName());
        Response<Service> response = execute(getService().upgrade(service.getId(), serviceUpgrade));
        finishUpgrade(response.body(), 0);
    }

    public void upgrade(String environmentName, String stackName, String serviceName) {
        Project project = environmentBusiness.findOne(new EnvironmentFilterBuild().withName(environmentName));
        Stack stack = stackBusiness.findOne(new StackFilterBuild().withAccountId(project.getId()).withName(stackName));
        Service service = findOne(new ServiceFilterBuild().withStackId(stack.getId()).withName(serviceName));
        upgrade(service.getId());
    }

    private void finishPreviousUpgrade(Service service) {
        if (StatusRancher.isEqual(StatusRancher.UPGRADED, service.getState())) {
            log(String.format("Finishing previous upgrade of service %s",service.getName()));
            finishUpgrade(service,0);
        }
    }

    private void finishUpgrade(Service service, int timeout) {
        log(String.format("Waiting for the upgrade to finish %s - %s",service.getName(), service.getState()));
        if(StatusRancher.isEqual(StatusRancher.ACTIVE, service.getState())){
            return;
        }
        if (timeout > 10) {
            throw new RancherRuntimeException(String.format("Timeout - Service could not be upgraded. Current status: %s",service.getState()));
        }
        if (StatusRancher.isEqual(StatusRancher.UPGRADED, service.getState())) {
            Response<Service> execute = execute(getService().finishupgrade(service.getId()));
            if(!execute.isSuccessful()){
                throw new RancherRuntimeException("Service could not be upgraded.");
            }
        }
        sleep(4000);
        finishUpgrade(findById(service.getId()), ++timeout);
    }

    private Service findById(String id) {
        Service service = execute(getService().get(id)).body();
        if(service == null){
            throw new RancherRuntimeException(String.format("Service id %s not found!", id));
        }
        return service;
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log(String.format("Ops... %s",e.toString()));
        }
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
}
