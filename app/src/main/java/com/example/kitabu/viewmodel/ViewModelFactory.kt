package com.example.kitabu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kitabu.data.repository.BookRepository
import com.example.kitabu.data.repository.BookingRepository

class ViewModelFactory(
    private val bookRepository: BookRepository,
    private val bookingRepository: BookingRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        return when {

            modelClass.isAssignableFrom(BookViewModel::class.java) -> {
                BookViewModel(bookRepository) as T
            }

            modelClass.isAssignableFrom(BookingViewModel::class.java) -> {
                BookingViewModel(bookingRepository) as T
            }

            else -> {
                throw IllegalArgumentException(
                    "Unknown ViewModel class: ${modelClass.name}"
                )
            }
        }
    }
}
