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
    val invalidFields: Set<ClusterField>
        get() =
            buildSet {
                if (method != ClusterMethod.Cloud && !(method == ClusterMethod.Lan && ip.isBlank() && port.isBlank())) {
                    if (!validIp(ip.trim())) add(ClusterField.Ip)
                    if (port.trim().toIntOrNull() !in 1..65535) add(ClusterField.Port)
                }
                if (method == ClusterMethod.Cloud || (method == ClusterMethod.Central && auth == ClusterAuth.Password)) {
                    if (account.isBlank()) add(ClusterField.Account)
                    if (password.isEmpty()) add(ClusterField.Password)
                }
                if (method == ClusterMethod.Central) {
                    if (auth == ClusterAuth.Certificate && serverCertificate.isNullOrBlank()) add(ClusterField.ServerCertificate)
                    if (auth == ClusterAuth.Mtls) {
                        if (caCertificate.isNullOrBlank()) add(ClusterField.CaCertificate)
                        if (clientPublicKey.isNullOrBlank()) add(ClusterField.ClientPublicKey)
                        if (clientPrivateKey.isNullOrBlank()) add(ClusterField.ClientPrivateKey)
                    }
                }
            }
    val isValid: Boolean get() = invalidFields.isEmpty()
}

enum class ClusterField { Ip, Port, Account, Password, ServerCertificate, CaCertificate, ClientPublicKey, ClientPrivateKey }

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
