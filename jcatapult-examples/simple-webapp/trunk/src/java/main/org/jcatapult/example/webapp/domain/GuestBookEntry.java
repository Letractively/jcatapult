package org.jcatapult.example.webapp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.jcatapult.domain.AuditableImpl;

/**
 * <p>
 * This class is a GuestBookEntry.
 * </p>
 *
 * @author  Scaffolder
 */
@Entity()
@Table(name = "guest_book_entries")
public class GuestBookEntry extends AuditableImpl {
    @Column(nullable =  false)
    private String name;

    @Column(nullable = false)
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}