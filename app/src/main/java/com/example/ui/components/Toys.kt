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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.utils.VibrationManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

import com.example.utils.SoundManager

// Pop It Toy
@Composable
fun PopItToy(vibrationEnabled: Boolean = true, soundManager: SoundManager? = null, onPop: () -> Unit = {}) {
    val context = LocalContext.current
    val vibrationManager = remember { VibrationManager(context) }
    var resetTrigger by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(32.dp))
                .padding(16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(25) { index ->
                    PopBubble(resetTrigger) {
                        if (vibrationEnabled) vibrationManager.vibrate()
                        soundManager?.playSfx("pop")
                        onPop()
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { resetTrigger = !resetTrigger }) {
            Text("Reset Pop It")
        }
    }
}

@Composable
fun PopBubble(resetTrigger: Boolean, onPop: () -> Unit) {
    var isPopped by remember(resetTrigger) { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPopped) 0.8f else 1f, label = "pop")
    val color = if (isPopped) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .background(color, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isPopped) {
                    isPopped = true
                    onPop()
                } else {
                    isPopped = false
                }
            }
    )
}

// Stress Ball Toy
@Composable
fun StressBallToy(vibrationEnabled: Boolean = true, soundManager: SoundManager? = null, onSqueeze: () -> Unit = {}) {
    val context = LocalContext.current
    val vibrationManager = remember { VibrationManager(context) }
    var isPressed by remember { mutableStateOf(false) }

    val scaleX by animateFloatAsState(
        targetValue = if (isPressed) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "x"
    )
    val scaleY by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "y"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    this.scaleX = scaleX
                    this.scaleY = scaleY
                }
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val wasPressed = isPressed
                            isPressed = event.changes.any { it.pressed }
                            if (isPressed && !wasPressed) {
                                if (vibrationEnabled) vibrationManager.vibrate(30L)
                                soundManager?.playSfx("squish")
                                onSqueeze()
                            }
                        }
                    }
                }
        )
    }
}

// Bubble Wrap Toy
@Composable
fun BubbleWrapToy(soundManager: SoundManager? = null, onPop: () -> Unit = {}) {
    var resetTrigger by remember { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Transparent)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(60) {
                    WrapBubble(resetTrigger) {
                        soundManager?.playSfx("bubble")
                        onPop()
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { resetTrigger = !resetTrigger }) {
            Text("Reset All")
        }
    }
}

@Composable
fun WrapBubble(resetTrigger: Boolean, onPop: () -> Unit) {
    var isPopped by remember(resetTrigger) { mutableStateOf(false) }
    val alpha by animateFloatAsState(targetValue = if (isPopped) 0.1f else 0.4f, label = "alpha")

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(Color.Gray.copy(alpha = alpha), CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isPopped) {
                    isPopped = true
                    onPop()
                }
            }
    )
}

// Slime Toy
@Composable
fun SlimeToy(soundManager: SoundManager? = null) {
    var touchPoint by remember { mutableStateOf<Offset?>(null) }
    var lastSoundTime by remember { mutableStateOf(0L) }
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { 
                        touchPoint = it
                        soundManager?.playSfx("slime")
                        lastSoundTime = System.currentTimeMillis()
                    },
                    onDragEnd = { touchPoint = null },
                    onDragCancel = { touchPoint = null },
                    onDrag = { change, _ -> 
                        touchPoint = change.position
                        val now = System.currentTimeMillis()
                        if (now - lastSoundTime > 400) {
                            soundManager?.playSfx("slime")
                            lastSoundTime = now
                        }
                    }
                )
            }
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val slimeColor = Color(0xFF00BFA5)
        
        drawCircle(color = slimeColor, radius = 300f, center = center)
        
        touchPoint?.let {
            drawCircle(color = slimeColor, radius = 150f, center = it)
            // draw connecting line to simulate goo
            drawLine(color = slimeColor, start = center, end = it, strokeWidth = 150f)
        }
    }
}

// Sand Flow Toy
@Composable
fun SandFlowToy(soundManager: SoundManager? = null) {
    val particles = remember { List(100) { Offset(Random.nextFloat(), Random.nextFloat()) }.toMutableStateList() }
    var touchPoint by remember { mutableStateOf<Offset?>(null) }
    var lastSoundTime by remember { mutableStateOf(0L) }
    
    LaunchedEffect(touchPoint) {
        while (true) {
            val now = System.currentTimeMillis()
            if (touchPoint != null && now - lastSoundTime > 200) {
                soundManager?.playSfx("sand")
                lastSoundTime = now
            }
            
            for (i in particles.indices) {
                val p = particles[i]
                var nextY = p.y + 0.01f
                var nextX = p.x
                if (touchPoint != null) {
                    val targetX = touchPoint!!.x / 1000f // normalize roughly
                    nextX += (targetX - p.x) * 0.05f
                }
                if (nextY > 1f) nextY = 0f
                particles[i] = Offset(nextX, nextY)
            }
            delay(16)
        }
    }
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { touchPoint = it },
                    onDragEnd = { touchPoint = null },
                    onDragCancel = { touchPoint = null },
                    onDrag = { change, _ -> touchPoint = change.position }
                )
            }
    ) {
        val w = size.width
        val h = size.height
        val sandColor = Color(0xFFD4C4A8)
        
        particles.forEach { p ->
            drawCircle(color = sandColor, radius = 10f, center = Offset(p.x * w, p.y * h))
        }
    }
}

// Fidget Spinner
@Composable
fun FidgetSpinnerToy(soundManager: SoundManager? = null) {
    var rotation by remember { mutableStateOf(0f) }
    var velocity by remember { mutableStateOf(0f) }
    var lastSoundTime by remember { mutableStateOf(0L) }
    
    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            if (Math.abs(velocity) > 2f && now - lastSoundTime > 300) {
                soundManager?.playSfx("spin", Math.min(1f, Math.abs(velocity) / 20f))
                lastSoundTime = now
            }
            if (velocity > 0) {
                rotation += velocity
                velocity *= 0.98f // friction
                if (velocity < 0.1f) velocity = 0f
            } else if (velocity < 0) {
                rotation += velocity
                velocity *= 0.98f
                if (velocity > -0.1f) velocity = 0f
            }
            delay(16)
        }
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    velocity += dragAmount.x * 0.5f
                }
            }
    ) {
        Canvas(modifier = Modifier
            .size(200.dp)
            .graphicsLayer { rotationZ = rotation }) {
            val center = Offset(size.width/2, size.height/2)
            drawCircle(color = Color.DarkGray, radius = 50f, center = center)
            val armLength = 100f
            for (i in 0..2) {
                val angle = (i * 120) * (Math.PI / 180)
                val x = center.x + armLength * Math.cos(angle).toFloat()
                val y = center.y + armLength * Math.sin(angle).toFloat()
                drawCircle(color = Color(0xFF00BCD4), radius = 60f, center = Offset(x, y))
                drawLine(color = Color(0xFF00BCD4), start = center, end = Offset(x, y), strokeWidth = 40f)
            }
        }
    }
}

// Breathing Circle
@Composable
fun BreathingCircleToy(soundManager: SoundManager? = null) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    LaunchedEffect(scale > 1.0f) { soundManager?.playSfx("breathe") }

    val text = if (scale < 1.0f) "Breathe In" else "Breathe Out"

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .scale(scale)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
        )
        Text(text, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground)
    }
}

// Rain Mode
@Composable
fun RainModeToy(soundManager: SoundManager? = null) {
    LaunchedEffect(Unit) { soundManager?.playSfx("rain") }

    var ripples = remember { mutableStateListOf<Offset>() }
    
    LaunchedEffect(ripples.size) {
        if (ripples.isNotEmpty()) {
            delay(1000)
            if (ripples.isNotEmpty()) ripples.removeAt(0)
        }
    }
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF263238))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.any { it.pressed }) {
                            soundManager?.playSfx("water_drop")
                            ripples.add(event.changes.first().position)
                        }
                    }
                }
            }
    ) {
        val rainColor = Color(0xFF81D4FA).copy(alpha = 0.5f)
        for (i in 0..50) {
            val x = Random.nextFloat() * size.width
            val y = (System.currentTimeMillis() % 2000) / 2000f * size.height + Random.nextFloat() * 100
            drawLine(rainColor, start = Offset(x, y), end = Offset(x, y + 40f), strokeWidth = 2f)
        }
        
        ripples.forEach {
            drawCircle(color = rainColor, radius = 50f, center = it, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f))
        }
    }
}
