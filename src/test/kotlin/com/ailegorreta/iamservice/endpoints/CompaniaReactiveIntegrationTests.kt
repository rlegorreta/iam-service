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
 *  CompaniaReactiveIntegrationTests.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.endpoints

import com.ailegorreta.iamservice.model.Compania
import com.ailegorreta.iamservice.model.Negocio
import com.ailegorreta.iamservice.service.compania.CompaniaService
import com.ailegorreta.iamservice.service.compania.dto.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono
import java.time.LocalDate

/**
 *  This test class uses Spring WebFlux testing REST so the UserContext cannot be utilized
 *  because it runs in a different thread.
 *
 *  When we want to send events to the event logger, therefore need to use the UserContext
 *  we cannot use the WebTestClient class
 *
 * @author rlh
 * @date July 2023
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient(timeout = "20000") //20 seconds
@TestPropertySource(properties = arrayOf("spring.cloud.discovery.enabled = false",        // disable Eureka for testing
                                         "spring.cloud.config.uri = http://localhost:8071",
                                         "spring.profiles.active = localNoDocker"))
class CompaniaReactiveIntegrationTests(@Autowired val client: WebTestClient) {

    /**
     * note : To simplify the REST testing we disabled the token checking. This is done in the SecurityConfiguration
     *        class and the method ResourceServerConfiguration.
     *        Before deployment the toen has to be enabled again
     */

    @DisplayName("Read all companies to test the REST")
    @Test
    fun listAllCompanies() {
        client.get().uri("/iam/compania/companias")
                    .exchange()
                    .expectStatus().isOk
                    .expectBodyList(Compania::class.java).hasSize(6)
    }

    @DisplayName("Read one company by name")
    @Test
    fun findCompanyByName() {
        client.get().uri("/iam/compania/compania/by/nombre?nombre=LegoSoft")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody<CompaniaDTO>()
                    .consumeWith { result ->
                         val compania: CompaniaDTO = result.responseBody!!
                            assertThat(compania.nombre).isEqualTo("LegoSoft")
                    }
    }

    @Test
    fun newOperadora() {
        val corpDTO = CompaniaDTO(null, "Televisa", true, Negocio.INDUSTRIAL,
                                    "TEST")
        val adminDTO = UsuarioDTO(null, 450L, "emilio", "Emilio", "Azcarraga",
                                  "5591495042", "staff@legosoft.com.mx",
                                  true, true, true, LocalDate.now(), "America/Mexico", "TEST")
        val newCorpDTO = NewCorporativoDTO(corpDTO, adminDTO)

        client.post().uri("compania/nuevo/corporativo")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(Mono.just(newCorpDTO), NewCorporativoDTO::class.java)
                     .exchange()
                     .expectStatus().isOk
                     .expectBody<CompaniaDTO>()
                     .consumeWith { result ->
                        val newCorpResDTO: CompaniaDTO = result.responseBody!!
                            assertThat(newCorpResDTO.nombre).isEqualTo("Televisa")
                     }
    }

    @Test
    fun newGrupo(@Autowired companiaService: CompaniaService) {
        val grupoDTO = GrupoDTO(null, "Otro grupo de Televisa", true, "TEST")
        val ciaDTO = companiaService.findByNombre("Televisa")
        val newGrupoDTO = NewGrupoDTO(grupoDTO, "emilio")        // The administrator must exists

        assertThat(ciaDTO.isPresent).isTrue()

        val cias: ArrayList<CompaniaDTO> = ArrayList()

        cias.add(ciaDTO.get())
        grupoDTO.permiteCompanias = cias
        client.post().uri("/iam/compania/grupo/add")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(Mono.just(newGrupoDTO), NewGrupoDTO::class.java)
                     .exchange()

                     .expectStatus().isOk
                     .expectBody<GrupoDTO>()
                     .consumeWith { result ->
                         val newGrupoResDTO: GrupoDTO = result.responseBody!!
                         assertThat(newGrupoResDTO.nombre).isEqualTo("Otro grupo de Televisa")
                     }
    }
}
