package com.example.actions.ns.bean;

import java.util.logging.Logger;

import org.texturemedia.smarturls.ActionNames;
import org.texturemedia.smarturls.ActionName;
import org.texturemedia.smarturls.Results;
import org.texturemedia.smarturls.Result;
import org.apache.commons.configuration.Configuration;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionSupport;
import com.example.services.BeanService;
import org.jcatapult.scaffold.Bean;

/**
 * <p>
 * This class is the action that persists the Beans.
 * </p>
 *
 * @author  Scaffolder
 */
@ActionNames({
    @ActionName(name = "save"),
    @ActionName(name = "update")
})
@Results({
    @Result(name = "success", location = "index", type = "redirect-action"),
    @Result(action = "save", name = "input", location = "add.jsp"),
    @Result(action = "update", name = "input", location = "edit.jsp")
})
public class Save extends ActionSupport {
    private static final Logger logger = Logger.getLogger(Save.class.getName());
    private final BeanService beanService;
    private final Configuration configuration;
    private Bean bean;
    private Integer emailId;
    private Integer[] categoriesIds;

    @Inject
    public Save(BeanService beanService, Configuration configuration) {
        this.beanService = beanService;
        this.configuration = configuration;
    }

    public Bean getBean() {
        return bean;
    }


    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public Integer getEmailId() {
        return emailId;
    }

    public void setEmailId(Integer id) {
        this.emailId = id;
    }

    public Integer[] getCategoriesIds() {
        return categoriesIds;
    }

    public void setCategoriesIds(Integer[] ids) {
        this.categoriesIds = ids;
    }


    @Override
    public String execute() {
        beanService.persist(bean, emailId, categoriesIds);
        return SUCCESS;
    }
}