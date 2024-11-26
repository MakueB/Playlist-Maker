package com.example.playlistmaker.newplaylist.ui

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {
    private val newPlstViewModel by viewModel<NewPlaylistViewModel>()
    private val playerViewModel by viewModel<PlayerViewModel>()

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding: FragmentNewPlaylistBinding get() = _binding!!

    //private val requester = PermissionRequester.instance()
    private lateinit var contentResolver: ContentResolver
    private var uri: Uri? = null

    private val readImagesPermission: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

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

//    private fun openGallery() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            requester.request(
//                readImagesPermission
//            ).collect { result ->
//                when (result) {
//                    is PermissionResult.Granted -> photoPicker.launch(
//                        PickVisualMediaRequest(
//                            ActivityResultContracts.PickVisualMedia.ImageOnly
//                        )
//                    )
//
//                    is PermissionResult.Denied.NeedsRationale -> openDialog(
//                        getString(R.string.rationale_title),
//                        ::openGallery
//                    )
//
//                    is PermissionResult.Denied.DeniedPermanently -> openDialog(
//                        getString(R.string.open_settings_request),
//                        ::openSettings
//                    )
//
//                    is PermissionResult.Cancelled -> Toast.makeText(
//                        requireContext(),
//                        getString(R.string.image_permission_denied),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }

    private fun openDialog(
        title: String,
        action: () -> Unit
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                action()
                dialog.dismiss()
            }
            .show()
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath = File(
            requireActivity().filesDir,
            getString(R.string.app_name)
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "${newPlstViewModel.playlistName.value}_cover.jpg")
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
