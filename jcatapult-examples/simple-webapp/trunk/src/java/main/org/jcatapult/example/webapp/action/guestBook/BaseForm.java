package org.jcatapult.example.webapp.action.guestBook;

import org.jcatapult.example.webapp.domain.GuestBookEntry;
import org.jcatapult.example.webapp.service.GuestBookEntryService;
import org.jcatapult.struts.action.BaseAction;

import com.google.inject.Inject;

/**
 * <p>
 * This class is the base class for the forms. It provides validation
 * support.
 * </p>
 *
 * @author  Scaffolder
 */
public abstract class BaseForm extends BaseAction {
    final GuestBookEntryService guestBookEntryService;
    GuestBookEntry guestBookEntry;

    @Inject
    public BaseForm(GuestBookEntryService guestBookEntryService) {
        this.guestBookEntryService = guestBookEntryService;
    }

    public GuestBookEntry getGuestBookEntry() {
        return guestBookEntry;
    }

    public void setGuestBookEntry(GuestBookEntry guestBookEntry) {
        this.guestBookEntry = guestBookEntry;
    }

    /**
     * Clear out the errors if the request is a GET, which means a form was not submitted.
     */
    @Override
    public void validate() {
        if (isGet()) {
            clearErrors();
        } else {
            super.validate();
        }
    }
}