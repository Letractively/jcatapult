package ${pkgName};

import static org.easymock.EasyMock.*;
import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.test.JCatapultBaseTest;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * <p>
 * This class tests the ${className} action.
 * </p>
 *
 * @author  Scaffolder
 */
public class ${className}Test extends JCatapultBaseTest {
    @Test
    public void testExecute() {
        MessageStore messageStore = createStrictMock(MessageStore.class);
        replay(messageStore);

        ${className} action = new ${className}(messageStore);
        // Setup action here

        String result = action.execute();
        assertEquals("success", result);
        // Additional verification here
    }
}