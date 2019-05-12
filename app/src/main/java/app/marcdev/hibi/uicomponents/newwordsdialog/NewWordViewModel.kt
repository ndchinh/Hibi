package app.marcdev.hibi.uicomponents.newwordsdialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.marcdev.hibi.data.entity.NewWord
import app.marcdev.hibi.data.repository.NewWordRepository
import app.marcdev.hibi.entryscreens.addentryscreen.NewWordsToSaveToNewEntry
import timber.log.Timber

class NewWordViewModel(private val newWordRepository: NewWordRepository) : ViewModel() {
  private var _entryId = 0
  val entryId: Int
    get() = _entryId

  private val _displayAddButton = MutableLiveData<Boolean>()
  val displayAddButton: LiveData<Boolean>
    get() = _displayAddButton

  private val _allowEdits = MutableLiveData<Boolean>()
  val allowEdits: LiveData<Boolean>
    get() = _allowEdits

  private val _displayNoWords = MutableLiveData<Boolean>()
  val displayNoWords: LiveData<Boolean>
    get() = _displayNoWords

  fun passArguments(entryIdArg: Int, isEditModeArg: Boolean) {
    _entryId = entryIdArg
    Timber.i("Log: passArguments: set entryId")
    _displayAddButton.value = isEditModeArg
    _allowEdits.value = isEditModeArg
  }

  fun listReceived(isEmpty: Boolean) {
    _displayNoWords.value = isEmpty
  }

  fun getNewWords(): LiveData<List<NewWord>> {
    return if(entryId != 0)
      newWordRepository.getNewWordsByEntryId(entryId)
    else
      NewWordsToSaveToNewEntry.list
  }
}