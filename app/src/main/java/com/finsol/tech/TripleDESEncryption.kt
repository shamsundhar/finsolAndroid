package com.finsol.tech

import android.util.Base64
import android.util.Base64.encodeToString
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


class TripleDESEncryption {

    val key = "Finsol Technology Ltd."

    fun encrypt(message: String): String {
        val md = MessageDigest.getInstance("md5")
        val digestOfPassword = md.digest(key.toByteArray(charset("utf-8")))
        val keyBytes = Arrays.copyOf(digestOfPassword, 24)
        var j = 0
        var k = 16
        while (j < 8) {
            keyBytes[k++] = keyBytes[j++]
        }
        val keyz: SecretKey = SecretKeySpec(keyBytes, "DESede")
//        val iv =
//            IvParameterSpec(ByteArray(8))
        val cipher = Cipher.getInstance("DESede/ECB/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keyz)
        val plainTextBytes = message.toByteArray(charset("utf-8"))
        val cipherText = cipher.doFinal(plainTextBytes)
        return encodeToString(cipherText,Base64.DEFAULT)
    }

}