package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import java.time.Instant
import java.time.LocalDate
import java.time.Year
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFechaNacimiento(
    anio: String,
    onAnioElegido: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    var mostrarDialogo by remember { mutableStateOf(false) }

    val initialMillis = remember(anio) {
        val a = anio.toIntOrNull()
        if (a != null && a in 1990..Year.now().value) {
            LocalDate.of(a, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        } else {
            null
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates = remember {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val start1990 = LocalDate.of(1990, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                    val today = LocalDate.now().atStartOfDay(ZoneOffset.UTC).plusDays(1).toInstant().toEpochMilli() - 1
                    return utcTimeMillis in start1990..today
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year in 1990..Year.now().value
                }
            }
        }
    )

    Box(modifier = modifier) {
        OutlinedTextField(
            value = anio,
            onValueChange = {},
            readOnly = true,
            label = { Text("Año de nacimiento") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "Año de nacimiento",
                    tint = cs.primary
                )
            },
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(error)
                } else {
                    val a = anio.toIntOrNull()
                    if (a != null && a in 1990..Year.now().value) {
                        val edad = Year.now().value - a
                        Text(
                            text = if (edad == 1) "1 año" else "$edad años",
                            style = MaterialTheme.typography.labelSmall,
                            color = cs.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = BordeSuave,
                focusedBorderColor = cs.primary,
                unfocusedContainerColor = cs.surface,
                focusedContainerColor = cs.surface,
                unfocusedLabelColor = cs.onSurfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { mostrarDialogo = true }
        )
    }

    if (mostrarDialogo) {
        DatePickerDialog(
            onDismissRequest = { mostrarDialogo = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val fecha = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            onAnioElegido(fecha.year.toString())
                        }
                        mostrarDialogo = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
