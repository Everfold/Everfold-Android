package com.everfold.kotlin.mock

import com.everfold.kotlin.feature.connections.ClusterSetup
import com.everfold.kotlin.feature.conversation.AgentRunState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AgentStateTest {
    @Test
    fun taskFailureDoesNotDisconnectOrStopAnotherSession() {
        val workspace = MockWorkspace()
        workspace.completeSetup(ClusterSetup())
        workspace.previewRunState("welcome", AgentRunState.Failed)
        assertTrue(workspace.connectionPreview)
        assertEquals(AgentRunState.Failed, workspace.conversations.first { it.id == "welcome" }.runState)
        assertEquals(AgentRunState.Idle, workspace.conversations.first { it.id == "week" }.runState)
    }

    @Test
    fun waitingAndStoppingAreDistinctStates() {
        val workspace = MockWorkspace()
        workspace.previewRunState("welcome", AgentRunState.Waiting)
        val waiting = workspace.conversations.first { it.id == "welcome" }
        assertFalse(waiting.isResponding)
        assertFalse(waiting.responseStopped)
        workspace.stop("welcome")
        assertTrue(workspace.conversations.first { it.id == "welcome" }.responseStopped)
    }
}
