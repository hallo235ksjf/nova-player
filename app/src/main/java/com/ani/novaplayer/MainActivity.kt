package com.ani.novaplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ani.novaplayer.ui.LibraryScreen
import com.ani.novaplayer.ui.PlayerScreen
import com.ani.novaplayer.ui.theme.NovaPlayerTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = PlayerViewModel(applicationContext)
        viewModel.connect()

        setContent {
            NovaPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    var hasPermission by remember { mutableStateOf(false) }
                    val permission = if (Build.VERSION.SDK_INT >= 33)
                        arrayOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO)
                    else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

                    val launcher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestMultiplePermissions()
                    ) { result -> hasPermission = result.values.all { it } }

                    LaunchedEffect(Unit) { launcher.launch(permission) }

                    NavHost(navController = navController, startDestination = "library") {
                        composable("library") {
                            LibraryScreen(
                                hasPermission = hasPermission,
                                viewModel = viewModel,
                                onOpenPlayer = { navController.navigate("player") }
                            )
                        }
                        composable("player") {
                            PlayerScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        viewModel.release()
        super.onDestroy()
    }
}
