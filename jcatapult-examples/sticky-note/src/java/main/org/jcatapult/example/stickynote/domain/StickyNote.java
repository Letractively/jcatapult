package org.jcatapult.example.stickynote.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.jcatapult.domain.IdentifiableImpl;

/**
 * <p>
 * This class is a StickyNote.
 * </p>
 *
 * @author  Scaffolder
 */
@Entity()
@Table(name = "sticky_notes")
public class StickyNote extends IdentifiableImpl {
    @Column(nullable = false)
    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}