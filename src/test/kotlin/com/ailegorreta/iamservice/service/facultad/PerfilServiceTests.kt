/* Copyright (c) 2023, LegoSoft Soluciones, S.C.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are not permitted.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 *  GrupoServiceTests.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */

package com.ailegorreta.iamservice.service.facultad

import com.ailegorreta.iamservice.EnableTestContainers
import com.ailegorreta.iamservice.service.AbstractServiceTest
import com.ailegorreta.iamservice.service.facultad.dto.PerfilDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import java.util.function.Consumer

/**
 * Test for the PerfilService.
 *
 * @author rlh
 * @project iam-service
 * @date September 2023
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableTestContainers
/* ^ This is a custom annotation to load the containers */
@ExtendWith(MockitoExtension::class)
@EmbeddedKafka(bootstrapServersProperty = "spring.kafka.bootstrap-servers")
/* ^ this is because: https://blog.mimacom.com/embeddedkafka-kafka-auto-configure-springboottest-bootstrapserversproperty/ */
@ActiveProfiles("integration-tests")
class PerfilServiceTests: AbstractServiceTest() {

    @Test
    fun `Add a new profile`(@Autowired perfilService: PerfilService) {
        val nombre = "Desarrollador"
        val perfilDTO = PerfilDTO(null, nombre, "Puesto de desarrollador",
                                  true, false, "TEST")

        assertThat(perfilService.add(perfilDTO))
            .satisfies(Consumer{ profile -> assertThat(profile?.nombre).isEqualTo(nombre) })
    }

    @Test
    fun `Assign a role to a profile`(@Autowired perfilService: PerfilService,
                                     @Autowired rolService: RolService) {
        // First we read a Profile that must exists
        val nombre = "Administrador"
        val profile = perfilService.findByNombre(nombre, false)
        val role = rolService.findByIdRol(6L)

        assertThat(profile).isPresent
        assertThat(role).isPresent

        assertThat(perfilService.assignRole(6L, profile.get()))
            .satisfies(Consumer{ p -> assertThat(p.perfilDTO.nombre).isEqualTo(nombre) })
    }
    @Test
    fun `Validate a role`(@Autowired perfilService: PerfilService) {
        var nombre = "Administrador"
        val profile = perfilService.findByNombre(nombre, true)

        assertThat(profile).isPresent
        println(" Perfil ${profile.get().nombre}")
        profile.get().roles.forEach {
            println(" Rol => ${it.nombre}")
            it.facultades.forEach {
                println("   Facultad = ${it.nombre}")
            }
        }
    }
}
