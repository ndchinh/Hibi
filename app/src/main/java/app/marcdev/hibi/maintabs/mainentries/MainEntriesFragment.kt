package app.marcdev.hibi.maintabs.mainentries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.marcdev.hibi.R
import app.marcdev.hibi.internal.PREF_ENTRY_DIVIDERS
import app.marcdev.hibi.internal.base.BinaryOptionDialog
import app.marcdev.hibi.maintabs.mainentriesrecycler.EntriesRecyclerAdapter
import app.marcdev.hibi.maintabs.mainentriesrecycler.MainEntriesHeaderItemDecoration
import app.marcdev.hibi.uicomponents.TextInputDialog
import app.marcdev.hibi.uicomponents.multiselectdialog.MultiSelectMenu
import app.marcdev.hibi.uicomponents.multiselectdialog.addtagtomultientrydialog.AddTagToMultiEntryDialog
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import timber.log.Timber

class MainEntriesFragment : Fragment(), KodeinAware {
  override val kodein by closestKodein()

  // <editor-fold desc="View Model">
  private val viewModelFactory: MainEntriesViewModelFactory by instance()
  private lateinit var viewModel: MainEntriesViewModel
  // </editor-fold>

  // <editor-fold desc="UI Components">
  private lateinit var loadingDisplay: ConstraintLayout
  private lateinit var noResults: ConstraintLayout
  private lateinit var recyclerAdapter: EntriesRecyclerAdapter
  // </editor-fold>

  override fun onCreate(savedInstanceState: Bundle?) {
    Timber.v("Log: onCreate: Started")
    super.onCreate(savedInstanceState)
    viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainEntriesViewModel::class.java)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_main_entries, container, false)
    bindViews(view)
    initRecycler(view)
    setupObservers()
    viewModel.loadEntries()
    return view
  }

  private fun bindViews(view: View) {
    loadingDisplay = view.findViewById(R.id.const_entries_loading)
    loadingDisplay.visibility = View.GONE

    noResults = view.findViewById(R.id.const_no_entries)
    noResults.visibility = View.GONE
  }

  private fun initRecycler(view: View) {
    val recycler: RecyclerView = view.findViewById(R.id.recycler_entries)
    this.recyclerAdapter = EntriesRecyclerAdapter(requireContext(), true, onSelectClick, requireActivity().theme)
    val layoutManager = LinearLayoutManager(context)
    recycler.adapter = recyclerAdapter
    recycler.layoutManager = layoutManager

    val includeEntryDividers = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean(PREF_ENTRY_DIVIDERS, true)
    if(includeEntryDividers) {
      val dividerItemDecoration = DividerItemDecoration(recycler.context, layoutManager.orientation)
      recycler.addItemDecoration(dividerItemDecoration)
    }
    val decoration = MainEntriesHeaderItemDecoration(recycler, recyclerAdapter)
    recycler.addItemDecoration(decoration)
  }

  private fun setupObservers() {
    viewModel.displayLoading.observe(this, Observer { value ->
      value?.let { show ->
        loadingDisplay.visibility = if(show) View.VISIBLE else View.GONE
      }
    })

    viewModel.displayNoResults.observe(this, Observer { value ->
      value?.let { show ->
        noResults.visibility = if(show) View.VISIBLE else View.GONE
      }
    })

    viewModel.entries.observe(this, Observer { items ->
      items?.let {
        recyclerAdapter.updateList(items)
      }
    })
  }

  private val onSelectClick = View.OnClickListener {
    val menu = MultiSelectMenu(onMultiSelectMenuItemSelected)
    menu.show(requireFragmentManager(), "Select Menu")
  }

  private val onMultiSelectMenuItemSelected = object : MultiSelectMenu.ItemSelectedListener {
    override fun itemSelected(item: Int) {
      when(item) {
        MultiSelectMenu.TAG -> initMultiTagDialog()
        MultiSelectMenu.BOOK -> initMultiBookDialog()
        MultiSelectMenu.LOCATION -> initMultiLocationSetDialog()
        MultiSelectMenu.DELETE -> initMultiDeleteDialog()
      }
    }
  }

  private fun initMultiTagDialog() {
    val dialog = AddTagToMultiEntryDialog(recyclerAdapter.getSelectedEntryIds().size, ::onSaveTagsToMultiEntryClick)
    dialog.show(requireFragmentManager(), "Set Multi Entry Tags")
  }

  private fun onSaveTagsToMultiEntryClick(deleteMode: Boolean, tagIds: List<Int>) {
    viewModel.addTagsToSelectedEntries(deleteMode, tagIds, recyclerAdapter.getSelectedEntryIds())
  }

  private fun initMultiBookDialog() {
    // TODO
  }

  private fun initMultiLocationSetDialog() {
    val locationDialog = TextInputDialog()
    val selectedAmount = recyclerAdapter.getSelectedEntryIds().size
    locationDialog.setTitle(resources.getQuantityString(R.plurals.multi_location_title, selectedAmount, selectedAmount))
    locationDialog.setHint(resources.getString(R.string.location))
    locationDialog.setDeleteClickListener(View.OnClickListener {
      viewModel.addLocationToSelectedEntries("", recyclerAdapter.getSelectedEntryIds())
      locationDialog.dismiss()
    })
    locationDialog.setSaveClickListener(object : TextInputDialog.TextInputDialogSaveListener {
      override fun onSave(text: String) {
        viewModel.addLocationToSelectedEntries(text, recyclerAdapter.getSelectedEntryIds())
        locationDialog.dismiss()
      }
    })
    locationDialog.show(requireFragmentManager(), "Set Multi Location Dialog")
  }

  private fun initMultiDeleteDialog() {
    val deleteConfirmDialog = BinaryOptionDialog()
    val selectedAmount = recyclerAdapter.getSelectedEntryIds().size
    deleteConfirmDialog.setTitle(resources.getQuantityString(R.plurals.multi_delete_title, selectedAmount, selectedAmount))
    deleteConfirmDialog.setMessage(resources.getQuantityString(R.plurals.multi_delete_message, selectedAmount, selectedAmount))
    deleteConfirmDialog.setNegativeButton(resources.getString(R.string.delete), View.OnClickListener {
      viewModel.deleteSelectedEntries(recyclerAdapter.getSelectedEntryIds())
      deleteConfirmDialog.dismiss()
    })
    deleteConfirmDialog.setPositiveButton(resources.getString(R.string.cancel), View.OnClickListener { deleteConfirmDialog.dismiss() })
    deleteConfirmDialog.show(requireFragmentManager(), "Confirm Multi Delete Dialog")
  }
}