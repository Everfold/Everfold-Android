package com.everfold.kotlin.feature.connections

enum class ClusterMethod { Lan, Central, Cloud }

enum class ClusterAuth { None, Password, Certificate, Mtls }

data class ClusterSetup(
    val method: ClusterMethod = ClusterMethod.Lan,
    val ip: String = "",
    val port: String = "",
    val auth: ClusterAuth = ClusterAuth.None,
    val account: String = "",
    val password: String = "",
    val serverCertificate: String? = null,
    val caCertificate: String? = null,
    val clientPublicKey: String? = null,
    val clientPrivateKey: String? = null,
) {
    val isValid: Boolean
        get() {
            if (method == ClusterMethod.Cloud) return hasCredentials
            if (method == ClusterMethod.Lan && ip.isBlank() && port.isBlank()) return true
            if (!validIp(ip.trim()) || port.toIntOrNull() !in 1..65535) return false
            if (method == ClusterMethod.Lan) return true
            return when (auth) {
                ClusterAuth.None -> true
                ClusterAuth.Password -> hasCredentials
                ClusterAuth.Certificate -> serverCertificate != null
                ClusterAuth.Mtls -> caCertificate != null && clientPublicKey != null && clientPrivateKey != null
            }
        }
    private val hasCredentials: Boolean get() = account.isNotBlank() && password.isNotEmpty()
}

internal fun validIp(value: String): Boolean {
    if (':' !in value) {
        val parts = value.split('.')
        return parts.size == 4 && parts.all { it.isNotEmpty() && it.length <= 3 && it.all(Char::isDigit) && it.toIntOrNull() in 0..255 }
    }
    if (value.count { it == ':' } < 2 || value.contains(":::")) return false
    val halves = value.split("::")
    if (halves.size > 2) return false
    val groups = halves.flatMap { it.split(':') }.filter { it.isNotEmpty() }
    if (groups.any { it.length > 4 || it.any { char -> char !in "0123456789abcdefABCDEF" } }) return false
    return if (halves.size == 2) groups.size < 8 else groups.size == 8 && !value.startsWith(':') && !value.endsWith(':')
}
