package com.example.kitabu.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isAvailable = :isAvailable ORDER BY title ASC")
    fun getBooksByAvailability(isAvailable: Boolean): Flow<List<BookEntity>>

    @Query(
        "SELECT * FROM books " +
                "WHERE title LIKE '%' || :query || '%' " +
                "OR author LIKE '%' || :query || '%' " +
                "ORDER BY title ASC"
    )
    fun searchBooks(query: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE bookId = :bookId LIMIT 1")
    suspend fun getBookById(bookId: Int): BookEntity?
}
