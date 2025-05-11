package com.example.playlistmaker.createandeditplaylist.ui.base

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.createandeditplaylist.ui.create.CreatePlaylistViewModel
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class BaseCreateEditPlaylistFragment : Fragment() {
    protected var _binding: FragmentCreatePlaylistBinding? = null
    protected val binding: FragmentCreatePlaylistBinding
        get() = requireNotNull(_binding) { "Binding wasn't initialized!" }

    protected abstract val viewModel: CreatePlaylistViewModel
    protected abstract fun onSubmitAction()
    open fun setupInitialUi() {}

    private val photoPicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { pickedUri: Uri? ->
            pickedUri?.let {
                viewModel.saveImageToPrivateStorage(pickedUri, requireContext().contentResolver)
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

        setupToolBar()
        setupListeners()
        setupInitialUi()
        observeViewModel()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }

            }
        )
    }

    private fun setupToolBar() {
        binding.toolbar.setNavigationOnClickListener {
            handleBackPressed()
        }
    }

    protected open fun handleBackPressed() {
        if (viewModel.hasUnsavedChanges()) {
            showExitConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext(),  R.style.CustomAlertDialog)
            .setTitle(getString(R.string.exit_creation_title))
            .setMessage(getString(R.string.exit_creation_message))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.finish)) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
            .apply {
                getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.textColor)
                )
                getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.textColor)
                )
            }
    }

    private fun setupListeners() {
        binding.run {
            playlistNameEditText.setOnFocusChangeListener { _, hasFocus ->
                playlistNameEditText.hint =
                    if (hasFocus && playlistNameEditText.text.isNullOrEmpty())
                        getString(R.string.playlist_name_hint)
                    else ""
            }

            playlistNameEditText.addTextChangedListener {
                viewModel.updatePlaylistName(it.toString())
            }

            playlistDescriptionEditText.addTextChangedListener {
                viewModel.updatePlaylistDescription(it.toString())
            }

            playlistImage.setOnClickListener {
                openGallery()
            }

            createButton.setOnClickListener {
                onSubmitAction()
            }
        }
    }

    private fun openGallery() {
        photoPicker.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    open fun observeViewModel() {
        viewModel.playlistName.observe(viewLifecycleOwner) {

        }

        viewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            binding.playlistImage.setImageURI(uri)
            binding.placeholder.isVisible = uri == null
        }

        viewModel.isSaveButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.createButton.isEnabled = isEnabled
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}