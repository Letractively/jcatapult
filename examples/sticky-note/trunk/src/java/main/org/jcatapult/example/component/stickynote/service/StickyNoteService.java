package org.jcatapult.example.component.stickynote.service;

import java.util.List;

import org.jcatapult.example.component.stickynote.domain.StickyNote;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This is the service for dealing with {@link StickyNote}s
 * </p>
 *
 * @author  Scaffolder
 */
@ImplementedBy(StickyNoteServiceImpl.class)
public interface StickyNoteService {
    /**
     * Gets all of the StickyNotes sorted using the given column name.
     *
     * @param   sortProperty (Optional) The sort property on the {@link StickyNote} object.
     * @return  The List of StickyNotes.
     */
    List<StickyNote> find(String sortProperty);

    /**
     * Gets a page of the StickyNotes sorted using the given column name.
     *
     * @param   page The page of StickyNotes to fetch (1 based).
     * @param   numberPerPage The number of StickyNotes to fetch (1 based).
     * @param   sortProperty (Optional) The sort property on the {@link StickyNote} object.
     * @return  The List of StickyNotes.
     */
    List<StickyNote> find(int page, int numberPerPage, String sortProperty);

    /**
     * @return  The total number of StickyNotes.
     */
    int getNumberOfStickyNotes();

    /**
     * Saves or updates the given StickyNote.
     *
     * @param   stickyNote The StickyNote to save or update.
     */
    void persist(StickyNote stickyNote);

    /**
     * Deletes the StickyNote with the given ID.
     *
     * @param   id The ID of the StickyNote to delete.
     */
    void delete(int id);

    /**
     * Deletes the StickyNotes with the given IDs.
     *
     * @param   ids The IDs of the StickyNotes to delete.
     */
    void deleteMany(int[] ids);

    /**
     * Locates the StickyNote with the given id.
     *
     * @param   id The ID of the StickyNote.
     * @return  The StickyNote or null if it doesn't exist or has been deleted.
     */
    StickyNote getById(Integer id);

    /**
     * Locates all of the sticky notes for a specific URI.
     *
     * @param   uri The uri.
     * @return  The list of sticky notes or an empty list if there are none for the given URI.
     */
    List<StickyNote> findByURI(String uri);
}