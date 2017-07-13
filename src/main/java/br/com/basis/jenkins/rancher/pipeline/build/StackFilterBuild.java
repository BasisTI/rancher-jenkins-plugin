package br.com.basis.jenkins.rancher.pipeline.build;

import io.rancher.base.Filters;

public class StackFilterBuild implements FilterBuild{

    private Filters filters;

    public StackFilterBuild(){
        filters = new Filters();
    }

    public StackFilterBuild withAccountId(String accountId){
        filters.put("accountId", accountId);
        return this;
    }

    public StackFilterBuild withName(String name){
        filters.put("name", name);
        return this;
    }

    @Override
    public Filters getFilters() {
        return filters;
    }
}
