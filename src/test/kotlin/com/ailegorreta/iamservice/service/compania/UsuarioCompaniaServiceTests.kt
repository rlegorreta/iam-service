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
 *  UsuarioCompaniaServiceTests.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */

package com.ailegorreta.iamservice.service.compania

import com.ailegorreta.iamservice.EnableTestContainers
import com.ailegorreta.iamservice.repository.*
import com.ailegorreta.iamservice.service.AbstractServiceTest
import com.ailegorreta.iamservice.service.compania.dto.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.neo4j.driver.Driver
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.util.function.Consumer

/**
 * Test for the UsuariosCompaniaService.
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
class UsuarioCompaniaServiceTests: AbstractServiceTest() {

    @Test
    fun `Update User`(@Autowired usuarioCompaniaService: UsuarioCompaniaService,
                      @Autowired companiaService: CompaniaService) {
        val nombreUsuario = "adminACME"
        val user = usuarioCompaniaService.findByNombreUsuario(nombreUsuario)
        val company = companiaService.findByNombre("ACME SA de CV")

        assertThat(user).isPresent
        assertThat(company).isPresent

        user.get().companias = arrayListOf(company.get())

        assertThat(usuarioCompaniaService.addUser(user.get()).nombreUsuario).isEqualTo(nombreUsuario)
    }

    @Test
    fun `Find with id supervisor depth`(@Autowired driver: Driver,
                                        @Autowired usuarioCompaniaService: UsuarioCompaniaService) {
        processCypher(driver, "/testOne.cypher", false)

        val nombreUsuario = "adminACMEJr"
        val user = usuarioCompaniaService.findByNombreUsuario(nombreUsuario)

        assertThat(user).isPresent

        val result = usuarioCompaniaService.findByIdWithSupervisor(user.get().id!!, true)

        assertThat(result).isPresent
        assertThat(result.get().supervisor!!.nombreUsuario).isEqualTo("adminACME")
        assertThat(result.get().companias).isNotEmpty()
    }

    @Test
    fun `Find users with supervisor name`(@Autowired driver: Driver,
                                          @Autowired usuarioCompaniaService: UsuarioCompaniaService) {
        processCypher(driver, "/testOne.cypher", false)

        assertThat(usuarioCompaniaService.findUsuariosBySupervisor("adminACME").count()).isEqualTo(1L)
    }

    @Test
    fun `Company employees graph`(@Autowired usuarioCompaniaService: UsuarioCompaniaService) {
        val graph = usuarioCompaniaService.graphEmpleadosByAdministradorMaestro("adminIAM")

        assertThat(graph.nodes).isNotEmpty
        assertThat(graph.edges).isNotEmpty
        println("Nodes:")
        graph.nodes.forEach { println("    $it") }
        graph.edges.forEach { println("    $it") }
    }

    @Test
    fun `Add or update a user supervisor`(@Autowired usuarioCompaniaService: UsuarioCompaniaService) {
        val nombreUsuario = "adminACMET"
        val user = usuarioCompaniaService.findByNombreUsuario(nombreUsuario)
        val supervisor = usuarioCompaniaService.findByNombreUsuario("adminACME")

        assertThat(user).isPresent
        assertThat(supervisor).isPresent

        user.get().supervisor = supervisor.get()        // assign as supervisor

        assertThat(usuarioCompaniaService.addUser(user.get()).nombreUsuario).isEqualTo(nombreUsuario)
    }

    @Test
    fun `New member Administrator for an existing group`(@Autowired groupService: GrupoService,
                                                         @Autowired usuarioCompaniaService: UsuarioCompaniaService) {
        // The user can exist or be a new user. This test is for a new user
        val nombreUsuario = "jperez"
        val adminDTO = UsuarioDTO(null, 1550, nombreUsuario, "Juan","Perez",
                "5591495042", "staff@legosoft.com.mx",
                false, true, true,
                LocalDate.now(), "America/Mexico","TEST")

        val group = groupService.findByNombre("Admin AI/ML")

        assertThat(group).isPresent
        assertThat(usuarioCompaniaService.newMember(group.get().id!!, adminDTO))
                .satisfies(Consumer{ admin -> Assertions.assertThat(admin?.nombreUsuario).isEqualTo(nombreUsuario) })
    }

    /**
     * This is the same case as above but the user already exists in the DB
     */
    @Test
    fun `New member existing Administrator for an existing group`(@Autowired grupoService: GrupoService,
                                                                  @Autowired usuarioCompaniaService: UsuarioCompaniaService) {
        // First we read a user that must exists
        val nombreUsuario = "adminACMET"
        val admin = usuarioCompaniaService.findByNombreUsuario(nombreUsuario)
        val group = grupoService.findByNombre("Admin AI/ML")

        assertThat(admin).isPresent
        assertThat(group).isPresent

        assertThat(usuarioCompaniaService.newMember(group.get().id!!, admin.get()))
            .satisfies(Consumer{ user -> assertThat(user?.nombreUsuario).isEqualTo(nombreUsuario) })
    }

    @Test
    fun `New User normal`(@Autowired companiaService: CompaniaService,
                          @Autowired usuarioCompaniaService: UsuarioCompaniaService) {
        // The user must not exist
        val nombreUsuario = "jperez"
        val usuarioDTO = UsuarioDTO(null, 1021L, nombreUsuario, "Juan", "Perez",
                "5591495042", "staff@legosoft.com.mx",
                false, true, false,
                LocalDate.now(), "America/Mexico","TEST")

        val company = companiaService.findByNombre("ACME SA de CV")

        assertThat(company).isPresent
        usuarioDTO.companias = listOf(company.get())

        assertThat(usuarioCompaniaService.addUser(usuarioDTO))
                .satisfies(Consumer{ newUser -> assertThat(newUser?.nombreUsuario).isEqualTo(nombreUsuario) })
    }

    @Test
    fun `Assign a user to an existing Company`(@Autowired usuarioCompaniaService: UsuarioCompaniaService,
                                               @Autowired companiaService: CompaniaService) {
        val nombreUsuario = "adminACMET"
        val user = usuarioCompaniaService.findByNombreUsuario(nombreUsuario)
        val company = companiaService.findByNombre("AI/ML SA de CV")

        assertThat(user).isPresent
        assertThat(company).isPresent
        user.get().companias = arrayListOf(company.get())

        assertThat(usuarioCompaniaService.addUser(user.get()))
                .satisfies(Consumer{ u -> assertThat(u?.nombreUsuario).isEqualTo(nombreUsuario) })
    }

}

