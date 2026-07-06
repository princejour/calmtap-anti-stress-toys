package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

import com.example.utils.SoundManager

@Composable
fun ChallengePopIt(vibrationEnabled: Boolean = true, soundManager: SoundManager? = null, onSuccess: () -> Unit) {
    var pops by remember { mutableStateOf(0) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text("Pop 10 bubbles!", style = MaterialTheme.typography.titleMedium)
        Text("$pops / 10", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.weight(1f)) {
            PopItToy(vibrationEnabled = vibrationEnabled, soundManager = soundManager) {
                pops++
                if (pops == 10) onSuccess()
            }
        }
    }
}

@Composable
fun ChallengeStressBall(soundManager: SoundManager? = null, onSuccess: () -> Unit, onFail: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    var timePressed by remember { mutableStateOf(0f) }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            while (timePressed < 5f) {
                delay(100)
                timePressed += 0.1f
                if (timePressed >= 5f) {
                    onSuccess()
                }
            }
        } else {
            if (timePressed > 0f && timePressed < 5f) {
                onFail()
                timePressed = 0f
            }
        }
    }

    val scaleX by animateFloatAsState(targetValue = if (isPressed) 1.2f else 1f, label = "x")
    val scaleY by animateFloatAsState(targetValue = if (isPressed) 0.8f else 1f, label = "y")

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text("Hold for 5 seconds!", style = MaterialTheme.typography.titleMedium)
        Text("${String.format("%.1f", timePressed)}s", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f).fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer { this.scaleX = scaleX; this.scaleY = scaleY }
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val wasPressed = isPressed
                                isPressed = event.changes.any { it.pressed }
                                if (isPressed && !wasPressed) { soundManager?.playSfx("squish") }
                            }
                        }
                    }
            )
        }
    }
}

@Composable
fun ChallengeFidgetSpinner(soundManager: SoundManager? = null, onSuccess: () -> Unit) {
    var rotation by remember { mutableStateOf(0f) }
    var velocity by remember { mutableStateOf(0f) }
    var maxVelocity by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            if (velocity > 0) {
                rotation += velocity
                velocity *= 0.98f
                if (velocity < 0.1f) velocity = 0f
            } else if (velocity < 0) {
                rotation += velocity
                velocity *= 0.98f
                if (velocity > -0.1f) velocity = 0f
            }
            if (Math.abs(velocity) > maxVelocity) maxVelocity = Math.abs(velocity)
            if (maxVelocity > 40f) onSuccess()
            delay(16)
        }
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text("Spin to reach target speed!", style = MaterialTheme.typography.titleMedium)
        Text("Speed: ${maxVelocity.toInt()} / 40", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f).fillMaxWidth().pointerInput(Unit) {
            detectDragGestures { change, dragAmount -> velocity += dragAmount.x * 0.5f; if (Math.abs(velocity) > 2f) soundManager?.playSfx("spin", Math.min(1f, Math.abs(velocity) / 20f)) }
        }) {
            Canvas(modifier = Modifier.size(200.dp).graphicsLayer { rotationZ = rotation }) {
                val center = Offset(size.width/2, size.height/2)
                drawCircle(color = Color.DarkGray, radius = 50f, center = center)
                for (i in 0..2) {
                    val angle = (i * 120) * (Math.PI / 180)
                    val x = center.x + 100f * Math.cos(angle).toFloat()
                    val y = center.y + 100f * Math.sin(angle).toFloat()
                    drawCircle(color = Color(0xFF00BCD4), radius = 60f, center = Offset(x, y))
                    drawLine(color = Color(0xFF00BCD4), start = center, end = Offset(x, y), strokeWidth = 40f)
                }
            }
        }
    }
}

@Composable
fun ChallengeBreathingCircle(soundManager: SoundManager? = null, onSuccess: () -> Unit, onFail: () -> Unit) {
    var timeElapsed by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while(timeElapsed < 12f) {
            delay(100)
            timeElapsed += 0.1f
        }
        onSuccess()
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text("Do not touch the screen for 12 seconds.", style = MaterialTheme.typography.titleMedium)
        Text("${String.format("%.1f", 12f - timeElapsed)}s left", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.weight(1f).fillMaxWidth().pointerInput(Unit) {
            awaitPointerEventScope {
                while(true) {
                    val event = awaitPointerEvent()
                    if (event.changes.any { it.pressed }) {
                        onFail()
                    }
                }
            }
        }) {
            BreathingCircleToy(soundManager = soundManager)
        }
    }
}

@Composable
fun ChallengeBubbleWrap(soundManager: SoundManager? = null, onSuccess: () -> Unit) {
    var pops by remember { mutableStateOf(0) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text("Clear 20 bubbles!", style = MaterialTheme.typography.titleMedium)
        Text("$pops / 20", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.weight(1f)) {
            BubbleWrapToy(soundManager = soundManager) {
                pops++
                if (pops >= 20) onSuccess()
            }
        }
    }
}

@Composable
fun ChallengeSlime(soundManager: SoundManager? = null, onSuccess: () -> Unit, onFail: () -> Unit) {
    var timeDragging by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(isDragging) {
        if (isDragging) {
            while (timeDragging < 3f) {
                delay(100)
                timeDragging += 0.1f
            }
            onSuccess()
        } else {
            if (timeDragging > 0f && timeDragging < 3f) {
                onFail()
                timeDragging = 0f
            }
        }
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text("Drag slime continuously for 3s!", style = MaterialTheme.typography.titleMedium)
        Text("${String.format("%.1f", timeDragging)}s", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            var touchPoint by remember { mutableStateOf<Offset?>(null) }
            Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true; touchPoint = it },
                    onDragEnd = { isDragging = false; touchPoint = null },
                    onDragCancel = { isDragging = false; touchPoint = null },
                    onDrag = { change, _ -> touchPoint = change.position; soundManager?.playSfx("slime") }
                )
            }) {
                val center = Offset(size.width / 2, size.height / 2)
                val slimeColor = Color(0xFF00BFA5)
                drawCircle(color = slimeColor, radius = 200f, center = center)
                touchPoint?.let {
                    drawCircle(color = slimeColor, radius = 100f, center = it)
                    drawLine(color = slimeColor, start = center, end = it, strokeWidth = 100f)
                }
            }
        }
    }
}

@Composable
fun ChallengeSandFlow(soundManager: SoundManager? = null, onSuccess: () -> Unit) {
    var timeDragging by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(isDragging) {
        if (isDragging) {
            while (timeDragging < 3f) {
                delay(100)
                timeDragging += 0.1f
                if (timeDragging >= 3f) onSuccess()
            }
        } else {
            timeDragging = 0f
        }
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text("Move sand continuously for 3s!", style = MaterialTheme.typography.titleMedium)
        Text("${String.format("%.1f", timeDragging)}s", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            var touchPoint by remember { mutableStateOf<Offset?>(null) }
            val particles = remember { List(50) { Offset(Random.nextFloat(), Random.nextFloat()) }.toMutableStateList() }
            LaunchedEffect(touchPoint) {
                while (true) {
                    for (i in particles.indices) {
                        val p = particles[i]
                        var nextY = p.y + 0.01f
                        var nextX = p.x
                        if (touchPoint != null) {
                            val targetX = touchPoint!!.x / 1000f
                            nextX += (targetX - p.x) * 0.05f
                        }
                        if (nextY > 1f) nextY = 0f
                        particles[i] = Offset(nextX, nextY)
                    }
                    delay(16)
                }
            }
            Canvas(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true; touchPoint = it },
                    onDragEnd = { isDragging = false; touchPoint = null },
                    onDragCancel = { isDragging = false; touchPoint = null },
                    onDrag = { change, _ -> touchPoint = change.position; soundManager?.playSfx("sand") }
                )
            }) {
                val w = size.width
                val h = size.height
                val sandColor = Color(0xFFD4C4A8)
                particles.forEach { p ->
                    drawCircle(color = sandColor, radius = 15f, center = Offset(p.x * w, p.y * h))
                }
            }
        }
    }
}

@Composable
fun ChallengeRainMode(soundManager: SoundManager? = null, onSuccess: () -> Unit) {
    var taps by remember { mutableStateOf(0) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text("Tap the rain 5 times!", style = MaterialTheme.typography.titleMedium)
        Text("$taps / 5", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.weight(1f).fillMaxWidth().pointerInput(Unit) {
            awaitPointerEventScope {
                while(true) {
                    val event = awaitPointerEvent()
                    if (event.changes.any { it.pressed }) {
                        taps++
                        if (taps >= 5) onSuccess()
                    }
                }
            }
        }) {
            RainModeToy(soundManager = soundManager)
        }
    }
}
