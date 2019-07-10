/*
 * Copyright 2019 Marc Donald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marcdonald.hibi.screens.favouriteentries

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcdonald.hibi.data.entity.Entry
import com.marcdonald.hibi.data.repository.BookEntryRelationRepository
import com.marcdonald.hibi.data.repository.EntryRepository
import com.marcdonald.hibi.data.repository.TagEntryRelationRepository
import com.marcdonald.hibi.screens.mainentriesrecycler.BookEntryDisplayItem
import com.marcdonald.hibi.screens.mainentriesrecycler.MainEntriesDisplayItem
import com.marcdonald.hibi.screens.mainentriesrecycler.TagEntryDisplayItem
import kotlinx.coroutines.launch

class FavouriteEntriesViewModel(private val entryRepository: EntryRepository,
																private val tagEntryRelationRepository: TagEntryRelationRepository,
																private val bookEntryRelationRepository: BookEntryRelationRepository)
	: ViewModel() {

	private val _entries = MutableLiveData<List<MainEntriesDisplayItem>>()
	val entries: LiveData<List<MainEntriesDisplayItem>>
		get() = _entries

	private val _displayLoading = MutableLiveData<Boolean>()
	val displayLoading: LiveData<Boolean>
		get() = _displayLoading

	private val _displayNoResults = MutableLiveData<Boolean>()
	val displayNoResults: LiveData<Boolean>
		get() = _displayNoResults

	fun loadEntries() {
		viewModelScope.launch {
			_displayLoading.value = true
			getMainEntryDisplayItems()
			_displayLoading.value = false
		}
	}

	private suspend fun getMainEntryDisplayItems() {
		val favouriteEntries = entryRepository.getFavouriteEntries()
		val tagEntryDisplayItems = tagEntryRelationRepository.getTagEntryDisplayItems()
		val bookEntryDisplayItem = bookEntryRelationRepository.getBookEntryDisplayItems()
		_entries.value = combineData(favouriteEntries, tagEntryDisplayItems, bookEntryDisplayItem)
		_displayNoResults.value = entries.value == null || entries.value!!.isEmpty()
	}

	private fun combineData(entries: List<Entry>, tagEntryDisplayItems: List<TagEntryDisplayItem>, bookEntryDisplayItems: List<BookEntryDisplayItem>): List<MainEntriesDisplayItem> {
		val itemList = ArrayList<MainEntriesDisplayItem>()

		entries.forEach { entry ->
			val item = MainEntriesDisplayItem(entry, listOf(), listOf())
			val listOfTags = ArrayList<String>()
			val listOfBooks = ArrayList<String>()

			tagEntryDisplayItems.forEach { tagEntryDisplayItem ->
				if(tagEntryDisplayItem.entryId == entry.id) {
					listOfTags.add(tagEntryDisplayItem.tagName)
				}
			}

			bookEntryDisplayItems.forEach { bookEntryDisplayItem ->
				if(bookEntryDisplayItem.entryId == entry.id) {
					listOfBooks.add(bookEntryDisplayItem.bookName)
				}
			}

			item.tags = listOfTags
			item.books = listOfBooks
			itemList.add(item)
		}

		return addListHeaders(itemList)
	}

	private fun addListHeaders(allItems: MutableList<MainEntriesDisplayItem>): List<MainEntriesDisplayItem> {
		val listWithHeaders = mutableListOf<MainEntriesDisplayItem>()
		listWithHeaders.addAll(allItems)

		var lastMonth = 12
		var lastYear = 9999
		if(allItems.isNotEmpty()) {
			lastMonth = allItems.first().entry.month + 1
			lastYear = allItems.first().entry.year
		}

		val headersToAdd = mutableListOf<Pair<Int, MainEntriesDisplayItem>>()

		for(x in 0 until allItems.size) {
			if(((allItems[x].entry.month < lastMonth) && (allItems[x].entry.year == lastYear))
				 || (allItems[x].entry.month > lastMonth) && (allItems[x].entry.year < lastYear)
				 || (allItems[x].entry.year < lastYear)
			) {
				val header = Entry(0, allItems[x].entry.month, allItems[x].entry.year, 0, 0, "")
				val headerItem = MainEntriesDisplayItem(header, listOf(), listOf())
				lastMonth = allItems[x].entry.month
				lastYear = allItems[x].entry.year
				headersToAdd.add(Pair(x, headerItem))
			}
		}

		for((add, x) in (0 until headersToAdd.size).withIndex()) {
			listWithHeaders.add(headersToAdd[x].first + add, headersToAdd[x].second)
		}

		return listWithHeaders
	}

	fun removeSelectedItemsFromFavourites(selectedIds: List<Int>) {
		viewModelScope.launch {
			selectedIds.forEach { id ->
				entryRepository.setEntryIsFavourite(id, false)
			}
			getMainEntryDisplayItems()
		}
	}
}