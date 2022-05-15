package com.github.libretube

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.libretube.adapters.SearchAdapter
import com.github.libretube.adapters.SearchHistoryAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SearchFragment : Fragment() {
    private val TAG = "SearchFragment"
    private var selectedFilter = 0
    private var nextPage : String? = null
    private lateinit var searchRecView : RecyclerView
    private var searchAdapter : SearchAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchRecView = view.findViewById<RecyclerView>(R.id.search_recycler)

        val autoTextView = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)

        val historyRecycler = view.findViewById<RecyclerView>(R.id.history_recycler)

        val filterImageView = view.findViewById<ImageView>(R.id.filterMenu_imageView)

        var tempSelectedItem = 0

        filterImageView.setOnClickListener {
            val options = arrayOf(getString(R.string.all), getString(R.string.videos), getString(R.string.channels), getString(R.string.playlists))
            AlertDialog.Builder(view.context)
                .setTitle(getString(R.string.choose_filter))
                .setSingleChoiceItems(options, selectedFilter, DialogInterface.OnClickListener {
                        _, id -> tempSelectedItem = id
                })
                .setPositiveButton(getString(R.string.okay), DialogInterface.OnClickListener {
                        _, _ -> selectedFilter = tempSelectedItem
                        fetchSearch(autoTextView.text.toString())
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show()
        }

        //show search history

        searchRecView.visibility = GONE
        historyRecycler.visibility = VISIBLE

        historyRecycler.layoutManager = LinearLayoutManager(view.context)

        var historylist = getHistory()
        if (historylist.size != 0) {
            historyRecycler.adapter =
                SearchHistoryAdapter(requireContext(), historylist, autoTextView)
        }

        searchRecView.layoutManager = GridLayoutManager(view.context, 1)
        autoTextView.requestFocus()
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm!!.showSoftInput(autoTextView, InputMethodManager.SHOW_IMPLICIT)
        autoTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!! != "") {
                    searchRecView.visibility = VISIBLE
                    historyRecycler.visibility = GONE
                    searchRecView.adapter = null

                    searchRecView.viewTreeObserver
                        .addOnScrollChangedListener {
                            if (!searchRecView.canScrollVertically(1)) {
                                fetchNextSearchItems(autoTextView.text.toString())
                            }

                        }

                    GlobalScope.launch {
                        fetchSuggestions(s.toString(), autoTextView)
                        delay(1000)
                        addtohistory(s.toString())
                        fetchSearch(s.toString())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.isEmpty()) {
                    searchRecView.visibility = GONE
                    historyRecycler.visibility = VISIBLE
                    var historylist = getHistory()
                    if (historylist.size != 0) {
                        historyRecycler.adapter =
                            SearchHistoryAdapter(requireContext(), historylist, autoTextView)
                    }
                }
            }

        })
        autoTextView.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard();
                autoTextView.dismissDropDown();
                return@OnEditorActionListener true
            }
            false
        })
        autoTextView.setOnItemClickListener { _, _, _, _ ->
            hideKeyboard()
        }
    }

    private fun fetchSuggestions(query: String, autoTextView: AutoCompleteTextView){
        lifecycleScope.launchWhenCreated {
            val response = try {
                RetrofitInstance.api.getSuggestions(query)
            } catch (e: IOException) {
                println(e)
                Log.e(TAG, "IOException, you might not have internet connection")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response")
                return@launchWhenCreated
            }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, response)
            autoTextView.setAdapter(adapter)
        }
    }
    private fun fetchSearch(query: String){
        lifecycleScope.launchWhenCreated {
            val response = try {
                RetrofitInstance.api.getSearchResults(query, "videos")
            } catch (e: IOException) {
                println(e)
                Log.e(TAG, "IOException, you might not have internet connection $e")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response")
                return@launchWhenCreated
            }
            nextPage = response.nextpage
            if(response.items!!.isNotEmpty()){
               runOnUiThread {
                   searchAdapter = SearchAdapter(response.items, selectedFilter)
                   searchRecView.adapter = searchAdapter
               }
            }

        }
    }

    private fun fetchNextSearchItems(query: String){
        lifecycleScope.launchWhenCreated {
                val response = try {
                    RetrofitInstance.api.getSearchResultsNextPage(query!!, "videos", nextPage!!)
                } catch (e: IOException) {
                    println(e)
                    Log.e(TAG, "IOException, you might not have internet connection")
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Log.e(TAG, "HttpException, unexpected response," + e.response())
                    return@launchWhenCreated
                }
                nextPage = response.nextpage
                searchAdapter?.updateItems(response.items!!)
            }
        }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    private fun addtohistory(query: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        var historyList = getHistory()


        if (historyList.size != 0 && query == historyList.get(historyList.size - 1)) {
            return
        } else if (query == "") {
            return
        } else {
            historyList = historyList + query

        }



        if (historyList.size > 10) {
            historyList = historyList.takeLast(10)
        }

        var set: Set<String> = HashSet(historyList)

        sharedPreferences.edit().putStringSet("search_history", set)
            .apply()
    }

    private fun getHistory(): List<String> {
        try {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val set: Set<String> = sharedPreferences.getStringSet("search_history", HashSet())!!
            return set.toList()
        } catch (e: Exception) {
            return emptyList()
        }

    }
}

