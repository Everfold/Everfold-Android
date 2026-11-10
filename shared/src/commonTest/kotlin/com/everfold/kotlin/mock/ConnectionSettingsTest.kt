package com.everfold.kotlin.mock

import com.everfold.kotlin.feature.connections.ClusterAuth
import com.everfold.kotlin.feature.connections.ClusterMethod
import com.everfold.kotlin.feature.connections.ClusterSetup
import com.everfold.kotlin.feature.connections.ConnectionMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConnectionSettingsTest {
    @Test
    fun setupCarriesCredentialsIntoConnectionSettings() {
        val workspace = MockWorkspace()
        val setup =
            ClusterSetup(
                method = ClusterMethod.Central,
                ip = "192.168.1.1",
                port = "443",
                auth = ClusterAuth.Mtls,
                caCertificate = "ca.pem",
                clientPublicKey = "public.pem",
                clientPrivateKey = "private.pem",
            )
        workspace.completeSetup(setup)
        assertEquals(setup, workspace.clusterSetup)
        assertTrue(workspace.connectionPreview)
        workspace.updateClusterSetup(setup.copy(port = "8443"))
        assertFalse(workspace.connectionPreview)
        assertEquals("ca.pem", workspace.clusterSetup.caCertificate)
    }

    @Test
    fun switchingModesInvalidatesConnectionButRetainsForm() {
        val workspace = MockWorkspace()
        val setup = ClusterSetup(method = ClusterMethod.Cloud, account = "demo", password = "test")
        workspace.completeSetup(setup)
        workspace.changeConnectionMode(ConnectionMode.Local)
        workspace.updateConnectionPreview(true)
        assertFalse(workspace.connectionPreview)
        workspace.changeConnectionMode(ConnectionMode.Remote)
        assertEquals(setup, workspace.clusterSetup)
        assertFalse(workspace.connectionPreview)
    }
}
