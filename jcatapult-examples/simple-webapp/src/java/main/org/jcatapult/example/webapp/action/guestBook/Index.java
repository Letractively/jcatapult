package org.jcatapult.example.webapp.action.guestBook;

import java.util.List;

import org.jcatapult.struts.action.BaseAction;

import com.google.inject.Inject;
import org.jcatapult.example.webapp.domain.GuestBookEntry;
import org.jcatapult.example.webapp.service.GuestBookEntryService;

/**
 * <p>
 * This class is an action that lists out and sorts the GuestBookEntries.
 * </p>
 *
 * @author  Scaffolder
 */
public class Index extends BaseAction {
    private final GuestBookEntryService guestBookEntryService;
    private List<GuestBookEntry> guestBookEntries;
    private String sortProperty;
    private int totalCount;
    private int numberPerPage = 20;
    private int page = 1;
    private boolean showAll = false;

    @Inject
    public Index(GuestBookEntryService guestBookEntryService) {
        this.guestBookEntryService = guestBookEntryService;
    }

    /**
     * The name of the sort property within the {@link GuestBookEntry}. This must be a property name.
     *
     * @return  The sort property.
     */
    public String getSortProperty() {
        return sortProperty;
    }

    /**
     * Sets the name of the sort property within the {@link GuestBookEntry}. This must be a property name.
     *
     * @param   sortProperty The sort property.
     */
    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    /**
     * @return  The total number of GuestBookEntries.
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * @return  The number of GuestBookEntries being displayed per page.
     */
    public int getNumberPerPage() {
        return numberPerPage;
    }

    /**
     * Sets the number of GuestBookEntries being displayed per page.
     *
     * @param   numberPerPage The number of GuestBookEntries displayed per page.
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
     * @return  Determines if all of the GuestBookEntries are to be displayed.
     */
    public boolean isShowAll() {
        return showAll;
    }

    /**
     * Sets whether or not all the GuestBookEntries are being displayed.
     *
     * @param   showAll True to display all the GuestBookEntries, false to paginate.
     */
    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    /**
     * @return  The results of the search or an empty list.
     */
    public List<GuestBookEntry> getGuestBookEntries() {
        return guestBookEntries;
    }

    @Override
    public String execute() {
        if (showAll) {
            guestBookEntries = guestBookEntryService.find(sortProperty);
        } else {
            guestBookEntries = guestBookEntryService.find(page, numberPerPage, sortProperty);
        }

        totalCount = guestBookEntryService.getNumberOfGuestBookEntries();
        return SUCCESS;
    }
}