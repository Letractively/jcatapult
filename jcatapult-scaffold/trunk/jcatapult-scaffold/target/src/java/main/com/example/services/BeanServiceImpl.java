package com.example.services;

import java.util.ArrayList;
import java.util.List;

import com.texturemedia.catapult.service.CatapultBeanService;

import com.google.inject.Inject;
import org.jcatapult.scaffold.Bean;
import org.jcatapult.scaffold.Email;
import org.jcatapult.scaffold.Category;

/**
 * <p>
 * This is the implementation of the BeanService.
 * </p>
 *
 * @author  Scaffolder
 */
public class BeanServiceImpl implements BeanService {
    private CatapultBeanService catapultBeanService;

    @Inject
    public BeanServiceImpl(CatapultBeanService catapultBeanService) {
        this.catapultBeanService = catapultBeanService;
    }

    /**
     * {@inheritDoc}
     */
    public List<Bean> find(int page, int number, String sortProperty) {
        if (sortProperty == null) {
            sortProperty = getDefaultSortProperty();
        }

        page--;

        return catapultBeanService.queryPaginated(Bean.class, "select obj from Bean obj " +
            "order by obj." + sortProperty, page * number, number);
    }

    /**
     * {@inheritDoc}
     */
    public List<Bean> find(String sortProperty) {
        if (sortProperty == null) {
            sortProperty = getDefaultSortProperty();
        }

        return catapultBeanService.query(Bean.class, "select obj from Bean obj " +
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
    public int getNumberOfBeans() {
        return (int) catapultBeanService.queryCount(Bean.class,
        "select count(obj) from Bean obj "  );
    }

    /**
     * {@inheritDoc}
     */
    public void persist(Bean bean, Integer emailId, Integer[] categoriesIds) {
        Email email = catapultBeanService.getById(Email.class, emailId);
        bean.setEmail(email);

        List<Category> categories = new ArrayList<Category>();
        for (Integer id : categoriesIds) {
            categories.add(catapultBeanService.findById(Category.class, id));
        }
        bean.setCategories(categories);

        catapultBeanService.persist(bean);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(int id) {
        catapultBeanService.delete(Bean.class, id);
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
    public List<Email> getEmails() {
        return catapultBeanService.findAllByType(Email.class);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getCategories() {
        return catapultBeanService.getAll(Category.class);
    }

    /**
     * {@inheritDoc}
     */
    public Bean getById(Integer id) {
        Bean bean = catapultBeanService.getById(Bean.class, id);
        return bean;
    }
}
