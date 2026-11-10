package com.everfold.kotlin.feature.connections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ClusterSetupTest {
    @Test
    fun lanAllowsDiscoveryButRequiresCompleteExplicitAddress() {
        assertTrue(ClusterSetup().isValid)
        assertFalse(ClusterSetup(ip = "192.168.1.2").isValid)
        assertFalse(ClusterSetup(port = "8080").isValid)
        assertTrue(ClusterSetup(ip = "192.168.1.2", port = "8080").isValid)
        assertFalse(ClusterSetup(ip = "256.1.1.1", port = "8080").isValid)
        assertFalse(ClusterSetup(ip = "192.168.1.2", port = "65536").isValid)
        assertFalse(ClusterSetup(ip = "192.168.1.2", port = "0").isValid)
    }

    @Test
    fun centralNodeRequiresCredentialsForSelectedAuthOnly() {
        val node = ClusterSetup(method = ClusterMethod.Central, ip = "::1", port = "443")
        assertTrue(node.isValid)
        assertFalse(node.copy(auth = ClusterAuth.Password).isValid)
        assertTrue(node.copy(auth = ClusterAuth.Password, account = "user", password = "secret").isValid)
        assertFalse(node.copy(auth = ClusterAuth.Certificate).isValid)
        assertTrue(node.copy(auth = ClusterAuth.Certificate, serverCertificate = "server.pem").isValid)
        val mtls = node.copy(auth = ClusterAuth.Mtls, caCertificate = "ca.pem", clientPublicKey = "public.pem")
        assertFalse(mtls.isValid)
        assertTrue(mtls.copy(clientPrivateKey = "private.pem").isValid)
    }

    @Test
    fun cloudRequiresAccountAndPasswordWithoutAnAddress() {
        val cloud = ClusterSetup(method = ClusterMethod.Cloud)
        assertFalse(cloud.isValid)
        assertFalse(cloud.copy(account = "user").isValid)
        assertTrue(cloud.copy(account = "user", password = "secret").isValid)
    }

    @Test
    fun ipv6RejectsMalformedCompression() {
        assertTrue(validIp("2001:db8::1"))
        assertTrue(validIp("::"))
        assertFalse(validIp("1::2::3"))
        assertFalse(validIp(":::"))
        assertFalse(validIp("1:2:3"))
        assertFalse(validIp("hello"))
    }

    @Test
    fun validationReportsOnlyFieldsRequiredByTheActiveMethod() {
        val form = ClusterSetup(method = ClusterMethod.Central, ip = "not-an-ip", port = "70000", auth = ClusterAuth.Password)
        assertEquals(setOf(ClusterField.Ip, ClusterField.Port, ClusterField.Account, ClusterField.Password), form.invalidFields)
        assertEquals(setOf(ClusterField.Account, ClusterField.Password), form.copy(method = ClusterMethod.Cloud).invalidFields)
        assertTrue(form.copy(method = ClusterMethod.Lan, ip = "", port = "").invalidFields.isEmpty())
    }

    @Test
    fun removingAnMtlsFileInvalidatesOnlyThatField() {
        val form =
            ClusterSetup(
                method = ClusterMethod.Central,
                ip = "::1",
                port = " 443 ",
                auth = ClusterAuth.Mtls,
                caCertificate = "ca.pem",
                clientPublicKey = "public.pem",
                clientPrivateKey = "private.pem",
            )
        assertTrue(form.isValid)
        assertEquals(setOf(ClusterField.ClientPrivateKey), form.copy(clientPrivateKey = null).invalidFields)
        assertEquals(setOf(ClusterField.CaCertificate), form.copy(caCertificate = " ").invalidFields)
    }
}
