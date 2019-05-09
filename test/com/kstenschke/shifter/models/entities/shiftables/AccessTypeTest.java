package com.kstenschke.shifter.models.entities.shiftables;

import com.kstenschke.shifter.models.ActionContainer;
import com.kstenschke.shifter.models.entities.AbstractShiftable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AccessTypeTest {

    private AccessType accessType;
    private AbstractShiftable shiftable;
    private ActionContainer actionContainer;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        accessType = null;
        shiftable = null;
        actionContainer = null;
    }

    @Test
    public void getInstanceNoActionContainer() {
        accessType = new AccessType(null);
        assertNull(accessType.getInstance());
    }

    @Test
    public void getInstanceAtParam() {
        actionContainer = new ActionContainer(null, true, false);
        actionContainer.setPrefixChar("@");
        actionContainer.setSelectedText("param");
        accessType = new AccessType(actionContainer);
        assertNull(accessType.getInstance());
    }

    @Test
    public void getInstanceFoo() {
        actionContainer = new ActionContainer(null, true, false);
        actionContainer.setPrefixChar("");
        actionContainer.setSelectedText("foo");
        accessType = new AccessType(actionContainer);
        assertNull(accessType.getInstance());
    }

    @Test
    public void getInstancePublic() {
        actionContainer = new ActionContainer(null, true, false);
        actionContainer.setPrefixChar("");
        actionContainer.setSelectedText("public");
        accessType = new AccessType(actionContainer);
        assertNotNull(accessType.getInstance());
    }

    @Test
    public void getInstanceProtected() {
        actionContainer = new ActionContainer(null, true, false);
        actionContainer.setSelectedText("protected");
        accessType = new AccessType(actionContainer);
        assertNotNull(accessType.getInstance());
    }

    @Test
    public void getInstancePrivate() {
        actionContainer = new ActionContainer(null, true, false);
        actionContainer.setSelectedText("private");
        accessType = new AccessType(actionContainer);
        assertNotNull(accessType.getInstance());
    }

    @Test
    public void getShiftedUpPublic() {
        actionContainer = new ActionContainer(null, true, false);

        actionContainer.setPrefixChar("");
        actionContainer.setSelectedText("public");
        accessType = new AccessType(actionContainer);
        shiftable = accessType.getInstance();
        assertEquals("protected", shiftable.getShifted("public"));
    }

    @Test
    public void getShiftedUpProtected() {
        actionContainer = new ActionContainer(null, true, false);

        actionContainer.setSelectedText("protected");
        accessType = new AccessType(actionContainer);
        shiftable = accessType.getInstance();
        assertEquals("private", shiftable.getShifted("protected"));
    }

    @Test
    public void getShiftedUpPrivate() {
        actionContainer = new ActionContainer(null, true, false);

        actionContainer.setSelectedText("private");
        accessType = new AccessType(actionContainer);
        shiftable = accessType.getInstance();
        assertEquals("public", shiftable.getShifted("private"));
    }

    @Test
    public void getShiftedDownPrivate() {
        actionContainer = new ActionContainer(null, false, false);

        actionContainer.setSelectedText("private");
        accessType = new AccessType(actionContainer);
        shiftable = accessType.getInstance();
        assertEquals("protected", shiftable.getShifted("private"));
    }

    @Test
    public void getShiftedDownProtected() {
        actionContainer = new ActionContainer(null, false, false);
        actionContainer.setSelectedText("protected");
        accessType = new AccessType(actionContainer);
        shiftable = accessType.getInstance();
        assertEquals("public", shiftable.getShifted("protected"));
    }

    @Test
    public void getShiftedDownPublic() {
        actionContainer = new ActionContainer(null, false, false);

        actionContainer.setSelectedText("public");
        accessType = new AccessType(actionContainer);
        shiftable = accessType.getInstance();
        assertEquals("private", shiftable.getShifted("public"));
    }
}