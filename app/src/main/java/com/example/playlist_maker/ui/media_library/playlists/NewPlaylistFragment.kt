package com.example.playlist_maker.ui.media_library.playlists

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentNewPlaylistBinding
import com.example.playlist_maker.domein.playlist.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()
    private var selectedImageUri: Uri? = null

    private val args: NewPlaylistFragmentArgs by navArgs()

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

        val playlistId = arguments?.getLong("playlistId", 0L) ?: 0L
        val name = arguments?.getString("name")
        val description = arguments?.getString("description")
        val coverPath = arguments?.getString("coverPath")

        handleIncomingPlaylist(playlistId, name, description, coverPath)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPress()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun handleIncomingPlaylist(
        playlistId: Long,
        name: String?,
        description: String?,
        coverPath: String?
    ) {
        if (playlistId == 0L) {
            // Новый плейлист
            binding.btnBack.title = getString(R.string.new_playlist)
            binding.btnCreatePlaylist.text = getString(R.string.create_playlist)
        } else {
            // Редактирование
            binding.btnBack.title = getString(R.string.edit_playlist)
            binding.btnCreatePlaylist.text = getString(R.string.save)

            // Загрузка плейлиста из базы
            viewModel.getPlaylistById(playlistId).observe(viewLifecycleOwner) { playlist ->
                if (playlist != null) {
                    Log.d("NewPlaylistFragment", "Loaded playlist for editing: $playlist")

                    binding.ietPlaylistName.setText(playlist.name)
                    binding.ietDesctiption.setText(playlist.description)

                    val coverUrl = playlist.coverPath
                    Glide.with(this)
                        .load(coverUrl.takeIf { it.isNotEmpty() } ?: R.drawable.placeholder_player)
                        .into(binding.ivNewPlaylistImage)

                    // Сохранение текущего плейлиста
                    selectedImageUri = Uri.parse(coverUrl.takeIf { it.isNotEmpty() } ?: "")
                } else {
                    Log.e("NewPlaylistFragment", "Failed to load playlist with ID: $playlistId")
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_loading_playlist),
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
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
            val playlistId = args.playlistId
            if (playlistId != 0L) {
                // Загрузка текущего плейлиста из базы
                viewModel.getPlaylistById(playlistId).observe(viewLifecycleOwner) { playlist ->
                    if (playlist != null) {
                        Log.d("NewPlaylistFragment", "Loaded playlist for update: $playlist")

                        val updatedPlaylist = playlist.copy(
                            name = binding.ietPlaylistName.text.toString(),
                            description = binding.ietDesctiption.text.toString(),
                            coverPath = selectedImageUri?.toString() ?: playlist.coverPath,
                            trackIds = playlist.trackIds, // Сохраняем текущие треки
                            trackCount = playlist.trackIds.size
                        )

                        Log.d("NewPlaylistFragment", "Updating playlist: $updatedPlaylist")
                        viewModel.updatePlaylist(updatedPlaylist)

                        Toast.makeText(
                            requireContext(),
                            getString(R.string.playlist_updated, updatedPlaylist.name),
                            Toast.LENGTH_SHORT
                        ).show()

                        findNavController().popBackStack()
                    } else {
                        Log.e("NewPlaylistFragment", "Failed to load playlist for ID: $playlistId")
                    }
                }
            } else {
                Log.d("NewPlaylistFragment", "Creating new playlist")
                createPlaylist()
            }
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
        parentFragmentManager.popBackStack()
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

    @SuppressLint("StringFormatInvalid")
    private fun createPlaylist() {
        val name = binding.ietPlaylistName.text?.toString()?.trim() ?: ""
        val description = binding.ietDesctiption.text?.toString() ?: ""
        val savedImageUri = saveImageToAppStorage(selectedImageUri)

        Log.d(
            "NewPlaylistFragment",
            "New playlist details: name=$name, description=$description, coverPath=$savedImageUri"
        )
        viewModel.createPlaylist(name, description, savedImageUri)
        val bundle = Bundle().apply {
            putBoolean("isPlaylistCreated", true)
        }
        parentFragmentManager.setFragmentResult("playlist_result", bundle)
        findNavController().navigateUp()
    }

    @SuppressLint("StringFormatInvalid")
    private fun updatePlaylist(originalPlaylist: Playlist) {
        // Сохранение текущих треков
        val updatedPlaylist = originalPlaylist.copy(
            name = binding.ietPlaylistName.text.toString(),
            description = binding.ietDesctiption.text.toString(),
            coverPath = selectedImageUri?.toString() ?: originalPlaylist.coverPath,
            trackIds = originalPlaylist.trackIds,
            trackCount = originalPlaylist.trackIds.size
        )

        Log.d("NewPlaylistFragment", "Updating playlist: $updatedPlaylist")

        viewModel.updatePlaylist(updatedPlaylist)

        Toast.makeText(
            requireContext(),
            getString(R.string.playlist_updated, updatedPlaylist.name),
            Toast.LENGTH_SHORT
        ).show()

        findNavController().popBackStack()
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