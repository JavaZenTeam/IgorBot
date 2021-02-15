package ru.javazen.telegram.bot.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MilestoneHelperTest {
    MilestoneHelper milestoneHelper = new MilestoneHelper();

    @Test
    void isAnniversary() {
        assertFalse(milestoneHelper.isMilestone(100));
        assertFalse(milestoneHelper.isMilestone(999));
        assertTrue(milestoneHelper.isMilestone(1000));
        assertTrue(milestoneHelper.isMilestone(2000));
        assertFalse(milestoneHelper.isMilestone(2222));
        assertTrue(milestoneHelper.isMilestone(1_000_000));
    }

    @Test
    void prevAnniversary() {
        assertEquals(0, milestoneHelper.prevMilestone(500));
        assertEquals(0, milestoneHelper.prevMilestone(1000));
        assertEquals(1000, milestoneHelper.prevMilestone(1500));
        assertEquals(2000, milestoneHelper.prevMilestone(2500));
        assertEquals(9000, milestoneHelper.prevMilestone(9900));
        assertEquals(9000, milestoneHelper.prevMilestone(10_000));
        assertEquals(10_000, milestoneHelper.prevMilestone(11_500));
        assertEquals(100_000, milestoneHelper.prevMilestone(111_500));
        assertEquals(1_000_000, milestoneHelper.prevMilestone(1_111_500));
        assertEquals(9_000_000, milestoneHelper.prevMilestone(10_000_000));
        assertEquals(10_000_000, milestoneHelper.prevMilestone(11_111_500));
    }

    @Test
    void nextAnniversary() {
        assertEquals(1000, milestoneHelper.nextMilestone(500));
        assertEquals(2000, milestoneHelper.nextMilestone(1000));
        assertEquals(2000, milestoneHelper.nextMilestone(1500));
        assertEquals(3000, milestoneHelper.nextMilestone(2500));
        assertEquals(10_000, milestoneHelper.nextMilestone(9000));
        assertEquals(10_000, milestoneHelper.nextMilestone(9900));
        assertEquals(20_000, milestoneHelper.nextMilestone(10_000));
        assertEquals(20_000, milestoneHelper.nextMilestone(11_500));
        assertEquals(200_000, milestoneHelper.nextMilestone(111_500));
        assertEquals(1_000_000, milestoneHelper.nextMilestone(900_000));
        assertEquals(1_000_000, milestoneHelper.nextMilestone(999_500));
        assertEquals(20_000_000, milestoneHelper.nextMilestone(11_999_500));
    }
}