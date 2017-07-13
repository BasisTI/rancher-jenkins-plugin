package br.com.basis.jenkins.rancher.pipeline.action;

import br.com.basis.jenkins.rancher.exception.RancherRuntimeException;
import io.rancher.Rancher;
import io.rancher.base.AbstractType;
import io.rancher.base.TypeCollection;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.springframework.util.CollectionUtils;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public abstract class AbstractAction<E, I extends AbstractType> implements Action {

    private Rancher rancher;
    private CpsScript cpsScript;
    private E service;

    public AbstractAction(Rancher rancher, Class<E> clazz, CpsScript cpsScript) {
        this.rancher = rancher;
        this.cpsScript = cpsScript;
        this.service = rancher.type(clazz);
    }

    protected void log(String msg) {
        if (cpsScript != null) {
            cpsScript.println(msg);
        }
    }

    protected I findOne(Call<TypeCollection<I>> call) {
        TypeCollection<I> execute = execute(call).body();
        if (CollectionUtils.isEmpty(execute.getData()) || execute.getData().size() > 1) {
            throw new RancherRuntimeException(this.service.getClass().getName()+ ": Verify search parameters. ");
        }
        return execute.getData().get(0);
    }

    protected E getService() {
        return service;
    }

    protected <T> Response<T> execute(Call<T> call) {
        try {
            return call.execute();
        } catch (IOException e) {
            throw new RancherRuntimeException("Ops... ", e);
        }
    }
}
