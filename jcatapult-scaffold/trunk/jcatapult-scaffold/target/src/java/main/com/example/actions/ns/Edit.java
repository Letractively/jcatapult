package com.example.actions.ns.bean;

import java.util.ArrayList;
import java.util.List;

import com.texturemedia.catapult.domain.CatapultBean;
import org.texturemedia.smarturls.Result;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionSupport;
import org.jcatapult.scaffold.Bean;
import org.jcatapult.scaffold.Email;
import org.jcatapult.scaffold.Category;
import com.example.services.BeanService;

/**
 * <p>
 * This class fetches an existing Bean for editing.
 * </p>
 *
 * @author  Scaffolder
 */
@Result(name = "error", location = "index.jsp")
public class Edit extends ActionSupport {
    private final BeanService beanService;
    private Integer id;
    private Bean bean;
    private Integer emailId;
    private Integer[] categoriesIds;

    @Inject
    public Edit(BeanService beanService) {
        this.beanService = beanService;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Bean getBean() {
        return bean;
    }

    public Integer getEmailId() {
        return emailId;
    }

    public Integer[] getCategoriesIds() {
        return categoriesIds;
    }


    @Override
    public String execute() {
        bean = beanService.getById(id);
        if (bean == null) {
            addActionError("That Bean has been deleted.");
            return ERROR;
        }

        emailId = bean.getEmail().getId();

        List<Integer> categoriesIds = new ArrayList<Integer>();
        for (CatapultBean bean : bean.getCategories()) {
            categoriesIds.add(bean.getId());
        }
        this.categoriesIds = categoriesIds.toArray(new Integer[categoriesIds.size()]);

        return SUCCESS;
    }
}