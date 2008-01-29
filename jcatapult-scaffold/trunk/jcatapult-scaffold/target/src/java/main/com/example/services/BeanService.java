package com.example.services;

import java.util.List;

import com.google.inject.ImplementedBy;
import org.jcatapult.scaffold.Bean;
import org.jcatapult.scaffold.Email;
import org.jcatapult.scaffold.Category;

/**
 * <p>
 * This is the service for dealing with {@link Bean}s
 * </p>
 *
 * @author  Scaffolder
 */
@ImplementedBy(BeanServiceImpl.class)
public interface BeanService {
    /**
     * Gets all of the Beans sorted using the given column name.
     *
     * @param   sortProperty (Optional) The sort property on the {@link Bean} object.
     * @return  The List of Beans.
     */
    List<Bean> find(String sortProperty);

    /**
     * Gets a page of the Beans sorted using the given column name.
     *
     * @param   page The page of Beans to fetch (1 based).
     * @param   numberPerPage The number of Beans to fetch (1 based).
     * @param   sortProperty (Optional) The sort property on the {@link Bean} object.
     * @return  The List of Beans.
     */
    List<Bean> find(int page, int numberPerPage, String sortProperty);

    /**
     * @return  The total number of Beans.
     */
    int getNumberOfBeans();

    /**
     * Saves or updates the given Bean.
     *
     * @param   bean The Bean to save or update.
     * @param   emailId The id of the email of the Bean.
     * @param   categoriesIds The list of IDs for the categories of the Bean.
     */
    void persist(Bean bean, Integer emailId, Integer[] categoriesIds);

    /**
     * Deletes the Bean with the given ID.
     *
     * @param   id The ID of the Bean to delete.
     */
    void delete(int id);

    /**
     * Deletes the Beans with the given IDs.
     *
     * @param   ids The IDs of the Beans to delete.
     */
    void deleteMany(int[] ids);

    /**
     * @return  A list of all the emails for Beans.
     */
    List<Email> getEmails();

    /**
     * @return  A list of all the categories for Beans.
     */
    List<Category> getCategories();

    /**
     * Locates the Bean with the given id.
     *
     * @param   id The ID of the Bean.
     * @return  The Bean or null if it doesn't exist or has been deleted.
     */
    Bean getById(Integer id);
}