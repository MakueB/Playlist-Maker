package com.example.playlistmaker.createandeditplaylist.ui.edit

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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.playlistmaker.R
import com.example.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.details.ui.DetailsFragmentArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EditPlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    val binding: FragmentCreatePlaylistBinding
        get() = requireNotNull(_binding) { "Binding wasn't initialized!" }

    private val args by navArgs<DetailsFragmentArgs>()
    private var playlist: Playlist? = null

    private val editPlaylistViewModel by viewModel<EditPlaylistViewModel>(
        parameters = { parametersOf(playlist) }
    )

    private var uri: Uri? = null

    private val photoPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { pickedUri: Uri? ->
        if (pickedUri != null) {
            val savedUri = saveImageToPrivateStorage(pickedUri)
            binding.playlistImage.setImageURI(savedUri)
            editPlaylistViewModel.updateImageUri(savedUri)
            binding.placeholder.isVisible = false
        } else {
            binding.placeholder.isVisible = true
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = args.playlist

        setupToolbar()
        setupListeners()
        setupUi()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })
    }

    private fun setupUi() {
        binding.apply {
            playlistNameEditText.setText(editPlaylistViewModel.playlistName.value)
            playlistDescriptionEditText.setText(editPlaylistViewModel.playlistDescription.value)
            playlistImage.setImageURI(editPlaylistViewModel.imageUri.value)
            placeholder.isVisible = editPlaylistViewModel.imageUri.value == null
            createButton.text = getString(R.string.save)
        }
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
                editPlaylistViewModel.updatePlaylistName(it.toString())
            }

            playlistDescriptionEditText.addTextChangedListener {
                editPlaylistViewModel.updatePlaylistDescription(it.toString())
            }

            playlistImage.setOnClickListener {
                openGallery()
            }

            createButton.setOnClickListener {
                uri?.let { saveImageToPrivateStorage(it) }

                val updatedPlaylist = editPlaylistViewModel.updateAndGetPlaylist()

                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.detailsFragment, true)
                    .build()

                val action = EditPlaylistFragmentDirections.actionEditPlaylistFragmentToDetailsFragment(updatedPlaylist)

                findNavController().navigate(action, navOptions)
            }

            editPlaylistViewModel.isSaveButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
                createButton.isEnabled = isEnabled
            }
        }
    }

    private fun openGallery() {
        photoPicker.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    private fun saveImageToPrivateStorage(uri: Uri): Uri {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
            ?: throw IOException("Can't open input stream from URI")

        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "Images"
        ).apply { mkdirs() }

        val file = File(filePath, getString(R.string.cover_file_name_template, System.currentTimeMillis()))
        val outputStream = FileOutputStream(file)

        BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        inputStream.close()
        outputStream.close()

        return file.toUri()
    }


    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}