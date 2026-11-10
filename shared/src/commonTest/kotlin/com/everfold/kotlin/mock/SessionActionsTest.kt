package com.everfold.kotlin.mock

import com.everfold.kotlin.ui.localization.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SessionActionsTest {
    @Test
    fun pinningAndRenamingPreserveTheConversation() {
        val workspace = MockWorkspace()
        workspace.togglePinned("reading")
        assertEquals("reading", workspace.conversations.first().id)
        val id = workspace.createConversation()
        assertEquals("reading", workspace.conversations.first().id)
        workspace.renameConversation(id, "  My plan  ")
        workspace.renameConversation(id, " ")
        assertEquals(UiText.Literal("My plan"), workspace.conversations.first { it.id == id }.title)
        workspace.conversations.toList().forEach { workspace.deleteConversation(it.id) }
        assertTrue(workspace.conversations.isEmpty())
        assertTrue(workspace.createConversation().isNotBlank())
    }

    @Test
    fun deletingAStreamingSessionDoesNotRecreateItOrStopOtherSessions() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val workspace = MockWorkspace()
                val named = workspace.createConversation()
                workspace.renameConversation(named, "My title")
                workspace.updateDraft(named, "First message")
                workspace.send(named, "Reply")
                workspace.updateDraft("welcome", "hello")
                workspace.updateDraft("week", "plan")
                workspace.send("welcome", "A long reply that is still streaming")
                workspace.send("week", "Other reply")
                advanceTimeBy(400)
                workspace.deleteConversation("welcome")
                advanceUntilIdle()
                assertFalse(workspace.conversations.any { it.id == "welcome" })
                assertEquals(UiText.Literal("My title"), workspace.conversations.first { it.id == named }.title)
                val other = workspace.conversations.first { it.id == "week" }
                assertFalse(other.isResponding)
                assertEquals("Other reply", other.messages.last().text)
            } finally {
                Dispatchers.resetMain()
            }
        }
}
