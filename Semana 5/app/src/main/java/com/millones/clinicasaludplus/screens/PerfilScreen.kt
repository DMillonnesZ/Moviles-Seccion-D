package com.millones.clinicasaludplus.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.millones.clinicasaludplus.ui.theme.PurpleBorder
import com.millones.clinicasaludplus.ui.theme.PurpleContainer
import com.millones.clinicasaludplus.ui.theme.PurpleLight
import com.millones.clinicasaludplus.ui.theme.PurpleLightBg
import com.millones.clinicasaludplus.ui.theme.PurplePrimary

@Composable
fun PerfilScreen(
    onMenuClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menú",
                    tint = Color(0xFF222222)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Perfil del paciente",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Patient Header Card
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(PurpleLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "JP",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = PurplePrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Juan Pérez",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF222222)
                )

                Text(
                    text = "DNI: 74839201 • Tipo de sangre: O+",
                    fontSize = 13.sp,
                    color = Color(0xFF777777)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Personal Info Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PurpleLightBg),
                border = BorderStroke(1.dp, PurpleBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Información Personal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF222222)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PerfilInfoRow(icon = Icons.Default.Email, label = "Correo", value = "juan.perez@email.com")
                    Spacer(modifier = Modifier.height(8.dp))
                    PerfilInfoRow(icon = Icons.Default.Phone, label = "Teléfono", value = "+51 987 654 321")
                    Spacer(modifier = Modifier.height(8.dp))
                    PerfilInfoRow(icon = Icons.Default.Shield, label = "Seguro Médico", value = "Rímac EPS (Plan Preferencial)")
                    Spacer(modifier = Modifier.height(8.dp))
                    PerfilInfoRow(icon = Icons.Default.ContactPhone, label = "Contacto de Emergencia", value = "María Pérez (+51 912 345 678)")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Options List
            Text(
                text = "Ajustes y Soporte",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(8.dp))

            PerfilOptionItem(
                icon = Icons.Default.Notifications,
                title = "Notificaciones de Citas",
                subtitle = "Recordatorios por SMS y App",
                onClick = {
                    Toast.makeText(context, "Notificaciones configuradas", Toast.LENGTH_SHORT).show()
                }
            )

            PerfilOptionItem(
                icon = Icons.Default.Badge,
                title = "Credencial Digital",
                subtitle = "Carnet digital de asegurado",
                onClick = {
                    Toast.makeText(context, "Carnet Digital #CS-7483", Toast.LENGTH_SHORT).show()
                }
            )

            PerfilOptionItem(
                icon = Icons.AutoMirrored.Filled.Help,
                title = "Soporte y Ayuda",
                subtitle = "Centro de atención 24/7",
                onClick = {
                    Toast.makeText(context, "Llamando a soporte técnico...", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PerfilInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PurplePrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = Color(0xFF777777))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333))
        }
    }
}

@Composable
fun PerfilOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleLightBg),
        border = BorderStroke(1.dp, PurpleBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PurpleContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF222222))
                Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF777777))
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF888888),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
