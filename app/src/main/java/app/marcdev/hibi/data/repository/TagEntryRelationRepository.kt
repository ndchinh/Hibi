package app.marcdev.hibi.data.repository

import androidx.lifecycle.LiveData
import app.marcdev.hibi.data.entity.Entry
import app.marcdev.hibi.data.entity.TagEntryRelation

interface TagEntryRelationRepository {

  suspend fun addTagEntryRelation(tagEntryRelation: TagEntryRelation)

  suspend fun getEntriesWithTag(tag: String): LiveData<List<Entry>>

  suspend fun getTagsWithEntry(entryId: Int): List<String>

  suspend fun deleteTagEntryRelation(tag: String, entryId: Int)

  suspend fun deleteTagEntryRelationByTagId(tag: String)

  suspend fun deleteTagEntryRelationByEntryId(entryId: Int)
}