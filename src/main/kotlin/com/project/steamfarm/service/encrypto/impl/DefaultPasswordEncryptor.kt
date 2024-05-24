package com.project.steamfarm.service.encrypto.impl

import com.project.steamfarm.service.encrypto.PasswordEncryptor
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*
import javax.crypto.Cipher

class DefaultPasswordEncryptor: PasswordEncryptor {
    override fun encrypt(pubKeySpec: RSAPublicKeySpec, pass: String): String? {
        try {
            val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
            val key: RSAPublicKey = keyFactory.generatePublic(pubKeySpec) as RSAPublicKey

            val cipher = Cipher.getInstance("RSA")
            cipher.init(Cipher.ENCRYPT_MODE, key)

            return Base64.getEncoder().encodeToString(cipher.doFinal(pass.toByteArray(StandardCharsets.UTF_8)))
        } catch (e: Exception) {
            return null
        }
    }
}