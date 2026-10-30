package com.tecsup.huellitasvet.data.repository

import com.tecsup.huellitasvet.data.local.entity.Administrador
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.local.entity.Usuario
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.local.relation.MascotaConDueno
import com.tecsup.huellitasvet.model.ConteoServicio
import com.tecsup.huellitasvet.model.EstadoCita
import com.tecsup.huellitasvet.model.PuntoPeso
import com.tecsup.huellitasvet.model.Rol
import com.tecsup.huellitasvet.model.UltimaVisita
import kotlinx.coroutines.flow.Flow

interface VetRepository {
    // Usuarios
    suspend fun login(correo: String, clave: String): Usuario?
    suspend fun obtenerUsuario(id: Int): Usuario?
    suspend fun obtenerUsuarioPorRol(rol: Rol): Usuario?
    fun observarDuenos(): Flow<List<Usuario>>
    suspend fun loginAdministrador(correo: String, clave: String): Administrador?
    suspend fun obtenerAdministrador(id: Int): Administrador?
    fun observarAdministradores(): Flow<List<Administrador>>

    // Servicios (ejemplo resuelto)
    fun observarServicios(): Flow<List<Servicio>>
    fun observarServiciosActivos(): Flow<List<Servicio>>
    suspend fun guardarServicio(s: Servicio)
    suspend fun eliminarServicio(s: Servicio)
    suspend fun cambiarActivo(id: Int, activo: Boolean)
    suspend fun servicioTieneCitas(id: Int): Boolean

    // Mascotas
    fun observarMascotasDeDueno(duenoId: Int): Flow<List<Mascota>>
    fun observarMascota(id: Int): Flow<Mascota?>
    fun observarTodasLasMascotas(): Flow<List<MascotaConDueno>>
    fun observarUltimasVisitas(): Flow<List<UltimaVisita>>
    suspend fun guardarMascota(m: Mascota)
    suspend fun eliminarMascota(m: Mascota)
    suspend fun mascotaTieneCitas(id: Int): Boolean

    // Citas
    fun observarHorasOcupadas(fecha: String): Flow<List<String>>
    fun observarFechasConCitas(fechaInicio: String, fechaFin: String): Flow<List<String>>
    suspend fun crearCita(mascotaId: Int, servicioId: Int, fecha: String,
                          hora: String, nota: String): Long
    fun observarCitasDeDueno(duenoId: Int): Flow<List<CitaConDetalle>>
    fun observarCitasPorFecha(fecha: String): Flow<List<CitaConDetalle>>
    fun observarCita(id: Int): Flow<CitaConDetalle?>
    fun observarHistorial(mascotaId: Int): Flow<List<CitaConDetalle>>
    fun observarPesos(mascotaId: Int): Flow<List<PuntoPeso>>
    suspend fun cambiarEstadoCita(id: Int, estado: EstadoCita)
    suspend fun reprogramarCita(id: Int, fecha: String, hora: String)
    suspend fun registrarAtencion(citaId: Int, diagnostico: String,
                                  tratamiento: String, peso: Double)

    // Panel
    fun observarCitasDeFecha(fecha: String): Flow<Int>
    fun observarCantidadPorEstado(estado: EstadoCita): Flow<Int>
    fun observarCitasPorServicio(): Flow<List<ConteoServicio>>
    fun observarIngresosDesde(fechaInicio: String): Flow<Double>
}
