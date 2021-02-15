package ru.javazen.telegram.bot.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SizedItemsContainerTest {
    private final SizedItemsContainer<String> container = new SizedItemsContainer<>();

    @Before
    public void setUp() {
        container.put("A", 1.0);
        container.put("B", 2.0);
        container.put("C", 1.5);
    }

    @Test
    public void get() {
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
    public void size() {
        assertEquals(1.0 + 2.0 + 1.5, container.size(), 0);
    }

}