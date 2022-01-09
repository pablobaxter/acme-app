package com.frybits.acme.models

/**
 * Route Model
 */
data class Route(
    val addressNumber: Int,
    val addressName: String,
    val dwellingType: DwellingType = DwellingType.SINGLE_FAMILY,
    val dwellingNumber: Int? = null
) {

    override fun toString(): String {
        return buildString {
            append(addressNumber)
            append(" ")
            append(addressName)
            if (dwellingType != DwellingType.SINGLE_FAMILY) {
                append(" ")
                append(dwellingType)
                if (dwellingNumber != null) {
                    append(" ")
                    append(dwellingNumber)
                }
            }
        }
    }
}

enum class DwellingType(private val type: String) {
    SUITE("Suite"), APARTMENT("Apt."), SINGLE_FAMILY("");

    override fun toString(): String {
        return type
    }

    companion object {
        fun getType(value: String): DwellingType {
            return when (value) {
                SUITE.type -> SUITE
                APARTMENT.type -> APARTMENT
                else -> SINGLE_FAMILY
            }
        }
    }
}
