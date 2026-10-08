package com.example.kitabu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kitabu.data.local.BookEntity
import com.example.kitabu.data.repository.BookRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val books: StateFlow<List<BookEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllBooks()
            } else {
                repository.searchBooks(query.trim())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun addBook(book: BookEntity) {
        viewModelScope.launch {
            repository.insertBook(book)
        }
    }

    fun updateBook(book: BookEntity) {
        viewModelScope.launch {
            repository.updateBook(book)
        }
    }

    fun setBookAvailability(book: BookEntity, isAvailable: Boolean) {
        viewModelScope.launch {
            repository.updateBook(
                book.copy(
                    isAvailable = isAvailable
                )
            )
        }
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }
}
