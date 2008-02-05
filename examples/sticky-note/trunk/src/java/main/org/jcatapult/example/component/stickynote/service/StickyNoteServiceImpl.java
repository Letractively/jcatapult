package org.jcatapult.example.component.stickynote.service;

import java.util.ArrayList;
import java.util.List;

import org.jcatapult.persistence.PersistenceService;

import com.google.inject.Inject;
import org.jcatapult.example.component.stickynote.domain.StickyNote;

/**
 * <p>
 * This is the implementation of the StickyNoteService.
 * </p>
 *
 * @author  Scaffolder
 */
public class StickyNoteServiceImpl implements StickyNoteService {
    private PersistenceService persistenceService;

    @Inject
    public StickyNoteServiceImpl(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * {@inheritDoc}
     */
    public List<StickyNote> find(int page, int number, String sortProperty) {
        if (sortProperty == null) {
            sortProperty = getDefaultSortProperty();
        }

        page--;

        return persistenceService.query(StickyNote.class, "select obj from StickyNote obj " +
            "order by obj." + sortProperty, page * number, number);
    }

    /**
     * {@inheritDoc}
     */
    public List<StickyNote> find(String sortProperty) {
        if (sortProperty == null) {
            sortProperty = getDefaultSortProperty();
        }

        return persistenceService.queryAll(StickyNote.class, "select obj from StickyNote obj " +
            "order by obj." + sortProperty);
    }

    /**
     * @return  The default sort property.
     */
    protected String getDefaultSortProperty() {
        return "note";
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfStickyNotes() {
        return (int) persistenceService.count(StickyNote.class);
    }

    /**
     * {@inheritDoc}
     */
    public void persist(StickyNote stickyNote) {
        persistenceService.persist(stickyNote);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(int id) {
        persistenceService.delete(StickyNote.class, id);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteMany(int[] ids) {
        for (int id : ids) {
            delete(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    public StickyNote getById(Integer id) {
        StickyNote stickyNote = persistenceService.findById(StickyNote.class, id);
        return stickyNote;
    }
}
