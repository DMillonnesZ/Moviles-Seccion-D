package com.tecsup.millones.authapp

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

// Colores turquesa
val Turquesa = Color(0xFF2BB5B5)
val TurquesaOscuro = Color(0xFF1E9C9C)
val TurquesaClaro = Color(0xFFBDEBEA)
val GrisCampo = Color(0xFFF3F5F7)

// Sombra suave para texto sobre fotos
val SombraTexto = Shadow(color = Color.Black.copy(alpha = 0.35f), offset = Offset(0f, 4f), blurRadius = 12f)

const val AUTOR = "Daniel Millones V. - Tecsup"

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verPassword by remember { mutableStateOf(false) }
    var recordarme by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        // ---------- IMAGEN DE FONDO ----------
        Image(
            bitmap = imagenLigera(R.drawable.login_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Capa turquesa encima de la foto
        CapaTurquesa()

        // ---------- ENCABEZADO ----------
        EncabezadoFoto(
            etiqueta = "PROGRAMACIÓN MÓVIL",
            titulo = "Bienvenido\nde vuelta",
            subtitulo = "Inicia sesión y gestiona tus tareas y proyectos",
            modifier = Modifier.align(Alignment.TopStart)
        )

        // ---------- TARJETA BLANCA ----------
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Iniciar Sesión", fontSize = 26.sp, fontWeight = FontWeight.Bold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿No tienes cuenta?", color = Color.Gray, fontSize = 14.sp)
                    TextButton(onClick = onNavigateToRegister) {
                        Text("Regístrate", color = Turquesa, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Correo
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Outlined.Email, null, tint = Color.Gray) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = camposColores(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = Color.Gray) },
                    trailingIcon = {
                        IconButton(onClick = { verPassword = !verPassword }) {
                            Icon(
                                if (verPassword) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = "Ver contraseña",
                                tint = Color.Gray
                            )
                        }
                    },
                    visualTransformation = if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = camposColores(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Recordarme + Olvidé contraseña
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = recordarme,
                            onCheckedChange = { recordarme = it },
                            colors = CheckboxDefaults.colors(checkedColor = Turquesa)
                        )
                        Text("Recordarme", fontSize = 13.sp, color = Color.Gray)
                    }
                    TextButton(onClick = {
                        if (email.isBlank()) {
                            Toast.makeText(context, "Escribe tu correo primero", Toast.LENGTH_SHORT).show()
                        } else {
                            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                                val msg = if (task.isSuccessful) "Te enviamos un correo de recuperación"
                                else "Error: ${task.exception?.message}"
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        }
                    }) {
                        Text("¿Olvidaste tu contraseña?", fontSize = 13.sp, color = Turquesa)
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Botón Ingresar
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                } else {
                                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Turquesa),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                    } else {
                        Text("Ingresar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(text = AUTOR, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
            }
        }
    }
}

// ================== COMPONENTES COMPARTIDOS ==================

// Degradado turquesa sobre la foto
@Composable
fun CapaTurquesa() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        TurquesaOscuro.copy(alpha = 0.92f),
                        Turquesa.copy(alpha = 0.55f),
                        Color.Transparent
                    )
                )
            )
    )
}

// Encabezado: etiqueta + título + subtítulo
@Composable
fun EncabezadoFoto(
    etiqueta: String,
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 24.dp, start = 24.dp, end = 24.dp)
    ) {
        // Etiqueta tipo píldora
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = 0.2f)
        ) {
            Text(
                text = etiqueta,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = titulo,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 38.sp,
            style = TextStyle(shadow = SombraTexto)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = subtitulo,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 15.sp,
            lineHeight = 20.sp,
            style = TextStyle(shadow = SombraTexto)
        )
    }
}

// Estilo de los campos
@Composable
fun camposColores() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = GrisCampo,
    unfocusedContainerColor = GrisCampo,
    focusedBorderColor = Turquesa,
    unfocusedBorderColor = Color.Transparent,
    cursorColor = TurquesaOscuro
)

// Carga la imagen reducida; si falla, no cierra la app
@Composable
fun imagenLigera(id: Int, maxAncho: Int = 1080): ImageBitmap {
    val res = LocalContext.current.resources
    return remember(id) {
        try {
            val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeResource(res, id, opts)
            var sample = 1
            while (opts.outWidth / sample > maxAncho) sample *= 2
            BitmapFactory.decodeResource(
                res, id,
                BitmapFactory.Options().apply {
                    inSampleSize = sample
                    inScaled = false
                }
            )?.asImageBitmap() ?: ImageBitmap(1, 1)
        } catch (e: Exception) {
            ImageBitmap(1, 1)
        }
    }
}