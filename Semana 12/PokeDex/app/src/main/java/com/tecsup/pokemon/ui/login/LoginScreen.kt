package com.tecsup.pokemon.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.pokemon.domain.model.Sesion

@Composable
fun LoginScreen(
        viewModel: LoginViewModel,
        onLoginExitoso: (Sesion) -> Unit
) {
val estado by viewModel.estado.collectAsStateWithLifecycle()
var verClave by rememberSaveable { mutableStateOf(false) }

// Cuando hay sesión, se avisa a la navegación
LaunchedEffect(estado.sesion) {
    estado.sesion?.let(onLoginExitoso)
}

Scaffold { padding ->
        Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp)
        ) {
    Text(
            text = "PokeDex",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
    )
    Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
    )

    Spacer(Modifier.height(32.dp))

    OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onCorreoChange,
            label = { Text("Correo electrónico") },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(12.dp))

    OutlinedTextField(
            value = estado.clave,
            onValueChange = viewModel::onClaveChange,
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            trailingIcon = {
                    IconButton(onClick = { verClave = !verClave }) {
                    Icon(
                            imageVector = if (verClave) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
            contentDescription = "Ver contraseña"
                        )
                    }
                },
    visualTransformation = if (verClave) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth()
            )

    // Mensaje de error (campos vacíos o credenciales incorrectas)
    estado.error?.let {
        Spacer(Modifier.height(8.dp))
        Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
        )
    }

    Spacer(Modifier.height(24.dp))

    Button(
            onClick = viewModel::iniciarSesion,
            enabled = !estado.cargando,
            modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
    ) {
        if (estado.cargando) {
            CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
            )
        } else {
            Text("Ingresar")
        }
    }
}
}
}