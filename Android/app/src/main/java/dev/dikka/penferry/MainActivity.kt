package dev.dikka.penferry

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Ratios {
    R16_9 {
        override fun toString() = "16:9"
        override fun toFloat() = 16f / 9f
    },
    R21_9 {
        override fun toString() = "21:9"
        override fun toFloat() = 21f / 9f
    },
    R4_3 {
        override fun toString() = "4:3"
        override fun toFloat() = 4f / 3f
    };

    abstract override fun toString(): String
    abstract fun toFloat(): Float

}

lateinit var output: Output

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Preferences.init(this)

        setContent {
            val address = remember { mutableStateOf(Preferences.address) }
            val port = remember { mutableStateOf("${Preferences.port}") }
            var expanded by remember { mutableStateOf(false) }
            var selectedRatio by remember { mutableStateOf(Ratios.R16_9) }
            MaterialTheme(
                colorScheme = dynamicDarkColorScheme(LocalContext.current)
            ) {
                val listState = rememberLazyListState()
                val list = remember { mutableStateListOf<String>() }
                val scope = rememberCoroutineScope()
                output = Output(list, listState, scope)

                Surface(Modifier.background(MaterialTheme.colorScheme.background)) {
                    output.New(
                        Modifier
                            .height(75.dp)
                            .width(320.dp)
                            .padding(start = 3.dp, top = 8.dp), 9.sp
                    )
                    Column(
                        Modifier
                            .padding(12.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(
                            Modifier,
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = {
                                    expanded = !expanded
                                },
                                modifier = Modifier.width(120.dp)
                            ) {
                                TextField(
                                    readOnly = true,
                                    value = selectedRatio.toString(),
                                    onValueChange = { },
                                    label = { Text("Ratio") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expanded
                                        )
                                    },
                                    modifier = Modifier.menuAnchor(),
                                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = {
                                        expanded = false
                                    }
                                ) {
                                    Ratios.values().forEach { ratio ->
                                        DropdownMenuItem(text = {
                                            Text(text = ratio.toString())
                                        }, onClick = {
                                            selectedRatio = ratio
                                            expanded = false
                                        })
                                    }
                                }
                            }

                            Spacer(Modifier.width(24.dp))
                            TextField(
                                value = address.value, onValueChange = {
                                    address.value = it
                                    Preferences.address = it
                                },
                                label = { Text("Address") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                            )
                            Spacer(Modifier.width(24.dp))
                            TextField(
                                value = port.value, onValueChange = {
                                    port.value = it
                                    Preferences.port = it.toIntOrNull() ?: 0
                                },
                                label = { Text("Port") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(120.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        val canvasSize = remember { mutableStateOf(IntSize.Zero) }
                        val pos = remember { mutableStateOf(Pair(0f, 0f)) }
                        val inRange = remember { mutableStateOf(false) }
                        Box(
                            Modifier
                                .aspectRatio(selectedRatio.toFloat())
                                .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.extraLarge)
                                .onGloballyPositioned {
                                    canvasSize.value = it.size
                                }
                                .pointerInteropFilter {
                                    when (it.action) {
                                        MotionEvent.ACTION_HOVER_MOVE -> {
                                            pos.value = Pair(it.x, it.y)
                                            PenPacket(
                                                PenEvent.HOVER_MOVE,
                                                it.x / canvasSize.value.width,
                                                it.y / canvasSize.value.height,
                                                buttonPressed = it.buttonState == BUTTON_STYLUS_PRIMARY
                                            ).send(Preferences.address, Preferences.port)
                                        }

                                        MotionEvent.ACTION_MOVE, 213 -> {
                                            if (it.getToolType(0) == TOOL_TYPE_STYLUS) {
                                                pos.value = Pair(it.x, it.y)
                                                PenPacket(
                                                    PenEvent.CONTACT_MOVE,
                                                    it.x / canvasSize.value.width,
                                                    it.y / canvasSize.value.height,
                                                    it.pressure,
                                                    it.buttonState == BUTTON_STYLUS_PRIMARY
                                                ).send(Preferences.address, Preferences.port)
                                            }
                                        }

                                        MotionEvent.ACTION_HOVER_EXIT -> {
                                            inRange.value = false
                                            PenPacket(PenEvent.HOVER_EXIT).send(Preferences.address, Preferences.port)
                                        }

                                        MotionEvent.ACTION_DOWN -> {
                                            if (it.getToolType(0) == TOOL_TYPE_STYLUS) {
                                                PenPacket(PenEvent.CONTACT_DOWN).send(
                                                    Preferences.address,
                                                    Preferences.port
                                                )
                                            }
                                        }

                                        MotionEvent.ACTION_UP -> {
                                            if (it.getToolType(0) == TOOL_TYPE_STYLUS) {
                                                PenPacket(PenEvent.CONTACT_UP).send(
                                                    Preferences.address,
                                                    Preferences.port
                                                )
                                            }
                                        }

                                        MotionEvent.ACTION_POINTER_1_UP -> {
                                            if (it.getToolType(1) == TOOL_TYPE_FINGER) {
                                                PenPacket(PenEvent.SUPP_ACTION).send(
                                                    Preferences.address,
                                                    Preferences.port
                                                )
                                            }
                                        }
                                    }
                                    true
                                }
                        ) {

                        }
                    }
                }
            }
        }
    }
}