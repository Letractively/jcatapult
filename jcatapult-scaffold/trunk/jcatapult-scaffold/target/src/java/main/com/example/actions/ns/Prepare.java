package com.example.actions.ns.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.opensymphony.xwork2.Action;
import org.jcatapult.scaffold.Email;
import org.jcatapult.scaffold.Category;
import com.example.services.BeanService;

/**
 * <p>
 * This class prepares the form that adds and edits Beans.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Prepare implements Action {
    private static final Logger logger = Logger.getLogger(Prepare.class.getName());
    private final BeanService beanService;
    private List<Email> emails = new ArrayList<Email>();
    private List<Category> categories = new ArrayList<Category>();

    @Inject
    public Prepare(BeanService beanService) {
        this.beanService = beanService;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public String execute() {
        emails = beanService.getEmails();
        categories = beanService.getCategories();
        return SUCCESS;
    }
}