package br.com.basis.jenkins.rancher.pipeline.build;

import io.rancher.base.Filters;

public class EnvironmentFilterBuild implements FilterBuild{

    private Filters filters;

    public EnvironmentFilterBuild(){
        filters = new Filters();
    }

    public EnvironmentFilterBuild withName(String name){
        filters.put("name", name);
        return this;
    }

    @Override
    public Filters getFilters() {
        return filters;
    }

    @Override
    public String toString() {
        return filters.toString();
    }
}
