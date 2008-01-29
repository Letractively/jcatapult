package ${actionPackage};

import java.util.List;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionSupport;
import ${type.fullName};
import ${servicePackage}.${type.name}Service;

/**
 * <p>
 * This class is an action that lists out and sorts the ${type.pluralName}.
 * </p>
 *
 * @author  Scaffolder
 */
public class Index extends ActionSupport {
    private final ${type.name}Service ${type.fieldName}Service;
    private List<${type.name}> ${type.pluralFieldName};
    private String sortProperty;
    private int totalCount;
    private int numberPerPage = 20;
    private int page = 1;
    private boolean showAll = false;

    @Inject
    public Index(${type.name}Service ${type.fieldName}Service) {
        this.${type.fieldName}Service = ${type.fieldName}Service;
    }

    /**
     * The name of the sort property within the {@link ${type.name}}. This must be a property name.
     *
     * @return  The sort property.
     */
    public String getSortProperty() {
        return sortProperty;
    }

    /**
     * Sets the name of the sort property within the {@link ${type.name}}. This must be a property name.
     *
     * @param   sortProperty The sort property.
     */
    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    /**
     * @return  The total number of ${type.pluralName}.
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * @return  The number of ${type.pluralName} being displayed per page.
     */
    public int getNumberPerPage() {
        return numberPerPage;
    }

    /**
     * Sets the number of ${type.pluralName} being displayed per page.
     *
     * @param   numberPerPage The number of ${type.pluralName} displayed per page.
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
     * @return  Determines if all of the ${type.pluralName} are to be displayed.
     */
    public boolean isShowAll() {
        return showAll;
    }

    /**
     * Sets whether or not all the ${type.pluralName} are being displayed.
     *
     * @param   showAll True to display all the ${type.pluralName}, false to paginate.
     */
    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    /**
     * @return  The results of the search or an empty list.
     */
    public List<${type.name}> get${type.pluralName}() {
        return ${type.pluralFieldName};
    }

    @Override
    public String execute() {
        if (showAll) {
            ${type.pluralFieldName} = ${type.fieldName}Service.find(sortProperty);
        } else {
            ${type.pluralFieldName} = ${type.fieldName}Service.find(page, numberPerPage, sortProperty);
        }

        totalCount = ${type.fieldName}Service.getNumberOf${type.pluralName}();
        return SUCCESS;
    }
}