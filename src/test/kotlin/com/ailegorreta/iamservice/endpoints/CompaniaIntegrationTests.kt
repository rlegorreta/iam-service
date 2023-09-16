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
 *  CompaniaIntegrationTests.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.endpoints

import java.util.*
import java.time.*

import com.fasterxml.jackson.databind.ObjectMapper
import com.ailegorreta.iamservice.model.Negocio
// import com.ailegorreta.resourceserver.utils.UserContext
import com.ailegorreta.iamservice.service.compania.CompaniaService
import com.ailegorreta.iamservice.service.compania.GrupoService
import com.ailegorreta.iamservice.service.compania.UsuarioCompaniaService
import com.ailegorreta.iamservice.service.compania.dto.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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
@AutoConfigureMockMvc
@TestPropertySource(properties = arrayOf("spring.cloud.discovery.enabled = false",        // disable Eureka for testing
                                         "spring.cloud.config.uri = http://localhost:8071",
                                         "spring.profiles.active = localNoDocker"))
class CompaniaIntegrationTests(@Autowired val client: MockMvc) {

    /**
     * note: Before test modifications that generates events in the eventlogger micro.service service. In order
     *      to send correctly the POST REST we need to comment the @LoadBalance directive (and not use Ribbon)
     *      en the main class IamApplication.
     *
     * The user context must be sent inorder to send correctly the event
     */
    @BeforeEach
    fun setUserContext() {
        // UserContext.setCorrelationId( "someCorrelationId")

    }

    /**
     * note : To simplify the REST testing we disabled the token checking. This is done in the SecurityConfiguration
     *        class and the method ResourceServerConfiguration.
     *        Before deployment the toen has to be enabled again
     */

    @DisplayName("Read all companies to test the REST")
    @Test
    fun listAllCompanies()  {
        client.perform(get("/iam/compania/companias"))
              .andExpect(status().isOk)
              .andExpect(jsonPath("$.length()").value(7));
    }

    @DisplayName("Read one company by name")
    @Test
    fun findCompanyByName() {
        client.perform(get("/iam/compania/compania/by/nombre?nombre=LegoSoft"))
              .andExpect(status().isOk)
              .andExpect(jsonPath("\$.nombre").value("LegoSoft"));
    }

    // @Test
    fun newOperadora(@Autowired mapper: ObjectMapper) {
        val corpDTO = CompaniaDTO(null, "Televisa", true, Negocio.INDUSTRIAL,
                                    "TEST")
        val adminDTO = UsuarioDTO(null, 450L, "emilio", "Emilio", "Azcarraga",
                "5591495042", "staff@legosoft.com.mx",
                true, true, true, LocalDate.now(),  "America/Mexico", "TEST")
        val newCorpDTO = NewCorporativoDTO(corpDTO, adminDTO)
        var newCorp: String? = null

        try {
            newCorp = mapper.writeValueAsString(newCorpDTO)
            println("======== Before add a new Operadora =>$newCorp")
        } catch (e: Exception) {
            e.printStackTrace()
            assertThat(false)
        }

        client.perform(post("/iam/compania/compania/nuevo/corporativo")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(newCorp!!))
              .andExpect(status().isOk)
              .andExpect(jsonPath("\$.nombre").value("Televisa"));
    }

    // @Test
    fun newGrupo(@Autowired mapper: ObjectMapper,
                 @Autowired companiaService: CompaniaService) {
        val grupoDTO = GrupoDTO(null, "Otro grupo de BlackRock", true, "TEST")
        val ciaDTO = companiaService.findByNombre("BlackRock")
        val newGrupoDTO = NewGrupoDTO(grupoDTO, "Agente4")        // The administrator must exists
        var newGrupo: String? = null

        assertThat(ciaDTO.isPresent).isTrue()

        val cias: ArrayList<CompaniaDTO> = ArrayList()

        cias.add(ciaDTO.get())
        grupoDTO.permiteCompanias = cias
        try {
            newGrupo = mapper.writeValueAsString(newGrupoDTO)
            println("======== Before add a new Grupo =>$newGrupo")
        } catch (e: Exception) {
            e.printStackTrace()
            assertThat(false)
        }
        client.perform(post("/iam/compania/grupo/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newGrupo!!))
              .andExpect(status().isOk)
              .andExpect(jsonPath("\$.nombre").value("Otro grupo de Televisa"))
    }

    // @Test
    fun updateLinkGrupo(@Autowired mapper: ObjectMapper,
                        @Autowired companiaService: CompaniaService,
                        @Autowired grupoService: GrupoService) {
        val ciasSinHerenecia = ArrayList<CompaniaDTO>()
        val ciaDTO = companiaService.findByNombre("Televisa")
        val grupoDTO = grupoService.findByNombre("Otro grupo de Televisa")

        assertThat(ciaDTO.isPresent).isTrue()
        assertThat(grupoDTO.isPresent).isTrue()
        ciasSinHerenecia.add(ciaDTO.get())

        val updateGrupoDTO = UpdateGrupoDTO(grupoDTO.get().id!!,
                                            "TEST",
                                            null,  // Do not touch link PERMITE
                                            null, // Do not touch PERMITE_AREA
                                            ArrayList<CompaniaDTO>()) // add PERMITE_SIN_HERENCIA
        var updateGrupo: String? = null

        try {
            updateGrupo = mapper.writeValueAsString(updateGrupoDTO)
            println("======== Before update grupo links =>$updateGrupo")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            assertThat(false)
        }
        client.perform(post("/iam/compania/grupo/actualiza")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateGrupo!!))
              .andExpect(status().isOk)
              .andExpect(jsonPath("\$.nombre").value("Otro grupo de Televisa"))
    }

    @DisplayName("Read one usuario by his(ver) id with supervisor")
    @Test
    fun findUsuarioByIdWithSupervisor(@Autowired usuarioCompaniaService: UsuarioCompaniaService) {
        val usuarioDTO = usuarioCompaniaService.findByNombreUsuario("Agente4")

        assertThat(usuarioDTO.isPresent).isTrue()

        client.perform(get("/iam/facultad/usuario/admin/withsupervisor/by/id?id=" + usuarioDTO.get().id!!))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.nombre").value("Agente4"));
    }

    @DisplayName("Read one usuario by his(ver) idUsuario")
    @Test
    fun findUsuarioByIdUsuario() {
        client.perform(get("/iam/facultad/usuario/admin/by/idUsuario?idUsuario=507"))
              .andExpect(status().isOk)
              .andExpect(jsonPath("\$.nombre").value("AdminBR"));
    }

    // @Test
    fun newAdmin(@Autowired mapper: ObjectMapper,
                 @Autowired grupoService: GrupoService) {
        val adminDTO = UsuarioDTO(null, 1001,"llegarreta", "Luis","Legarreta",
                                  "5591495042", "staff@legosoft.com.mx",
                                  false, true, true,
                                  LocalDate.now(), "America/Mexico", "TEST")
        val grupoDTO = grupoService.findByNombre("Otro grupo de Televisa")

        assertThat(grupoDTO.isPresent).isTrue()

        val newAdminDTO = NewAdminDTO(grupoDTO.get().id!!, adminDTO)
        var newAdmin: String? = null

        try {
            newAdmin = mapper.writeValueAsString(newAdminDTO)
            println("======== Before new Administrator =>$newAdmin")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            assertThat(false)
        }
        client.perform(post("/iam/facultad/usuario/admin/nuevo/admin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newAdmin!!))
              .andExpect(status().isOk)
              .andExpect(jsonPath("\$.nombre").value("llegarreta"))
    }

    // @Test
    fun testNewUser(@Autowired mapper: ObjectMapper,
                    @Autowired companiaService: CompaniaService) {
        val usuarioDTO = UsuarioDTO(null, 1002L, "gcanedo", "Guillermo","Canedo",
                                    "5591495042", "staff@legosoft.com.mx",
                                    false, true, false,
                                    LocalDate.now(), "America/Mexico","TEST")
        val legosoft = companiaService.findByNombre("LegoSoft")

        assertThat(legosoft.isPresent).isTrue()

        val cias: ArrayList<CompaniaDTO> = ArrayList<CompaniaDTO>()
        var newUser: String? = null

        cias.add(legosoft.get())
        usuarioDTO.companias = cias
        try {
            newUser = mapper.writeValueAsString(usuarioDTO)
            println("======== Before new User is inserted =>$newUser")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            assertThat(false)
        }
        client.perform(post("/iam/facultad/usuario/admin/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newUser!!))
               .andExpect(status().isOk)
               .andExpect(jsonPath("\$.nombre").value("gcanedo"))
    }

    @DisplayName("Read what companies an Administrator has access")
    @Test
    fun findAdminCompanies() {
        client.perform(get("/usuario/admin/companias?nombreAdministrador=AdminIAM"))
              .andExpect(status().isOk)
              .andExpect(jsonPath("$.length()").value(4));
    }

    @DisplayName("Read what employees an Administrator has access")
    @Test
    fun findAdminEmployees() {
        client.perform(get("/iam/facultad/usuario/admin/empleados?nombreAdministrador=AdminGAR"))
              .andExpect(status().isOk)
              .andExpect(jsonPath("$.length()").value(1));
    }

    @DisplayName("Read employees grafo that an Administrator has access")
    @Test
    fun findGraphoAdminEmployees() {
        client.perform(get("/iam/facultad/usuario/admin/grafo/empleados?nombreAdministrador=AdminGAR"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(2)) // Nodes and links
    }

    @DisplayName("Read what employees an Administrator maestro has access")
    @Test
    fun findAdminMaestroEmployees() {
        client.perform(get("/iam/facultad/usuario/admin/maestro/empleados?nombreAdministrador=AdminIAM"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(6));
    }

    @DisplayName("Read employees grafo that an Administrator maestro has access")
    @Test
    fun findGraphoAdminMaestroEmployees() {
        client.perform(get("/iam/facultad/usuario/admin/maestro/grafo/empleados?nombreAdministrador=AdminIAM"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(2)) // Nodes and links
    }

    @DisplayName("Read employees for a supervisor")
    @Test
    fun findUsuariosBySupervisor(@Autowired usuarioCompaniaService: UsuarioCompaniaService) {
        client.perform(get("/iam/facultad/usuarios/admin/by/supervisor?nombreSupervisor=AdminIAM"))
              .andExpect(status().isOk)
              .andExpect(jsonPath("$.length()").value(4)) // Nodes and links
    }

    @DisplayName("Read Operadoreas count byname and negocio <> NA ")
    @Test
    fun findCompaniasByName() {
        client.perform(get("/iam/compania/companias/nombre/count?nombre=Af"))
              .andExpect(status().isOk)

    }
}
