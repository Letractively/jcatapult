package org.jcatapult.example.component.stickynote.domain;

import javax.persistence.Entity;

import org.jcatapult.domain.IdentifiableImpl;

/**
 * <p>
 * This class is a StickyNote.
 * </p>
 *
 * @author  Scaffolder
 */
@Entity()
public class StickyNote extends IdentifiableImpl {
    private String headline;
    private String note;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}