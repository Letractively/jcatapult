package com.example.actions.ns.bean;

import java.util.List;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionSupport;
import org.jcatapult.scaffold.Bean;
import com.example.services.BeanService;

/**
 * <p>
 * This class is an action that lists out and sorts the Beans.
 * </p>
 *
 * @author  Scaffolder
 */
public class Index extends ActionSupport {
    private final BeanService beanService;
    private List<Bean> beans;
    private String sortProperty;
    private int totalCount;
    private int numberPerPage = 20;
    private int page = 1;
    private boolean showAll = false;

    @Inject
    public Index(BeanService beanService) {
        this.beanService = beanService;
    }

    /**
     * The name of the sort property within the {@link Bean}. This must be a property name.
     *
     * @return  The sort property.
     */
    public String getSortProperty() {
        return sortProperty;
    }

    /**
     * Sets the name of the sort property within the {@link Bean}. This must be a property name.
     *
     * @param   sortProperty The sort property.
     */
    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    /**
     * @return  The total number of Beans.
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * @return  The number of Beans being displayed per page.
     */
    public int getNumberPerPage() {
        return numberPerPage;
    }

    /**
     * Sets the number of Beans being displayed per page.
     *
     * @param   numberPerPage The number of Beans displayed per page.
     */
    public void setNumberPerPage(int numberPerPage) {
        this.numberPerPage = numberPerPage;
    }

    /**
     * @return  The page being displayed (1 based).
     */
    public int getPage() {
        return page;
    }

    /**
     * Sets the page being displayed.
     *
     * @param   page The page to display (1 based).
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return  Determines if all of the Beans are to be displayed.
     */
    public boolean isShowAll() {
        return showAll;
    }

    /**
     * Sets whether or not all the Beans are being displayed.
     *
     * @param   showAll True to display all the Beans, false to paginate.
     */
    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    /**
     * @return  The results of the search or an empty list.
     */
    public List<Bean> getBeans() {
        return beans;
    }

    @Override
    public String execute() {
        if (showAll) {
            beans = beanService.find(sortProperty);
        } else {
            beans = beanService.find(page, numberPerPage, sortProperty);
        }

        totalCount = beanService.getNumberOfBeans();
        return SUCCESS;
    }
}