package com.example.kitabu.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<BookingEntity>)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)

    @Query("SELECT * FROM bookings ORDER BY bookingDate ASC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE userName = :userName ORDER BY bookingDate ASC")
    fun getBookingsForUser(userName: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE status = :status ORDER BY bookingDate ASC")
    fun getBookingsByStatus(status: BookingStatus): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId LIMIT 1")
    suspend fun getBookingById(bookingId: Int): BookingEntity?

    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()
}
