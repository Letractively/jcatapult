package org.jcatapult.example.webapp.action.guestBook;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.jcatapult.example.webapp.service.GuestBookEntryService;

import com.google.inject.Inject;

/**
 * <p>
 * This class fetches an existing GuestBookEntry for editing.
 * </p>
 *
 * @author  Scaffolder
 */
public class Edit extends BaseForm {
    private Integer id;

    @Inject
    public Edit(GuestBookEntryService guestBookEntryService) {
        super(guestBookEntryService);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    @Action(results = {
        @Result(name = "error", location = "index.jsp"),
        @Result(name = "success", location = "index", type = "redirectAction")
    })
    public String execute() {
        if (isGet()) {
            guestBookEntry = guestBookEntryService.getById(id);
            if (guestBookEntry == null) {
                addActionError("That GuestBookEntry has been deleted.");
                return ERROR;
            }
        } else {
            guestBookEntryService.persist(guestBookEntry);
        }

        return SUCCESS;
    }
}