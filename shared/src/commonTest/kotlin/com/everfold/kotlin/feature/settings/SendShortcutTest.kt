package com.everfold.kotlin.feature.settings

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SendShortcutTest {
    @Test
    fun modifiedEnterSupportsControlAndCommand() {
        assertTrue(SendShortcut.ModifiedEnter.matches(true, false, false, false, true, false))
        assertTrue(SendShortcut.ModifiedEnter.matches(false, true, false, false, true, false))
        assertFalse(SendShortcut.ModifiedEnter.matches(false, false, false, false, true, false))
    }

    @Test
    fun plainEnterIsOnlyForDesktopAndAllowsShiftNewline() {
        assertTrue(SendShortcut.Enter.matches(false, false, false, false, true, false))
        assertFalse(SendShortcut.Enter.matches(false, false, true, false, true, false))
        assertFalse(SendShortcut.Enter.matches(false, false, false, false, false, false))
        assertFalse(SendShortcut.Enter.matches(true, false, false, false, true, false))
    }

    @Test
    fun neitherShortcutSubmitsWhileComposingOrWithAltOrShift() {
        for (shortcut in SendShortcut.entries) {
            val ctrl = shortcut == SendShortcut.ModifiedEnter
            assertFalse(shortcut.matches(ctrl, false, false, false, true, true))
            assertFalse(shortcut.matches(ctrl, false, true, false, true, false))
            assertFalse(shortcut.matches(ctrl, false, false, true, true, false))
        }
    }
}
