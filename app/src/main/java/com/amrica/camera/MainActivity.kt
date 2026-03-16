package com.amrica.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.RenderEffect
import android.graphics.Shader
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        if (cameraGranted && audioGranted) {
            // Permissions granted, initialize camera
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        requestCameraPermissions()

        setContent {
            GlassCameraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    CameraScreen()
                }
            }
        }
    }

    private fun requestCameraPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        
        if (requiredPermissions.all { 
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED 
        }) {
            // Ready
        } else {
            requestPermissionLauncher.launch(requiredPermissions)
        }
    }
}

@Composable
fun GlassCameraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color.Black,
            surface = Color.Transparent,
            primary = Color(0xFFFFD60A)
        ),
        content = content
    )
}

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    var currentMode by remember { mutableStateOf("PHOTO") }
    
    Box(modifier = Modifier.fillMaxSize()) {
        
        // Camera Preview Placeholder (To be linked with Camera2 API TextureView)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray)
        ) {
            // ViewFinder goes here
        }

        // Top Glass Bar
        TopGlassBar()

        // Bottom Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            ZoomControls()
            Spacer(modifier = Modifier.height(20.dp))
            ModeSelector(
                currentMode = currentMode,
                onModeSelected = { currentMode = it }
            )
            BottomGlassBar(currentMode)
        }
    }
}

@Composable
fun TopGlassBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .graphicsLayer {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    renderEffect = RenderEffect.createBlurEffect(
                        30f, 30f, Shader.TileMode.MIRROR
                    ).asComposeRenderEffect()
                }
            }
            .background(Color(0x33000000))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        IconButton(onClick = { /* Flash toggle */ }) {
            Text("⚡", color = Color.White, fontSize = 20.sp)
        }
        IconButton(onClick = { /* HDR toggle */ }) {
            Text("HDR", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        IconButton(onClick = { /* Settings */ }) {
            Text("⚙", color = Color.White, fontSize = 20.sp)
        }
    }
}

@Composable
fun ZoomControls() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        renderEffect = RenderEffect.createBlurEffect(
                            15f, 15f, Shader.TileMode.MIRROR
                        ).asComposeRenderEffect()
                    }
                }
                .background(Color(0x66000000), shape = RoundedCornerShape(30.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Text(".5", color = Color.White, fontWeight = FontWeight.Bold)
                Text("1x", color = Color(0xFFFFD60A), fontWeight = FontWeight.Bold)
                Text("3x", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ModeSelector(currentMode: String, onModeSelected: (String) -> Unit) {
    val modes = listOf("VIDEO", "PHOTO", "PORTRAIT", "PRO")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        modes.forEach { mode ->
            Text(
                text = mode,
                color = if (currentMode == mode) Color(0xFFFFD60A) else Color(0x99FFFFFF),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Composable
fun BottomGlassBar(currentMode: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .graphicsLayer {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    renderEffect = RenderEffect.createBlurEffect(
                        40f, 40f, Shader.TileMode.MIRROR
                    ).asComposeRenderEffect()
                }
            }
            .background(Color(0x22FFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery Preview
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33FFFFFF))
            )

            // Shutter Button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(if (currentMode == "VIDEO") Color.Red else Color.White)
                )
            }

            // Switch Camera
            IconButton(
                onClick = { /* Switch Camera Logic */ },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
            ) {
                Text("⟲", color = Color.White, fontSize = 24.sp)
            }
        }
    }
}
