package com.frybits.acme.utils

import kotlin.math.max
import kotlin.math.min

fun String.lengthIsEven(): Boolean {
    return length % 2 == 0
}

// TODO What to do with 'y'?
fun Char.isVowel(): Boolean {
    return equals('a', ignoreCase = true) ||
            equals('e', ignoreCase = true) ||
            equals('i', ignoreCase = true) ||
            equals('o', ignoreCase = true) ||
            equals('u', ignoreCase = true)
}

fun Int.gcd(other: Int): Int {
    return toLong().gcd(other.toLong()).toInt()
}

// Good ol' Euclidean Algorithm!
fun Long.gcd(other: Long): Long {
    var a = max(this, other)
    var b = min(this, other)
    while (true) {
        val r = a % b
        if (r == 0L) return b
        a = b
        b = r
    }
}
