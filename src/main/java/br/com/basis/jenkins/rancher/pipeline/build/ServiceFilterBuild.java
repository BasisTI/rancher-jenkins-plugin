package br.com.basis.jenkins.rancher.pipeline.build;

import io.rancher.base.Filters;

public class ServiceFilterBuild implements FilterBuild{

    private Filters filters;

    public ServiceFilterBuild(){
        filters = new Filters();
    }

    public ServiceFilterBuild withStackId(String stackId){
        filters.put("stackId", stackId);
        return this;
    }

    public ServiceFilterBuild withName(String name){
        filters.put("name", name);
        return this;
    }

    @Override
    public Filters getFilters() {
        return filters;
    }
}
