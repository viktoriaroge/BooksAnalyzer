package com.viroge.booksanalyzer.ui.screens.books.scanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.viroge.booksanalyzer.ui.components.PvTopAppBar
import com.viroge.booksanalyzer.ui.nav.LocalAppScaffoldPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanBookBarcodeScreen(
    state: ScanBookBarcodeUiState,
    onBack: () -> Unit,
    onIsbnDetected: (String) -> Unit,
    onResetScanner: () -> Unit,
) {
    val appScaffoldPadding = LocalAppScaffoldPadding.current
    val context = LocalContext.current
    val activity = context as? Activity

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var isPermanentlyDenied by remember { mutableStateOf(false) }

    val updatePermissionState = {
        val isGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        hasCameraPermission = isGranted

        if (!isGranted && activity != null) {
            isPermanentlyDenied = !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
        }
    }

    val openAppSettings = {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            if (!granted) updatePermissionState()
        }
    )

    // 3. Handle coming back from "Open Settings"
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                updatePermissionState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 4. Initial Launch
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(state.scannedIsbn) {
        state.scannedIsbn?.let {
            onIsbnDetected(it)
            onResetScanner() // Clear so it can scan again if we return
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            PvTopAppBar(
                title = stringResource(state.screenValues.screenName),
                canGoBack = true,
                onBack = onBack,
            )
        },
    ) { screenPadding ->

        Column(
            modifier = Modifier
                .padding(top = screenPadding.calculateTopPadding()) // top bar
                .padding(bottom = appScaffoldPadding.calculateBottomPadding())
                .fillMaxSize(),
        ) {
            if (hasCameraPermission) {
                ScannerCameraContent(onIsbnDetected = onIsbnDetected)

            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // TODO: Move new strings to mapper
                        val textToShow = if (isPermanentlyDenied) "Camera access permanently denied." else "Camera access required."
                        val buttonText = if (isPermanentlyDenied) "Open Settings" else "Enable Camera"

                        Text(
                            text = textToShow,
                            modifier = Modifier.padding(16.dp)
                        )

                        Button(onClick = {
                            if (isPermanentlyDenied) openAppSettings() else launcher.launch(Manifest.permission.CAMERA)
                        }) {
                            Text(buttonText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScannerCameraContent(
    modifier: Modifier = Modifier,
    onIsbnDetected: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptic = LocalHapticFeedback.current

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(LifecycleCameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                IsbnAnalyzer(
                    onIsbnDetected = onIsbnDetected,
                    onScanSuccess = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                )
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            },
            onRelease = {
                cameraController.unbind()
            }
        )

        ScannerOverlay()
    }
}

@Composable
fun ScannerOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        val scannerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    val scannerSize = size.width * 0.7f
                    val sideDim = (size.width - scannerSize) / 2
                    val topDim = (size.height - scannerSize) / 2

                    // Top
                    drawRect(
                        color = backgroundColor,
                        size = Size(size.width, topDim),
                    )
                    // Bottom
                    drawRect(
                        color = backgroundColor,
                        topLeft = Offset(0f, topDim + scannerSize),
                        size = Size(size.width, size.height - (topDim + scannerSize)),
                    )
                    // Left
                    drawRect(
                        color = backgroundColor,
                        topLeft = Offset(0f, topDim),
                        size = Size(sideDim, scannerSize),
                    )
                    // Right
                    drawRect(
                        color = backgroundColor,
                        topLeft = Offset(sideDim + scannerSize, topDim),
                        size = Size(sideDim, scannerSize),
                    )
                    drawContent()
                }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(1f)
                .border(
                    width = 2.dp,
                    color = scannerColor,
                )
        )
    }
}
