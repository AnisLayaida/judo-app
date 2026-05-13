package com.anislayaida.judoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.anislayaida.judoapp.data.technique.TechniqueSeeder
import com.anislayaida.judoapp.navigation.NavigationGraph
import com.anislayaida.judoapp.ui.theme.JudoAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var techniqueSeeder: TechniqueSeeder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            techniqueSeeder.seedIfNeeded()
        }

        setContent {
            JudoAppTheme {
                NavigationGraph()
            }
        }
    }
}