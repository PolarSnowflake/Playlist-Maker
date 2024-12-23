package com.example.playlist_maker.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentSearchBinding
import com.example.playlist_maker.domein.player.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchHistoryTracks: SearchHistoryTracks
    private val viewModel: SearchViewModel by viewModel()

    private var lastQuery: String = ""
    private var debounceJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter(emptyList()) { track ->  // Начинаем с пустого списка треков
            debounceJob?.cancel()
            debounceJob = lifecycleScope.launch {
                delay(300)
                viewModel.addTrackToHistory(track)
                searchHistoryTracks.hideHistory()
                startPlayerFragment(track)
            }
        }

        binding.recyclerView.adapter = trackAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchHistoryTracks = SearchHistoryTracks(
            requireContext(),
            binding.recyclerHistoryTracks,
            binding.historyClear,
            binding.searchHistoryHeader,
            viewModel
        )

        setupObservers()
        setupSearchBar()
    }

    // Настройка событий для поиска
    private fun setupSearchBar() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (s.isNullOrEmpty()) {
                    debounceJob?.cancel() // Отмена debounce
                    binding.progressBarLayout.visibility = View.GONE
                    trackAdapter.updateTracks(emptyList())
                    binding.recyclerView.visibility = View.GONE
                    hideErrorPlaceholder()
                    hideUpdateButton()
                    if (binding.searchBar.hasFocus()) {
                        searchHistoryTracks.loadSearchHistory() // Показываем историю, если есть
                    }
                } else {
                    searchHistoryTracks.hideHistory() // Скрываем историю при вводе текста
                    debounceSearch(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    // Дополнительная проверка в afterTextChanged для обработки случая длительного нажатия кнопки "Стереть"
                    debounceJob?.cancel()
                    binding.progressBarLayout.visibility = View.GONE
                    trackAdapter.updateTracks(emptyList())
                    binding.recyclerView.visibility = View.GONE
                    hideErrorPlaceholder()
                    hideUpdateButton()
                    if (binding.searchBar.hasFocus()) {
                        searchHistoryTracks.loadSearchHistory()
                    }
                }
            }
        })

        // Отслеживание фокуса
        binding.searchBar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchBar.text.isEmpty()) {
                searchHistoryTracks.loadSearchHistory()
                binding.recyclerView.visibility = View.GONE
            } else {
                searchHistoryTracks.hideHistory()
            }
        }

        // Кнопка "Готово" на клавиатуре
        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                debounceSearch(binding.searchBar.text.toString())
                hideKeyboard()
                searchHistoryTracks.hideHistory()
                true
            } else {
                false
            }
        }

        // Кнопка "Очистить поисковый запрос"
        binding.clearButton.setOnClickListener {
            binding.searchBar.text.clear()
            hideKeyboard()
            binding.recyclerView.visibility = View.GONE
            trackAdapter.updateTracks(emptyList())
            hideErrorPlaceholder()
            hideUpdateButton()
            searchHistoryTracks.loadSearchHistory() // Загрузка истории после очистки текста
        }

        // Кнопка "Обновить"
        binding.updateButton.setOnClickListener {
            retryLastSearch()
        }
    }

    private fun setupObservers() {
        // Изменение списка треков
        viewModel.searchResults.observe(viewLifecycleOwner) { tracks ->
            binding.progressBarLayout.visibility = View.GONE
            if (tracks.isNotEmpty()) {
                trackAdapter.updateTracks(tracks)  // Обновляем треки
                binding.recyclerView.visibility = View.VISIBLE
                hideErrorPlaceholder()
            } else {
                showNoResultsPlaceholder()  // Показываем плейсхолдер, если результаты пусты
            }
        }

        // Изменение состояния загрузки
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Ошибка
        viewModel.error.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                showErrorPlaceholder()
            } else {
                if (viewModel.searchResults.value.isNullOrEmpty()) {
                    showNoResultsPlaceholder()
                }
            }
        }
    }

    // Выполнение поискового запроса с задержкой (debounce)
    private fun debounceSearch(query: String) {
        debounceJob?.cancel()  // Отменяем предыдущий Job
        debounceJob = lifecycleScope.launch {
            delay(2000L)
            binding.progressBarLayout.visibility = View.VISIBLE
            performSearch(query)
        }
    }

    // Выполнение поискового запроса
    private fun performSearch(query: String) {
        lastQuery = query
        searchHistoryTracks.hideHistory()
        hideErrorPlaceholder()
        hideUpdateButton()
        binding.recyclerView.visibility = View.GONE
        binding.progressBarLayout.visibility = View.VISIBLE
        viewModel.searchTracks(query)
    }

    // Повтор последнего неудавшегося запроса
    private fun retryLastSearch() {
        performSearch(lastQuery)
    }

    // Плейсхолдер "Ничего не найдено"
    private fun showNoResultsPlaceholder() {
        trackAdapter.updateTracks(emptyList())
        binding.recyclerView.visibility = View.GONE
        binding.errorPlaceholderLayout.visibility = View.VISIBLE
        binding.placeholderError.visibility = View.VISIBLE
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.updateButtonLayout.visibility = View.GONE
        binding.placeholderMessage.text = getString(R.string.nothing_found)
        binding.placeholderError.setImageResource(R.drawable.not_found_icon)
        searchHistoryTracks.hideHistory()
    }

    // Плейсхолдер "Ошибка подключения"
    private fun showErrorPlaceholder() {
        trackAdapter.updateTracks(emptyList())
        binding.recyclerView.visibility = View.GONE
        binding.errorPlaceholderLayout.visibility = View.VISIBLE
        binding.placeholderError.visibility = View.VISIBLE
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.updateButtonLayout.visibility = View.VISIBLE
        binding.placeholderMessage.text = getString(R.string.no_int)
        binding.placeholderError.setImageResource(R.drawable.no_int_icon)
        searchHistoryTracks.hideHistory()
    }

    // Скрытие плейсхолдера ошибки
    private fun hideErrorPlaceholder() {
        binding.errorPlaceholderLayout.visibility = View.GONE
    }

    // Скрытие кнопки "Обновить"
    private fun hideUpdateButton() {
        binding.updateButtonLayout.visibility = View.GONE
    }

    // Скрытие клавиатуры
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.searchBar.windowToken, 0)
    }

    // Переход на PlayerFragment
    private fun startPlayerFragment(track: Track) {
        val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}