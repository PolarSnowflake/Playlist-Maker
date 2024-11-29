package com.example.playlist_maker.ui.media_library.playlists

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

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
        setTextInputLayoutColors()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPress()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            handleBackPress()
        }
        binding.ivNewPlaylistImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.ietPlaylistName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateCreateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {
                updateCreateButtonState()
            }
        })

        binding.btnCreatePlaylist.setOnClickListener {
            val name = binding.ietPlaylistName.text?.toString()?.trim() ?: ""
            if (name.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Название плейлиста не может быть пустым",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val description = binding.ietDesctiption.text?.toString() ?: ""
            val savedImageUri = saveImageToAppStorage(selectedImageUri)

            viewModel.createPlaylist(name, description, savedImageUri)

            Toast.makeText(requireContext(), "Плейлист \"$name\" создан", Toast.LENGTH_SHORT).show()

            parentFragmentManager.setFragmentResult(
                "playlist_result",
                Bundle().apply { putBoolean("isPlaylistCreated", true) }
            )

            parentFragmentManager.popBackStack()
        }

        updateCreateButtonState()
    }

    private fun updateCreateButtonState() {
        val playlistName = binding.ietPlaylistName.text?.toString()?.trim() ?: ""
        val isEnabled = playlistName.isNotEmpty()
        binding.btnCreatePlaylist.isEnabled = isEnabled
        val buttonColor = if (isEnabled) {
            ContextCompat.getColor(requireContext(), R.color.blue)
        } else {
            ContextCompat.getColor(requireContext(), R.color.gray)
        }

        binding.btnCreatePlaylist.backgroundTintList = ColorStateList.valueOf(buttonColor)
    }

    private fun handleBackPress() {
        if (hasUnsavedChanges()) {
            showExitDialog()
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        val isNameFilled = !binding.ietPlaylistName.text.isNullOrBlank()
        val isDescriptionFilled = !binding.ietDesctiption.text.isNullOrBlank()
        val isImageSelected = selectedImageUri != null

        return isNameFilled || isDescriptionFilled || isImageSelected
    }

    private fun showExitDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.exit_creation_qw))
            .setMessage(getString(R.string.exit_creation_message))
            .setPositiveButton(getString(R.string.finish)) { _, _ ->
                parentFragmentManager.popBackStack()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun saveImageToAppStorage(imageUri: Uri?): Uri? {
        if (imageUri == null) return null

        val imageFolder = File(requireContext().filesDir, "ImageFolder")
        if (!imageFolder.exists()) {
            imageFolder.mkdir()
        }

        val imageFile = File(imageFolder, "${System.currentTimeMillis()}.jpg")
        return try {
            requireContext().contentResolver.openInputStream(imageUri).use { inputStream ->
                imageFile.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
            Uri.fromFile(imageFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun setTextInputLayoutColors() {
        val defaultStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.textColorHint_npl_no_focus)
        val focusedStrokeColor =
            ContextCompat.getColor(requireContext(), R.color.textColorHint_npl_focus)

        binding.playlistNameInputLayout.setBoxStrokeColorStateList(
            ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_focused)
                ),
                intArrayOf(defaultStrokeColor, focusedStrokeColor)
            )
        )

        binding.descriptionInputLayout.setBoxStrokeColorStateList(
            ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_focused)
                ),
                intArrayOf(defaultStrokeColor, focusedStrokeColor)
            )
        )
    }
}