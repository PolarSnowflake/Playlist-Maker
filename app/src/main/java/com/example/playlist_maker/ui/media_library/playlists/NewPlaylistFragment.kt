package com.example.playlist_maker.ui.media_library.playlists

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.playlist_maker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.ivNewPlaylistImage.setImageURI(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        // Image selection
        binding.ivNewPlaylistImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.ietPlaylistName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isNameNotEmpty = !s.isNullOrEmpty()
                binding.btnCreatePlaylist.isEnabled = isNameNotEmpty
            }

            override fun afterTextChanged(s: Editable?) {
                binding.btnCreatePlaylist.isEnabled = !s?.toString().isNullOrEmpty()
            }
        })

        binding.btnCreatePlaylist.setOnClickListener {
            val name = binding.ietPlaylistName.text?.toString() ?: ""
            val description = binding.ietDesctiption.text?.toString() ?: ""
            viewModel.createPlaylist(name, description, selectedImageUri)

            Toast.makeText(requireContext(), "Плейлист \"$name\" создан", Toast.LENGTH_SHORT).show()
            (requireActivity() as? NewPlaylistListener)?.onPlaylistCreated()
            parentFragmentManager.popBackStack()
        }

        binding.btnBack.setNavigationOnClickListener {
            handleBackPress()
        }
    }

    private fun handleBackPress() {
        if (hasUnsavedChanges()) {
            showExitDialog()
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        return binding.ietPlaylistName.text?.isNotEmpty() == true ||
                binding.ietDesctiption.text?.isNotEmpty() == true ||
                selectedImageUri != null
    }

    private fun showExitDialog() {
        val dialog = ExitDialogFragment.newInstance()
        dialog.setPositiveClickListener {
            parentFragmentManager.popBackStack()
        }
        dialog.show(parentFragmentManager, "exit_dialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}