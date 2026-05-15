package com.anislayaida.judoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.anislayaida.judoapp.navigation.NavigationGraph
import com.anislayaida.judoapp.ui.theme.JudoAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JudoAppTheme {
                NavigationGraph()
            }
        }
    }
}