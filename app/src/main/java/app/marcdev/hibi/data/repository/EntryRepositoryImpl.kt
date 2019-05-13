package app.marcdev.hibi.data.repository

import android.database.sqlite.SQLiteConstraintException
import app.marcdev.hibi.data.database.DAO
import app.marcdev.hibi.data.entity.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class EntryRepositoryImpl private constructor(private val dao: DAO) : EntryRepository {

  override suspend fun addEntry(entry: Entry) {
    withContext(Dispatchers.IO) {
      try {
        Timber.d("Log: addEntry: Entry doesn't exist, adding new")
        dao.insertEntry(entry)
      } catch(exception: SQLiteConstraintException) {
        Timber.d("Log: addEntry: Entry already exists, updating existing")
        dao.updateEntry(entry)
      }
    }
  }

  override suspend fun getAllEntries(): List<Entry> {
    return withContext(Dispatchers.IO) {
      return@withContext dao.getAllEntries()
    }
  }

  override suspend fun getEntry(id: Int): Entry {
    return withContext(Dispatchers.IO) {
      return@withContext dao.getEntry(id)
    }
  }

  override suspend fun deleteEntry(id: Int) {
    withContext(Dispatchers.IO) {
      dao.deleteEntry(id)
    }
  }

  override suspend fun getLastEntryId(): Int {
    return withContext(Dispatchers.IO) {
      return@withContext dao.getLastEntryId()
    }
  }

  override suspend fun getEntriesOnDate(year: Int, month: Int, day: Int): List<Entry> {
    return withContext(Dispatchers.IO) {
      return@withContext dao.getEntriesOnDate(year, month, day)
    }
  }

  companion object {
    @Volatile private var instance: EntryRepositoryImpl? = null

    fun getInstance(dao: DAO) =
      instance ?: synchronized(this) {
        instance ?: EntryRepositoryImpl(dao).also { instance = it }
      }
  }
}