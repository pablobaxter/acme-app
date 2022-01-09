package com.frybits.acme.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Driver Model
 */
@Parcelize
data class Driver(val name: String): Parcelable
