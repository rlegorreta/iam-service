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
 *  RolServiceTests.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */

package com.ailegorreta.iamservice.service.facultad

import com.ailegorreta.iamservice.EnableTestContainers
import com.ailegorreta.iamservice.service.AbstractServiceTest
import com.ailegorreta.iamservice.service.facultad.dto.RolDTO
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.function.Consumer

/**
 * Test for the RolService.
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
class RolServiceTests: AbstractServiceTest() {

    @Test
    fun `Add a new role`(@Autowired rolService: RolService,
                         @Autowired facultadService: FacultadService) {
        val nombre = "New role"
        val rolDTO = RolDTO(null, 142L, nombre, "TEST",
                            LocalDateTime.now(),true)

        /* This example shows that NO matter we put permits they are ignored in RolDTO */
        val permitOne = facultadService.findByNombre("auditoria")
        val permitTwo = facultadService.findByNombre("cache")

        assertThat(permitOne).isPresent
        assertThat(permitTwo).isPresent

        rolDTO.facultades = arrayListOf(permitOne.get(), permitTwo.get())
        // ^ this instructions is ignored in RolService in order to force to call assignPermit Tests

        assertThat(rolService.add(rolDTO))
            .satisfies(Consumer{ rol -> run {
                assertThat(rol?.nombre).isEqualTo(nombre)
                assertThat(rol?.facultades).isEmpty()
            } })
    }

    @Test
    fun `Assign an existing permit to a role`(@Autowired rolService: RolService) {
        val role = rolService.findByIdRol(550L)

        assertThat(role).isPresent

        assertThat(rolService.assignPermit("templates", role.get())) // the permit must exist in the DB
            .satisfies(Consumer{ rol -> assertThat(rol.rolDTO.idRol).isEqualTo(550L) })
    }

    @Test
    fun `Assign an existing permit to a new role`(@Autowired rolService: RolService) {
        val rolDTO = RolDTO(null, 4678L, "New Role", "TEST",
                             LocalDateTime.now(),true)

        assertThat(rolService.assignPermit("templates", rolDTO)) // the permit must exist in the DB
            .satisfies(Consumer{ rol -> Assertions.assertThat(rol.rolDTO.idRol).isEqualTo(4678L) })
    }
}
