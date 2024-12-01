package com.example.playlistmaker.newplaylist.ui

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
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {
    private val newPlstViewModel by viewModel<NewPlaylistViewModel>()

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding: FragmentNewPlaylistBinding get() = _binding!!

    //private val requester = PermissionRequester.instance()
    private lateinit var contentResolver: ContentResolver
    private var uri: Uri? = null


    private val photoPicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            binding.playlistImage.setImageURI(uri)
            this.uri = uri
            newPlstViewModel.updateImageUri(uri)
            binding.placeholder.isVisible = uri == null
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        contentResolver = requireContext().contentResolver
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupListeners()

        binding.apply {
            newPlstViewModel.isSaveButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
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
                newPlstViewModel.updatePlaylistName(it.toString())
            }

            playlistDescriptionEditText.addTextChangedListener {
                newPlstViewModel.updatePlaylistDescription(it.toString())
            }

            playlistImage.setOnClickListener {
                openGallery()
            }

            createButton.setOnClickListener {
                uri?.let { saveImageToPrivateStorage(it) }
                newPlstViewModel.savePlaylist()
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

    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), // Используем внешний каталог для изображений
            "Images"
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "${newPlstViewModel.playlistName.value}_cover_${System.currentTimeMillis()}.jpg")
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        inputStream?.close()
        outputStream.close()
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
        return !newPlstViewModel.playlistName.value.isNullOrEmpty() ||
                !newPlstViewModel.playlistDescription.value.isNullOrEmpty() ||
                newPlstViewModel.imageUri.value != null
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
