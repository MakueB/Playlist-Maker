package com.example.playlistmaker.createplaylist.ui

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CreatePlaylistFragment : Fragment() {
    private val createPlstViewModel by viewModel<CreatePlaylistViewModel>()

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding: FragmentCreatePlaylistBinding get() = _binding!!

    private lateinit var contentResolver: ContentResolver
    private var uri: Uri? = null

    private val photoPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { pickedUri: Uri? ->
        if (pickedUri != null) {
            val savedUri = saveImageToPrivateStorage(pickedUri) // сохраняем и получаем новый Uri
            binding.playlistImage.setImageURI(savedUri)
            createPlstViewModel.updateImageUri(savedUri)
            binding.placeholder.isVisible = false
        } else {
            binding.placeholder.isVisible = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        contentResolver = requireContext().contentResolver
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupListeners()

        binding.apply {
            createPlstViewModel.isSaveButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
                createButton.isEnabled = isEnabled
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }
            })
    }

    private fun setupListeners() {
        binding.apply {
            playlistNameEditText.requestFocus()
            playlistNameEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (playlistNameEditText.text.isNullOrEmpty()) {
                        playlistNameEditText.hint = getString(R.string.playlist_name_hint)
                    } else {
                        playlistNameEditText.hint = ""
                    }
                }
            }

            playlistNameEditText.addTextChangedListener {
                createPlstViewModel.updatePlaylistName(it.toString())
            }

            playlistDescriptionEditText.addTextChangedListener {
                createPlstViewModel.updatePlaylistDescription(it.toString())
            }

            playlistImage.setOnClickListener {
                openGallery()
            }

            createButton.setOnClickListener {
                uri?.let { saveImageToPrivateStorage(it) }
                createPlstViewModel.savePlaylist()
                findNavController().navigateUp()
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            handleBackPressed()
        }
    }

    private fun handleBackPressed() {
        if (hasUnsavedChanges()) {
            showExitConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun openGallery() {
        photoPicker.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    open fun saveImageToPrivateStorage(uri: Uri): Uri {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
            ?: throw IOException("Can't open input stream from URI")

        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "Images"
        ).apply { mkdirs() }

        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)

        BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        inputStream.close()
        outputStream.close()

        return file.toUri() // <--- ВОТ ЧТО ТЕБЕ НУЖНО СОХРАНИТЬ В Playlist.imageUrl
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.exit_creation_title))
            .setMessage(getString(R.string.exit_creation_message))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.finish)) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
    }

    private fun hasUnsavedChanges(): Boolean {
        return !createPlstViewModel.playlistName.value.isNullOrEmpty() ||
                !createPlstViewModel.playlistDescription.value.isNullOrEmpty() ||
                createPlstViewModel.imageUri.value != null
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }
            })
    }
}
