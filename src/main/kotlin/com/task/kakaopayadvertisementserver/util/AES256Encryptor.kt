package com.task.kakaopayadvertisementserver.util

import com.task.kakaopayadvertisementserver.property.AES256Properties
import com.task.kakaopayadvertisementserver.util.Constants.AES_ALGORITHM
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class AES256Encryptor(
    private val aes256Properties: AES256Properties,
) {
    private val algorithm = aes256Properties.algorithm // "AES/CBC/PKCS5Padding"
    private val ivSize = aes256Properties.ivSize // 16 bytes for AES
    private val secretKey = aes256Properties.secretKey

    fun encrypt(plainText: String): String {
        val keySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), AES_ALGORITHM)
        val iv = ByteArray(ivSize)
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        val combined = iv + encrypted
        return Base64.getEncoder().encodeToString(combined)
    }

    fun decrypt(encryptedText: String): String {
        val combined = Base64.getDecoder().decode(encryptedText)
        val iv = combined.copyOfRange(0, ivSize)
        val encrypted = combined.copyOfRange(ivSize, combined.size)

        val keySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), AES_ALGORITHM)
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }
}
