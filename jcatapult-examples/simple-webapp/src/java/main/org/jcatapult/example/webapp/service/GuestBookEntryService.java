package org.jcatapult.example.webapp.service;

import java.util.List;

import com.google.inject.ImplementedBy;
import org.jcatapult.example.webapp.domain.GuestBookEntry;

/**
 * <p>
 * This is the service for dealing with {@link GuestBookEntry}s
 * </p>
 *
 * @author  Scaffolder
 */
@ImplementedBy(GuestBookEntryServiceImpl.class)
public interface GuestBookEntryService {
    /**
     * Gets all of the GuestBookEntries sorted using the given column name.
     *
     * @param   sortProperty (Optional) The sort property on the {@link GuestBookEntry} object.
     * @return  The List of GuestBookEntries.
     */
    List<GuestBookEntry> find(String sortProperty);

    /**
     * Gets a page of the GuestBookEntries sorted using the given column name.
     *
     * @param   page The page of GuestBookEntries to fetch (1 based).
     * @param   numberPerPage The number of GuestBookEntries to fetch (1 based).
     * @param   sortProperty (Optional) The sort property on the {@link GuestBookEntry} object.
     * @return  The List of GuestBookEntries.
     */
    List<GuestBookEntry> find(int page, int numberPerPage, String sortProperty);

    /**
     * @return  The total number of GuestBookEntries.
     */
    int getNumberOfGuestBookEntries();

    /**
     * Saves or updates the given GuestBookEntry.
     *
     * @param   guestBookEntry The GuestBookEntry to save or update.
     */
    void persist(GuestBookEntry guestBookEntry);

    /**
     * Deletes the GuestBookEntry with the given ID.
     *
     * @param   id The ID of the GuestBookEntry to delete.
     */
    void delete(int id);

    /**
     * Deletes the GuestBookEntries with the given IDs.
     *
     * @param   ids The IDs of the GuestBookEntries to delete.
     */
    void deleteMany(int[] ids);

    /**
     * Locates the GuestBookEntry with the given id.
     *
     * @param   id The ID of the GuestBookEntry.
     * @return  The GuestBookEntry or null if it doesn't exist or has been deleted.
     */
    GuestBookEntry getById(Integer id);
}