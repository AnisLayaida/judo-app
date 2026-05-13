package com.anislayaida.judoapp

import android.app.Application
import android.os.Build
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.anislayaida.judoapp.data.club.ClubSeeder
import com.anislayaida.judoapp.data.technique.TechniqueSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class JudoApplication : Application() {

    @Inject lateinit var techniqueSeeder: TechniqueSeeder
    @Inject lateinit var clubSeeder: ClubSeeder

    override fun onCreate() {
        super.onCreate()

        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .components {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
        )

        CoroutineScope(Dispatchers.IO).launch {
            techniqueSeeder.seedIfNeeded()
            clubSeeder.seedIfNeeded()
        }
    }
}