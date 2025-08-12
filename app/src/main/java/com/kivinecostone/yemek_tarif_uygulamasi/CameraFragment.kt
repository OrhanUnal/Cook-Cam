package com.kivinecostone.yemek_tarif_uygulamasi

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

class CameraFragment : Fragment() {

    private val CAMERA_AI_API = "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"
    private lateinit var ivImage: ImageView
    private lateinit var tvResult: TextView

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
        private const val GALLERY_PERMISSION_CODE = 3
        private const val  GALLERY_REQUEST_CODE = 4
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivImage = view.findViewById(R.id.iv_image)
        tvResult = view.findViewById(R.id.tv_result)
        val btnGallery = view.findViewById<Button>(R.id.btn_gallery)
        val btnCamera = view.findViewById<Button>(R.id.btn_camera)

        btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }

        btnGallery.setOnClickListener {
            val galleryPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            if (ContextCompat.checkSelfPermission(requireContext(), galleryPermission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, GALLERY_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(galleryPermission),
                    GALLERY_PERMISSION_CODE
                )
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                tvResult.text = "Kamera izni reddedildi."
            }
        }
        if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, GALLERY_REQUEST_CODE)
            } else {
                tvResult.text = "Galeri izni reddedildi."
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var thumbnail: Bitmap? = null
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
            thumbnail = data!!.extras!!.get("data") as Bitmap
            ivImage.setImageBitmap(thumbnail)

        } else if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE && data != null) {
            val imageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            thumbnail = bitmap
            ivImage.setImageBitmap(thumbnail)
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        thumbnail?.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

        sendImageToGPT(base64Image)
    }

    private fun sendImageToGPT(base64Image: String) {
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaType()

        val body = """
{
  "model": "gpt-4.1-mini",
  "messages": [
    {
      "role": "user",
      "content": [
        {
          "type": "text", 
          "text": "Görseli analiz et. Eğer görselde yemek varsa, bu yemeğin yaklaşık kalorisini tahmin et ve ''Tahmini Kalori:'' 'kcal' birimi ile yaz. Eğer görselde yemek yoksa, 'Bu fotoğrafta yemek görünmüyor.' de."
        },
        {
          "type": "image_url", 
          "image_url": {"url": "data:image/jpeg;base64,$base64Image"}
        }
      ]
    }
  ]
}
""".trimIndent()


        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(body.toRequestBody(mediaType))
            .addHeader("Authorization", "Bearer $CAMERA_AI_API")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    tvResult.text = "API isteği başarısız oldu: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                    requireActivity().runOnUiThread {
                        tvResult.text = "Geçersiz yanıt alındı."
                    }
                    return
                }

                try {
                    val jsonResponse = JSONObject(responseBody)
                    val botMessage = jsonResponse
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    requireActivity().runOnUiThread {
                        tvResult.text = "$botMessage"
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        tvResult.text = "Yanıt çözümlenemedi: ${e.message}"
                    }
                }
            }
        })
    }
}
