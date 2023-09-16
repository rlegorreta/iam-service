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
 *  UsuarioServiceTests.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.facultad

import com.ailegorreta.iamservice.EnableTestContainers
import com.ailegorreta.iamservice.service.AbstractServiceTest
import com.ailegorreta.iamservice.service.facultad.dto.AssignPerfilDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import java.util.function.Consumer

/**
 * Test for the UsuarioFactultadService.
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
class UsuarioServiceTests: AbstractServiceTest() {

    @Test
    fun assignProfileTest(@Autowired usuarioService: UsuarioService) {
        val nombreUsuario = "adminACMEB"
        val user = usuarioService.findByNombreUsuario(nombreUsuario)

        assertThat(user).isPresent
        // note: the Profile must exist in the database
        assertThat(usuarioService.assignProfile(AssignPerfilDTO("Abogado", user.get())))
            .satisfies(Consumer { u -> assertThat(u?.usuarioDTO!!.nombreUsuario).isEqualTo(nombreUsuario) })
    }

    @Test
    fun `Assign an extisting permit to the user`(@Autowired usuarioService: UsuarioService) {
        val nombreUsuario = "adminACMEB"
        val user = usuarioService.findByNombreUsuario(nombreUsuario)

        assertThat(user).isPresent
        // note: the  Permit must exist in the database
        assertThat(usuarioService.assignFacultad(AssignPerfilDTO("templates", user.get())))
            .satisfies(Consumer { u -> assertThat(u?.usuarioDTO!!.nombreUsuario).isEqualTo(nombreUsuario) })
    }

    @Test
    fun `Add a forbidden permit to an existing user`(@Autowired usuarioService: UsuarioService) {
        val nombreUsuario = "adminACMEB"
        val user = usuarioService.findByNombreUsuario(nombreUsuario)

        assertThat(user).isPresent
        // note: the  Permit must exist in the database
        assertThat(usuarioService.forbidFacultad(AssignPerfilDTO("templates", user.get())))
            .satisfies(Consumer { u -> assertThat(u?.usuarioDTO!!.nombreUsuario).isEqualTo(nombreUsuario) })
    }

    @Test
    fun `Find employees by permit`(@Autowired usuarioService: UsuarioService) {
        assertThat(usuarioService.findEmpleadosByFacultad("LMASS Desarrolladores SA de CV",
                                        "adminIAM").size).isEqualTo(3L)
    }
}
