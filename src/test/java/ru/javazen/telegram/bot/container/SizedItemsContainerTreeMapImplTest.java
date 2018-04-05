package ru.javazen.telegram.bot.container;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SizedItemsContainerTreeMapImplTest {
    private SizedItemsContainer<String> container = new SizedItemsContainerTreeMapImpl<>();

    @Before
    public void setUp() throws Exception {
        container.put("A", 1.0);
        container.put("B", 2.0);
        container.put("C", 1.5);
    }

    @Test
    public void get() throws Exception {
        assertEquals("A", container.get(0.0));
        assertEquals("A", container.get(0.5));

        assertEquals("B", container.get(1.0));
        assertEquals("B", container.get(1.5));
        assertEquals("B", container.get(2.0));
        assertEquals("B", container.get(2.5));
        assertEquals("C", container.get(3.0));
        assertEquals("C", container.get(3.5));
        assertEquals("C", container.get(4.0));

        assertNull(container.get(4.5));
        assertNull(container.get(5.0));
    }

    @Test
    public void size() throws Exception {
        assertEquals(1.0 + 2.0 + 1.5, container.size(), 0);
    }

}