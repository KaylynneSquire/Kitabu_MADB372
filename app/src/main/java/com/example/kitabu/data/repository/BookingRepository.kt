package com.example.kitabu.data.repository

import com.example.kitabu.data.local.BookingDao
import com.example.kitabu.data.local.BookingEntity
import com.example.kitabu.data.local.BookingStatus
import kotlinx.coroutines.flow.Flow

class BookingRepository(
    private val bookingDao: BookingDao
) {

    fun getAllBookings(): Flow<List<BookingEntity>> {
        return bookingDao.getAllBookings()
    }

    fun getBookingsForUser(userId: String): Flow<List<BookingEntity>> {
        return bookingDao.getBookingsForUser(userId)
    }

    fun getBookingsByStatus(status: BookingStatus): Flow<List<BookingEntity>> {
        return bookingDao.getBookingsByStatus(status)
    }

    suspend fun getBookingById(bookingId: Int): BookingEntity? {
        return bookingDao.getBookingById(bookingId)
    }

    suspend fun insertBooking(booking: BookingEntity) {
        bookingDao.insertBooking(booking)
    }

    suspend fun updateBooking(booking: BookingEntity) {
        bookingDao.updateBooking(booking)
    }

    suspend fun deleteBooking(booking: BookingEntity) {
        bookingDao.deleteBooking(booking)
    }

    suspend fun deleteAllBookings() {
        bookingDao.deleteAllBookings()
    }
}
