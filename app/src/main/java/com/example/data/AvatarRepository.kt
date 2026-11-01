package com.example.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.model.AvatarEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class AvatarRepository(
    private val avatarDao: AvatarDao,
    private val context: Context
) {
    val allAvatars: Flow<List<AvatarEntity>> = avatarDao.getAllAvatars()

    suspend fun saveAvatar(avatar: AvatarEntity) {
        withContext(Dispatchers.IO) {
            avatarDao.insertAvatar(avatar)
        }
    }

    suspend fun deleteAvatar(avatar: AvatarEntity) {
        withContext(Dispatchers.IO) {
            avatarDao.deleteAvatar(avatar)
        }
    }

    suspend fun clearAll() {
        withContext(Dispatchers.IO) {
            avatarDao.clearAll()
        }
    }

    suspend fun downloadImageToGallery(imageUrl: String, title: String): Result<Uri> {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = fetchBitmap(imageUrl) ?: return@withContext Result.failure(Exception("No se pudo cargar la imagen"))

                val filename = "LUNA_${System.currentTimeMillis()}.jpg"
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/LunaStudio")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return@withContext Result.failure(Exception("Error al registrar en MediaStore"))

                context.contentResolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, contentValues, null, null)
                }

                Result.success(uri)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun prepareImageForShare(imageUrl: String): Result<Uri> {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = fetchBitmap(imageUrl) ?: return@withContext Result.failure(Exception("No se pudo descargar la imagen"))
                val cacheDir = File(context.cacheDir, "images").apply { mkdirs() }
                val imageFile = File(cacheDir, "avatar_share_${System.currentTimeMillis()}.jpg")
                FileOutputStream(imageFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    imageFile
                )
                Result.success(uri)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun fetchBitmap(urlStr: String): Bitmap? {
        return try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connectTimeout = 15000
            connection.readTimeout = 20000
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            null
        }
    }
}
