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
 *  CompaniaServiceTests.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */

package com.ailegorreta.iamservice.service.compania

import com.ailegorreta.iamservice.model.Negocio
import java.time.LocalDate
import com.ailegorreta.iamservice.EnableTestContainers
import com.ailegorreta.iamservice.repository.AreaAsignadaRepository
import com.ailegorreta.iamservice.service.AbstractServiceTest
import com.ailegorreta.iamservice.service.compania.dto.AreaDTO
import com.ailegorreta.iamservice.service.compania.dto.CompaniaDTO
import com.ailegorreta.iamservice.service.compania.dto.UsuarioDTO
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime
import java.util.function.Consumer

/**
 * Test for the CompaniaService.
 *
 * @author rlh
 * @project: iam-service
 * @date September 2023
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableTestContainers
/* ^ This is a custom annotation to load the containers */
@ExtendWith(MockitoExtension::class)
@EmbeddedKafka(bootstrapServersProperty = "spring.kafka.bootstrap-servers")
/* ^ this is because: https://blog.mimacom.com/embeddedkafka-kafka-auto-configure-springboottest-bootstrapserversproperty/ */
@ActiveProfiles("integration-tests")
class CompaniaServiceTests: AbstractServiceTest() {

    @Test
    fun `New corporate`(@Autowired companiaService: CompaniaService,
                                                 @Autowired grupoService: GrupoService,
                                                 @Autowired usuarioCompaniaService: UsuarioCompaniaService) {
        // Note the user and the company must be new
        val nombre = "New Company"
        val idUsuario = 11000L
        val corpDTO = CompaniaDTO(0L, nombre, true, Negocio.GOBIERNO,
                                "TEST")
        val adminDTO = UsuarioDTO(0L, idUsuario, "rlh", "Ricardo","Legorreta",
                                    "5591495042", "staff@legosoft.com.mx",
                                    false, true, true,
                                    LocalDate.now(),
                                    "America/Mexico", "TEST")

        assertThat(companiaService.newCorporate(corpDTO, adminDTO))
                                  .satisfies(Consumer{ company -> assertThat(company?.nombre).isEqualTo(nombre) })
        assertThat(companiaService.findByNombre(nombre)).isPresent
        assertThat(grupoService.findByNombre(CompaniaService.GRUPO_ADMIN_NAME + nombre)).isPresent
        assertThat(usuarioCompaniaService.findByIdUsuario(idUsuario, false)).isPresent
    }

    @Test
    fun `New Area with Company sync with AreaAsignada`(@Autowired companiaService: CompaniaService,
                                                       @Autowired areaAsignadaRepository: AreaAsignadaRepository) {
        val compania = "ACME SA de CV"
        val nombre = "Sistemas"
        val areaDTO = AreaDTO(0L, nombre,
                            usuarioModificacion = "TEST", fechaModificacion = LocalDateTime.now(),
                            activo = true, idArea = 333L, idPersona = 40L)

        assertThat(companiaService.newArea(compania, areaDTO))
                                  .satisfies(Consumer{ area -> assertThat(area?.nombre).isEqualTo(nombre) })
        assertThat(areaAsignadaRepository.findByNombre(nombre)).isPresent
    }

    @Test
    fun `See the D3js graph for a company`(@Autowired companiaService: CompaniaService) {
        val administrator = "adminMACME"

        assertThat(companiaService.graphCompaniasByAdministrador(administrator))
            .satisfies(Consumer{ graph -> run {
                assertThat(graph.nodes).isNotEmpty
                println("Nodes:")
                graph.nodes.forEach{ println("  $it")}
                println("Edges:")
                graph.edges.forEach{ println("  $it")}
            }})
    }

}
