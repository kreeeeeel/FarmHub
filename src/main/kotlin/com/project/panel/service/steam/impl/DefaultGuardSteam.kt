package com.project.panel.service.steam.impl

import com.project.panel.service.steam.GuardSteam

import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val chars = charArrayOf(
    '2', '3', '4', '5', '6', '7', '8', '9', 'B', 'C', 'D', 'F',
    'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'T', 'V', 'W', 'X', 'Y'
)

class DefaultGuardSteam: GuardSteam {

    override fun getCode(sharedSecret: String): String {
        val timestamp = (System.currentTimeMillis() / 1000).toInt() / 30
        val array: ByteArray = timeToUint64(timestamp)
        val keyBytes = Base64.getDecoder().decode(sharedSecret)
        val signingKey = SecretKeySpec(keyBytes, "HmacSHA1")

        val mac = Mac.getInstance("HmacSHA1")
        mac.init(signingKey)

        val rawHmac = mac.doFinal(array)
        val begin = rawHmac[19].toInt() and 0xf
        val newArray = Arrays.copyOfRange(rawHmac, begin, begin + 4)
        var fullCode: Long = fromArrayToLong(newArray) and 0x7fffffff
        val code = StringBuilder()
        for (i in 0..4) {
            val div = (fullCode / chars.size).toInt()
            val mod = (fullCode % chars.size).toInt()
            code.append(chars[mod])
            fullCode = div.toLong()
        }
        return code.toString()
    }

    private fun fromArrayToLong(array: ByteArray): Long {
        var res: Long = 0
        res = res or ((array[0].toInt() and 0xff).toLong() shl 24)
        res = res or ((array[1].toInt() and 0xff).toLong() shl 16)
        res = res or ((array[2].toInt() and 0xff).toLong() shl 8)
        res = res or (array[3].toInt() and 0xff).toLong()
        return res
    }

    private fun timeToUint64(timestamp: Int): ByteArray {
        val res = ByteArray(8)
        for (i in 4..7) {
            res[i] = (timestamp shr 24 - 8 * (i - 4) and 0xff).toByte()
        }
        return res
    }

}