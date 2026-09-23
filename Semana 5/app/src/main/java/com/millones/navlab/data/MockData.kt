package com.millones.navlab.data

object MockData {
    val usuarios = listOf(
        Usuario(
            id = "2026-0001",
            email = "juan.perez@navlab.edu.pe",
            password = "123",
            nombreCompleto = "Juan Pérez García",
            carrera = "Diseño y Desarrollo de Software",
            facultad = "Tecnología Digital",
            telefono = "+51 987 654 321",
            ciclo = "VI Ciclo",
            avatarUrl = "https://i.pravatar.cc/150?img=12",
            biografia = "Estudiante apasionado por el desarrollo móvil Android, arquitectura de software y experiencias de usuario con Jetpack Compose."
        ),
        Usuario(
            id = "2026-0002",
            email = "maria.lopez@navlab.edu.pe",
            password = "123",
            nombreCompleto = "María López Torres",
            carrera = "Ingeniería de Software",
            facultad = "Ingeniería y Computación",
            telefono = "+51 912 345 678",
            ciclo = "V Ciclo",
            avatarUrl = "https://i.pravatar.cc/150?img=47",
            biografia = "Entusiasta de la inteligencia artificial, desarrollo de aplicaciones móviles multiplataforma y sistemas distribuidos."
        ),
        Usuario(
            id = "2026-0003",
            email = "carlos.mendoza@navlab.edu.pe",
            password = "123",
            nombreCompleto = "Carlos Mendoza Rivas",
            carrera = "Redes y Telecomunicaciones",
            facultad = "Tecnología Digital",
            telefono = "+51 955 443 322",
            ciclo = "IV Ciclo",
            avatarUrl = "https://i.pravatar.cc/150?img=33",
            biografia = "Especialista en ciberseguridad, infraestructura de redes en la nube y optimización de servidores."
        )
    )
}

object UserSession {
    var currentUser: Usuario? = MockData.usuarios[0]
}