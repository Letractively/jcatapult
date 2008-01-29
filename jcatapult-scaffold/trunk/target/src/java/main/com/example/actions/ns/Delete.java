package com.example.actions.ns.bean;

import org.texturemedia.smarturls.Result;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionSupport;
import com.example.services.BeanService;

/**
 * <p>
 * This class is the action that deletes one or more Bean(s)
 * </p>
 *
 * @author Scaffolder
 */
@Result(name = "success", location = "index", type = "redirect-action")
public class Delete extends ActionSupport {
    private final BeanService beanService;
    private int[] ids;

    @Inject
    public Delete(BeanService beanService) {
        this.beanService = beanService;
    }

    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    @Override
    public String execute() {
        if (ids != null && ids.length > 0) {
            beanService.deleteMany(ids);
        }

        return SUCCESS;
    }
}