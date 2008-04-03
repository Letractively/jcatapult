package ${pkgName};

import org.jcatapult.persistence.PersistenceService;
import org.jcatapult.test.JPABaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * <p>
 * This class tests the ${className} entity.
 * </p>
 *
 * @author  Scaffolder
 */
public class ${className}Test extends JPABaseTest {
    private PersistenceService persistenceService;

    @Inject
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Test
    public void testEntity() {
        ${className} instance = new ${className}();
        assertTrue(persistenceService.persist(instance));
    }
}