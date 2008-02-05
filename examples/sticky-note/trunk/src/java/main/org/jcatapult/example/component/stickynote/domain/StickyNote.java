package org.jcatapult.example.component.stickynote.domain;

import javax.persistence.Column;
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
    @Column(nullable = false)
    private String uri;

    @Column(nullable = false)
    private String headline;
    
    @Column(nullable = false)
    private String note;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

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