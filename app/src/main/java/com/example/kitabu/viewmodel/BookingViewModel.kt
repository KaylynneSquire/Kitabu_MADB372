package com.example.kitabu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kitabu.data.local.BookingEntity
import com.example.kitabu.data.repository.BookingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookingViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    val bookings: StateFlow<List<BookingEntity>> =
        repository.getAllBookings()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val currentUserBookings: StateFlow<List<BookingEntity>> =
        repository.getBookingsForUser("Current User")
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addBooking(booking: BookingEntity) {
        viewModelScope.launch {
            repository.insertBooking(booking)
        }
    }

    fun updateBooking(booking: BookingEntity) {
        viewModelScope.launch {
            repository.updateBooking(booking)
        }
    }

    fun renewBooking(booking: BookingEntity) {
        viewModelScope.launch {
            val renewedBooking = booking.copy(
                returnDeadline = booking.returnDeadline +
                        (7L * 24L * 60L * 60L * 1000L)
            )

            repository.updateBooking(renewedBooking)
        }
    }

    fun deleteBooking(booking: BookingEntity) {
        viewModelScope.launch {
            repository.deleteBooking(booking)
        }
    }

    fun deleteAllBookings() {
        viewModelScope.launch {
            repository.deleteAllBookings()
        }
    }
}
