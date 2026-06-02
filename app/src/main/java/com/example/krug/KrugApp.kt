package com.example.krug

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class KrugApp : Application(), ImageLoaderFactory {
    @Inject
    lateinit var imageLoader: ImageLoader

    override fun newImageLoader(): ImageLoader = imageLoader
}