package com.example.kitabu.data.local

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromBookingStatus(status: BookingStatus): String {
        return status.name
    }

    @TypeConverter
    fun toBookingStatus(status: String): BookingStatus {
        return try {
            BookingStatus.valueOf(status)
        } catch (e: Exception) {
            BookingStatus.PENDING
        }
    }
}
