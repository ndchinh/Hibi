package com.marcdonald.hibi.data.repository

import androidx.lifecycle.LiveData
import com.marcdonald.hibi.data.entity.Tag

interface TagRepository {

  suspend fun addTag(tag: Tag)

  suspend fun deleteTag(tagId: Int)

  suspend fun isTagInUse(tag: String): Boolean

  suspend fun getTagName(tagId: Int): String

  fun getAllTagsLD(): LiveData<List<Tag>>

  suspend fun getAllTags(): List<Tag>
}