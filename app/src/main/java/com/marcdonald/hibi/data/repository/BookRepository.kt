package com.marcdonald.hibi.data.repository

import androidx.lifecycle.LiveData
import com.marcdonald.hibi.data.entity.Book

interface BookRepository {

  suspend fun addBook(book: Book)

  suspend fun deleteBook(bookId: Int)

  suspend fun isBookNameInUse(name: String): Boolean

  suspend fun getBookName(bookId: Int): String

  fun getAllBooksLD(): LiveData<List<Book>>

  suspend fun getAllBooks(): List<Book>
}