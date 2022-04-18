package com.finsol.tech.presentation.account

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.finsol.tech.BuildConfig
import com.finsol.tech.R
import com.finsol.tech.databinding.FragmentAccountProfileBinding
import com.finsol.tech.presentation.base.BaseFragment
import java.io.File

class AccountProfileFragment: BaseFragment() {
    private lateinit var binding: FragmentAccountProfileBinding
    private var latestTmpUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
//    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            // There are no request codes
//            val data: Intent? = result.data
//
//            val selectedImageUri: Uri? = data?.data
//            if (null != selectedImageUri) {
//                // Get the path from the Uri
//                binding.avatar.setImageURI(data.data)
//            }
//        }
//    }

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                binding.avatar.setImageURI(uri)
            }
        }
    }

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { binding.avatar.setImageURI(uri) }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountProfileBinding.inflate(inflater, container, false)
        binding.updateButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.toolbar.backButton.visibility = View.VISIBLE
        binding.toolbar.title2.visibility = View.VISIBLE

        binding.imagePicker.setOnClickListener {
//            val builder = AlertDialog.Builder(context)
//            builder.setTitle("Select Action")
//            builder.setMessage("Choose your option")
//            builder.setPositiveButton("Gallery"){dialog, which ->
                selectImageFromGallery()
//                dialog.dismiss()
//            }
//            builder.setNegativeButton("Camera"){dialog, which ->
//                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//                    activity?.packageManager?.let { it1 ->
//                        takePictureIntent.resolveActivity(it1)?.also {
//                            val permission: Int? = context?.let { it2 ->
//                                ContextCompat.checkSelfPermission(
//                                    it2, android.Manifest.permission.CAMERA)
//                            }
//                            if(permission != PackageManager.PERMISSION_GRANTED){
//                                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA), 1)
//                            } else {
//                                takeImage()
//                            }
//
//                        }
//                    }
//                }
//            }
//            builder.show()
        }

        return binding.root
    }
    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTmpFileUri(): Uri {
        val cacheDir:File = File("")
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }
}