package com.example.kitabu.data.repository

import com.example.kitabu.data.local.BookDao
import com.example.kitabu.data.local.BookEntity
import kotlinx.coroutines.flow.Flow

class BookRepository(
    private val bookDao: BookDao
) {

    fun getAllBooks(): Flow<List<BookEntity>> {
        return bookDao.getAllBooks()
    }

    fun getBooksByAvailability(isAvailable: Boolean): Flow<List<BookEntity>> {
        return bookDao.getBooksByAvailability(isAvailable)
    }

    fun searchBooks(query: String): Flow<List<BookEntity>> {
        return bookDao.searchBooks(query)
    }

    suspend fun getBookById(bookId: Int): BookEntity? {
        return bookDao.getBookById(bookId)
    }

    suspend fun insertBook(book: BookEntity) {
        bookDao.insertBook(book)
    }

    suspend fun updateBook(book: BookEntity) {
        bookDao.updateBook(book)
    }

    suspend fun deleteBook(book: BookEntity) {
        bookDao.deleteBook(book)
    }
}
