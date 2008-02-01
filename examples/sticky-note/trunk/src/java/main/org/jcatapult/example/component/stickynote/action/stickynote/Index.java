package org.jcatapult.example.component.stickynote.action.stickynote;

import java.util.List;

import org.jcatapult.struts.action.BaseAction;

import com.google.inject.Inject;
import org.jcatapult.example.component.stickynote.domain.StickyNote;
import org.jcatapult.example.component.stickynote.service.StickyNoteService;

/**
 * <p>
 * This class is an action that lists out and sorts the StickyNotes.
 * </p>
 *
 * @author  Scaffolder
 */
public class Index extends BaseAction {
    private final StickyNoteService stickyNoteService;
    private List<StickyNote> stickyNotes;
    private String sortProperty;
    private int totalCount;
    private int numberPerPage = 20;
    private int page = 1;
    private boolean showAll = false;

    @Inject
    public Index(StickyNoteService stickyNoteService) {
        this.stickyNoteService = stickyNoteService;
    }

    /**
     * The name of the sort property within the {@link StickyNote}. This must be a property name.
     *
     * @return  The sort property.
     */
    public String getSortProperty() {
        return sortProperty;
    }

    /**
     * Sets the name of the sort property within the {@link StickyNote}. This must be a property name.
     *
     * @param   sortProperty The sort property.
     */
    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    /**
     * @return  The total number of StickyNotes.
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * @return  The number of StickyNotes being displayed per page.
     */
    public int getNumberPerPage() {
        return numberPerPage;
    }

    /**
     * Sets the number of StickyNotes being displayed per page.
     *
     * @param   numberPerPage The number of StickyNotes displayed per page.
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
     * @return  Determines if all of the StickyNotes are to be displayed.
     */
    public boolean isShowAll() {
        return showAll;
    }

    /**
     * Sets whether or not all the StickyNotes are being displayed.
     *
     * @param   showAll True to display all the StickyNotes, false to paginate.
     */
    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    /**
     * @return  The results of the search or an empty list.
     */
    public List<StickyNote> getStickyNotes() {
        return stickyNotes;
    }

    @Override
    public String execute() {
        if (showAll) {
            stickyNotes = stickyNoteService.find(sortProperty);
        } else {
            stickyNotes = stickyNoteService.find(page, numberPerPage, sortProperty);
        }

        totalCount = stickyNoteService.getNumberOfStickyNotes();
        return SUCCESS;
    }
}