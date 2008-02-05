package org.jcatapult.example.component.stickynote.action.stickynote;

import java.util.List;

import org.jcatapult.example.component.stickynote.domain.StickyNote;
import org.jcatapult.example.component.stickynote.service.StickyNoteService;
import org.jcatapult.servlet.JCatapultFilter;
import org.jcatapult.servlet.ServletObjectsHolder;
import org.jcatapult.struts.action.BaseAction;

import com.google.inject.Inject;

/**
 * <p>
 * This class fetches all the sticky notes for the current page and displays them
 * using a FreeMarker template.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class Fetch extends BaseAction {
    private final StickyNoteService stickyNoteService;
    private final String originalURI;
    private List<StickyNote> stickyNotes;

    @Inject
    public Fetch(StickyNoteService stickyNoteService) {
        this.stickyNoteService = stickyNoteService;
        this.originalURI = (String) ServletObjectsHolder.getServletRequest().
            getAttribute(JCatapultFilter.ORIGINAL_REQUEST_URI);
    }

    /**
     * @return  The sticky notes for the current URI.
     */
    public List<StickyNote> getStickyNotes() {
        return stickyNotes;
    }

    @Override
    public String execute() throws Exception {
        this.stickyNotes = stickyNoteService.findByURI(originalURI);
        return SUCCESS;
    }
}