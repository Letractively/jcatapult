package org.jcatapult.example.webapp.service;

import java.util.ArrayList;
import java.util.List;

import org.jcatapult.persistence.PersistenceService;

import com.google.inject.Inject;
import org.jcatapult.example.webapp.domain.GuestBookEntry;

/**
 * <p>
 * This is the implementation of the GuestBookEntryService.
 * </p>
 *
 * @author  Scaffolder
 */
public class GuestBookEntryServiceImpl implements GuestBookEntryService {
    private PersistenceService persistenceService;

    @Inject
    public GuestBookEntryServiceImpl(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * {@inheritDoc}
     */
    public List<GuestBookEntry> find(int page, int number, String sortProperty) {
        if (sortProperty == null) {
            sortProperty = getDefaultSortProperty();
        }

        page--;

        return persistenceService.query(GuestBookEntry.class, "select obj from GuestBookEntry obj " +
            "order by obj." + sortProperty, page * number, number);
    }

    /**
     * {@inheritDoc}
     */
    public List<GuestBookEntry> find(String sortProperty) {
        if (sortProperty == null) {
            sortProperty = getDefaultSortProperty();
        }

        return persistenceService.queryAll(GuestBookEntry.class, "select obj from GuestBookEntry obj " +
            "order by obj." + sortProperty);
    }

    /**
     * @return  The default sort property.
     */
    protected String getDefaultSortProperty() {
        return "name";
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfGuestBookEntries() {
        return (int) persistenceService.count(GuestBookEntry.class);
    }

    /**
     * {@inheritDoc}
     */
    public void persist(GuestBookEntry guestBookEntry) {
        persistenceService.persist(guestBookEntry);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(int id) {
        persistenceService.delete(GuestBookEntry.class, id);
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
    public GuestBookEntry getById(Integer id) {
        GuestBookEntry guestBookEntry = persistenceService.findById(GuestBookEntry.class, id);
        return guestBookEntry;
    }
}
