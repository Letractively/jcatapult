package org.jcatapult.example.webapp.action.guestBook;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jcatapult.struts.action.BaseAction;

import com.google.inject.Inject;
import org.jcatapult.example.webapp.service.GuestBookEntryService;

/**
 * <p>
 * This class prepares the form that adds and edits GuestBookEntries.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Prepare extends BaseAction {
    private static final Logger logger = Logger.getLogger(Prepare.class.getName());
    private final GuestBookEntryService guestBookEntryService;

    @Inject
    public Prepare(GuestBookEntryService guestBookEntryService) {
        this.guestBookEntryService = guestBookEntryService;
    }

    public String execute() {
        return SUCCESS;
    }
}