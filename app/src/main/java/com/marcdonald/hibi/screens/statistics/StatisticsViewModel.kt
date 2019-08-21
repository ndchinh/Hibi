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
package com.marcdonald.hibi.screens.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcdonald.hibi.data.database.NumberAndDateObject
import com.marcdonald.hibi.data.database.NumberAndIdObject
import com.marcdonald.hibi.data.repository.BookEntryRelationRepository
import com.marcdonald.hibi.data.repository.EntryRepository
import com.marcdonald.hibi.data.repository.NewWordRepository
import com.marcdonald.hibi.data.repository.TagEntryRelationRepository
import kotlinx.coroutines.launch

class StatisticsViewModel(private val entryRepository: EntryRepository,
													private val tagEntryRelationRepository: TagEntryRelationRepository,
													private val bookEntryRelationRepository: BookEntryRelationRepository,
													private val newWordRepository: NewWordRepository)
	: ViewModel() {

	init {
		getMostNewWordsInOneDay()
		getMostNewWordsInOneEntry()
		getMostEntriesInOneDay()
	}

	val totalEntries: LiveData<Int>
		get() = entryRepository.entryCount

	val totalFavourites: LiveData<Int>
		get() = entryRepository.favouritesCount

	val totalDays: LiveData<Int>
		get() = entryRepository.dayCount

	val totalLocations: LiveData<Int>
		get() = entryRepository.locationCount

	val totalTaggedEntries: LiveData<Int>
		get() = tagEntryRelationRepository.taggedEntriesCount

	val totalEntriesInBooks: LiveData<Int>
		get() = bookEntryRelationRepository.entriesInBooksCount

	val totalNewWords: LiveData<Int>
		get() = newWordRepository.newWordCount

	private val _mostNewWordsInOneDay = MutableLiveData<NumberAndDateObject>()
	val mostNewWordsInOneDay: LiveData<NumberAndDateObject>
		get() = _mostNewWordsInOneDay

	private val _mostNewWordsInOneEntry = MutableLiveData<NumberAndIdObject>()
	val mostNewWordsInOneEntry: LiveData<NumberAndIdObject>
		get() = _mostNewWordsInOneEntry


	private val _mostEntriesInOneDay = MutableLiveData<NumberAndDateObject>()
	val mostEntriesInOneDay: LiveData<NumberAndDateObject>
		get() = _mostEntriesInOneDay

	private fun getMostNewWordsInOneDay() {
		viewModelScope.launch {
			val mostNewWords = newWordRepository.getMostNewWordsInOneDay()
			_mostNewWordsInOneDay.postValue(mostNewWords)
		}
	}

	private fun getMostNewWordsInOneEntry() {
		viewModelScope.launch {
			val mostNewWords = newWordRepository.getMostNewWordsInOneEntry()
			_mostNewWordsInOneEntry.postValue(mostNewWords)
		}
	}

	private fun getMostEntriesInOneDay() {
		viewModelScope.launch {
			val mostEntries = entryRepository.getMostEntriesInOneDay()
			_mostEntriesInOneDay.postValue(mostEntries)
		}
	}
}