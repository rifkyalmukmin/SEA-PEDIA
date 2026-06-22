package com.example.seapedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.seapedia.core.navigation.NavGraph
import com.example.seapedia.core.ui.theme.SeaPediaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SeaPediaTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
