package com.example.krug.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    /**
     * Сжимает изображение до заданного максимального размера в КБ.
     * @param originalFile исходный файл
     * @param maxSize максимальный размер в килобайтах (по умолчанию 1024)
     * @return сжатый файл (новый) или originalFile, если сжатие не потребовалось
     */
    fun compressPhoto(originalFile: File, maxSize: Int = 2048): File {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(originalFile.absolutePath, options)

        var width = options.outWidth
        var height = options.outHeight

        // Если изображение больше maxSize по любой стороне, уменьшаем пропорционально
        if (width > maxSize || height > maxSize) {
            val scaleFactor = maxOf(width.toFloat() / maxSize, height.toFloat() / maxSize)
            width = (width / scaleFactor).toInt()
            height = (height / scaleFactor).toInt()
        }

        val sampleSize = calculateInSampleSize(options.outWidth, options.outHeight, width, height)
        options.inSampleSize = sampleSize
        options.inJustDecodeBounds = false

        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath, options)
        val compressedFile = File(originalFile.parent, "compressed_${originalFile.name}")

        FileOutputStream(compressedFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)  // фиксированное качество 90%
        }
        bitmap.recycle()
        return compressedFile
    }

    private fun calculateInSampleSize(origWidth: Int, origHeight: Int, reqWidth: Int, reqHeight: Int): Int {
        var inSampleSize = 1
        if (origHeight > reqHeight || origWidth > reqWidth) {
            val halfHeight = origHeight / 2
            val halfWidth = origWidth / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
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