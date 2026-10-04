package com.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.GrisTexto

private data class Seccion(val titulo: String, val cuerpo: String)

private val secciones = listOf(
    Seccion(
        "1. Aceptación de los términos",
        "Al crear tu cuenta en la app Paciente de la Clínica SaludPlus y marcar la casilla de " +
                "aceptación, declaras que has leído estos Términos y Condiciones y la Política de " +
                "Privacidad, y que los aceptas. Si no estás de acuerdo, no podrás registrarte ni usar " +
                "la aplicación."
    ),
    Seccion(
        "2. Marco legal",
        "Estos términos se rigen por la normativa peruana, en particular:\n" +
                "• Ley N.° 29733, Ley de Protección de Datos Personales.\n" +
                "• Decreto Supremo N.° 016-2024-JUS, Reglamento de la Ley N.° 29733, vigente desde el " +
                "30 de marzo de 2025.\n" +
                "• Ley N.° 26842, Ley General de Salud.\n" +
                "• Ley N.° 29414, que establece los derechos de las personas usuarias de los " +
                "servicios de salud.\n" +
                "• Ley N.° 30024, que crea el Registro Nacional de Historias Clínicas Electrónicas.\n" +
                "• Norma Técnica de Salud para la Gestión de la Historia Clínica (R.M. N.° 214-2018/MINSA)."
    ),
    Seccion(
        "3. Responsable del tratamiento",
        "El responsable del tratamiento de tus datos personales es la Clínica SaludPlus, con " +
                "domicilio en Av. Los Olivos 123, Lima, Perú. Para consultas sobre privacidad puedes " +
                "escribir a privacidad@saludplus.example."
    ),
    Seccion(
        "4. Datos que recopilamos",
        "Para brindarte el servicio recopilamos:\n" +
                "• Datos de identificación y contacto: nombre completo, teléfono y correo electrónico.\n" +
                "• Datos de acceso: la contraseña de tu cuenta.\n" +
                "• Datos de tus citas: médico, especialidad, fecha y hora.\n" +
                "• Motivo de consulta, solo si decides escribirlo.\n\n" +
                "Los datos relacionados con tu salud, como las citas médicas y el motivo de consulta, " +
                "son datos sensibles según la Ley N.° 29733 y reciben protección reforzada."
    ),
    Seccion(
        "5. Para qué usamos tus datos",
        "Usamos tus datos únicamente para:\n" +
                "• Crear y administrar tu cuenta.\n" +
                "• Agendar, confirmar y cancelar tus citas médicas.\n" +
                "• Enviarte avisos y notificaciones sobre tus citas.\n" +
                "• Cumplir las obligaciones que la normativa de salud impone a la clínica.\n\n" +
                "No usaremos tus datos para publicidad ni para otras finalidades sin pedirte antes un " +
                "consentimiento aparte."
    ),
    Seccion(
        "6. Consentimiento",
        "Tu consentimiento debe ser libre, previo, expreso, inequívoco e informado. Como tratamos " +
                "datos sensibles, la ley exige que lo otorgues por escrito, lo que incluye la firma " +
                "electrónica u otro medio que garantice tu voluntad. Al marcar la casilla de aceptación " +
                "en el registro, das ese consentimiento por medio electrónico.\n\n" +
                "Puedes revocarlo en cualquier momento, sin efectos retroactivos, escribiendo al correo " +
                "de contacto. Si lo revocas, es posible que ya no podamos brindarte el servicio de la app."
    ),
    Seccion(
        "7. Confidencialidad de la historia clínica",
        "La información de tu historia clínica es reservada. Solo pueden acceder a ella tú, tu " +
                "representante legal y el personal de salud que te atiende o que tú autorices, salvo las " +
                "excepciones que establece la Ley General de Salud.\n\n" +
                "La Ley N.° 30024 reconoce que el paciente es el propietario de la información de su " +
                "historia clínica electrónica, y obliga a garantizar su reserva, privacidad y " +
                "confidencialidad. Todo el personal que accede a ella debe guardar el secreto profesional."
    ),
    Seccion(
        "8. Con quién compartimos tus datos",
        "No vendemos tus datos. Solo los compartimos con:\n" +
                "• El profesional de salud que te atiende.\n" +
                "• Autoridades de salud o judiciales, cuando la ley lo exige.\n" +
                "• Proveedores que nos prestan servicios tecnológicos y tratan los datos por encargo " +
                "nuestro, bajo contrato y con las mismas obligaciones de confidencialidad y seguridad.\n\n" +
                "Si algún dato se transfiere fuera del Perú, lo haremos cumpliendo lo que establece la " +
                "normativa de protección de datos personales."
    ),
    Seccion(
        "9. Conservación",
        "Conservamos tus datos solo el tiempo necesario para las finalidades indicadas y por los " +
                "plazos que fije la normativa de salud para las historias clínicas. Cuando ya no sean " +
                "necesarios, los eliminamos o los anonimizamos de forma segura."
    ),
    Seccion(
        "10. Seguridad",
        "Aplicamos medidas técnicas, organizativas y legales para proteger tus datos contra accesos " +
                "no autorizados, pérdida o alteración. Si ocurre una brecha de seguridad que afecte tus " +
                "datos personales, la comunicaremos a la Autoridad Nacional de Protección de Datos " +
                "Personales y a las personas afectadas dentro de los plazos que fija el Reglamento.\n\n" +
                "Tú también debes cuidar tu contraseña y no compartirla con nadie."
    ),
    Seccion(
        "11. Tus derechos",
        "Como titular de tus datos puedes ejercer los derechos de:\n" +
                "• Acceso: saber qué datos tuyos tratamos.\n" +
                "• Rectificación: corregir datos inexactos o incompletos.\n" +
                "• Cancelación: pedir que eliminemos tus datos.\n" +
                "• Oposición: negarte a un tratamiento determinado.\n" +
                "• Portabilidad: recibir tus datos en un formato que puedas reutilizar.\n\n" +
                "También tienes los derechos que reconoce la Ley N.° 29414, como recibir información " +
                "sobre tu atención, que se respete tu intimidad y que se te pida tu consentimiento " +
                "informado.\n\n" +
                "Para ejercerlos, escribe a privacidad@saludplus.example. Si consideras que no atendimos " +
                "tu solicitud, puedes presentar un reclamo ante la Autoridad Nacional de Protección de " +
                "Datos Personales del Ministerio de Justicia y Derechos Humanos o, por temas de atención " +
                "en salud, ante SUSALUD."
    ),
    Seccion(
        "12. Uso de la aplicación",
        "• Tu cuenta es personal: debes dar datos verdaderos y no compartir tu contraseña.\n" +
                "• La app sirve para gestionar citas; no reemplaza la consulta médica ni la atención de " +
                "emergencias.\n" +
                "• En una emergencia, acude al servicio de emergencia más cercano o llama al SAMU (106).\n" +
                "• La clínica puede reprogramar o cancelar una cita por causas de fuerza mayor y te " +
                "avisará por los medios registrados."
    ),
    Seccion(
        "13. Cambios en estos términos",
        "Podemos actualizar estos términos. Si el cambio es importante, te avisaremos en la app y, " +
                "cuando la ley lo requiera, volveremos a pedir tu consentimiento."
    ),
    Seccion(
        "14. Ley aplicable",
        "Estos términos se rigen por las leyes de la República del Perú. Cualquier controversia se " +
                "someterá a los jueces y tribunales de Lima, sin perjuicio de tus derechos como usuario " +
                "de servicios de salud y como consumidor."
    )
)

@Composable
fun TerminosScreen(onAtras: () -> Unit) {
    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(titulo = "Términos y Condiciones", onAtras = onAtras) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            // Zona con scroll para leer todo el texto
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Términos y Condiciones de Uso y Política de Privacidad",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AzulOscuro
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Clínica SaludPlus · App Paciente",
                    fontSize = 14.sp,
                    color = GrisTexto
                )
                Text(
                    text = "Última actualización: 4 de octubre de 2026",
                    fontSize = 13.sp,
                    color = GrisTexto
                )

                Spacer(Modifier.height(16.dp))

                // Aviso de versión de demostración
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFF1D6))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Versión de demostración: esta app es un proyecto académico. Los datos " +
                                "se guardan solo en la memoria del teléfono y se pierden al cerrar la app.",
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = Color(0xFF8A5A00)
                    )
                }

                secciones.forEach { seccion ->
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = seccion.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = seccion.cuerpo,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = GrisTexto
                    )
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Texto de ejemplo con fines educativos; no constituye asesoría legal.",
                    fontSize = 12.sp,
                    color = GrisTexto
                )
                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(8.dp))
            BotonAzul(texto = "Entendido", onClick = onAtras)
            Spacer(Modifier.height(16.dp))
        }
    }
}