package com.example.krug.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageUtils {

    /**
     * Преобразует Uri (например, content://) во временный File.
     * @return File или null, если не удалось
     */
    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, "avatar_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Сжимает изображение до заданного максимального размера в КБ.
     * @param originalFile исходный файл
     * @param maxSizeKB максимальный размер в килобайтах (по умолчанию 1024)
     * @return сжатый файл (новый) или originalFile, если сжатие не потребовалось
     */
    fun compressImage(originalFile: File, maxSizeKB: Int = 1024): File {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(originalFile.absolutePath, options)

        var width = options.outWidth
        var height = options.outHeight
        var sampleSize = 1
        while (width * height > 1_500_000) { // ограничиваем площадь ~1.5 млн пикселей (примерно 1200x1200)
            sampleSize *= 2
            width /= 2
            height /= 2
        }
        options.inSampleSize = sampleSize
        options.inJustDecodeBounds = false

        var bitmap = BitmapFactory.decodeFile(originalFile.absolutePath, options)
        val compressedFile = File(originalFile.parent, "compressed_${originalFile.name}")
        var quality = 100
        do {
            FileOutputStream(compressedFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
            quality -= 10
        } while (compressedFile.length() > maxSizeKB * 1024 && quality > 20)

        bitmap.recycle()
        return compressedFile
    }


    /**
     * Загружает Bitmap из Uri и обрезает до центрального квадрата.
     */
    fun cropToSquareBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val original = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val width = original.width
            val height = original.height
            val minSide = minOf(width, height)

            val x = (width - minSide) / 2
            val y = (height - minSide) / 2

            val cropped = Bitmap.createBitmap(original, x, y, minSide, minSide)
            if (cropped != original) original.recycle()
            cropped
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Из Uri делает квадратный файл (JPEG, качество 100%).
     */
    fun cropToSquareFile(context: Context, uri: Uri): File? {
        val bitmap = cropToSquareBitmap(context, uri) ?: return null
        val outFile = File(context.cacheDir, "cropped_avatar_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        bitmap.recycle()
        return outFile
    }
}