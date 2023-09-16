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
 *  FacultadIntegrationTests.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.endpoints

import com.fasterxml.jackson.databind.ObjectMapper
import com.ailegorreta.iamservice.model.FacultadTipo
import com.ailegorreta.iamservice.service.facultad.*
import com.ailegorreta.iamservice.service.facultad.dto.*
// import com.lmass.resourceserver.utils.UserContext
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
import java.time.LocalDateTime

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
class FacultadIntegrationTests(@Autowired val client: MockMvc) {

    /**
     * note: Before test modifications that generates events in the eventlogger micro.service service. In order
     *      to send correctly the POST REST we need to comment the @LoadBalance directive (and not use Ribbon)
     *      en the main class IamRepoApplication.
     *
     * The user context must be sent inorder to send correctly the event
     */
    @BeforeEach
    fun setUserContext() {
        // UserContext.setCorrelationId("someCorrelationId")
    }

    /**
     * note : To simplify the REST testing we disabled the token checking. This is done in the SecurityConfiguration
     *        class and the method ResourceServerConfiguration.
     *        Before deployment the toen has to be enabled again
     */

    @DisplayName("Read all users to test the REST")
    @Test
    fun listAllUsuarios()  {
        client.perform(get("/iam/facultad/usuarios"))
              .andExpect(status().isOk)
              .andExpect(jsonPath("$.length()").value(12));
    }

    @DisplayName("Read one user by IdUsuario")
    @Test
    fun findUsuarioByIdUsuario() {
        client.perform(get("/iam/facultad/usuario/by/idUsuario?idUsuario=0"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.nombre").value("AdminIAM"));
    }

    //@Test
    fun assignProfileToUser(@Autowired mapper: ObjectMapper,
                            @Autowired usuarioService: UsuarioService) {
        val userDTO = usuarioService.findByNombreUsuario("Agente4")

        assertThat(userDTO.isPresent).isTrue()

        val assignPerfilDTO = AssignPerfilDTO("Contab", userDTO.get())
        var assignPerfil: String? = null

        try {
            assignPerfil = mapper.writeValueAsString(assignPerfilDTO)
            println("======== Before assign perfil to Usuario")
        } catch (e: Exception) {
            e.printStackTrace()
            assertThat(false)
        }

        client.perform(post("/iam/facultad/usuario/asigna/perfil")
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignPerfil!!))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.nombre").value("Contab"));
    }

    // @Test
    fun testAssignPermitToUser(@Autowired mapper: ObjectMapper,
                               @Autowired usuarioService: UsuarioService) {
        val usuarioDTO = usuarioService.findByNombreUsuario("Agente3")

        assertThat(usuarioDTO.isPresent).isTrue()

        val assignFacultadDTO = AssignPerfilDTO("VECTOR_PRECIOS", usuarioDTO.get())
        var assingFacultad: String? = null
        try {
            assingFacultad = mapper.writeValueAsString(assignFacultadDTO)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            assertThat(false)
        }
        client.perform(post("/iam/facultad/usuario/asigna/facultad")
                .contentType(MediaType.APPLICATION_JSON)
                .content(assingFacultad!!))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.nombre").value("VECTOR_PRECIOS"))
    }

    // @Test
    fun testForbidPermitToUser(@Autowired mapper: ObjectMapper,
                               @Autowired usuarioService: UsuarioService) {
        val usuarioDTO = usuarioService.findByNombreUsuario("Agente3")

        assertThat(usuarioDTO.isPresent).isTrue()

        val forbidFacultadDTO = AssignPerfilDTO("GEN_CONTABLE", usuarioDTO.get())
        var forbidFacultad: String? = null
        try {
            forbidFacultad = mapper.writeValueAsString(forbidFacultadDTO)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            assertThat(false)
        }
        client.perform(post("/iam/facultad/usuario/prohibe/facultad")
                .contentType(MediaType.APPLICATION_JSON)
                .content(forbidFacultad!!))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.nombre").value("GEN_CONTABLE"))
    }

    @DisplayName("Read Facultades de un usuario")
    @Test
    fun findPermitByUser() {
        client.perform(get("/iam/facultad/usuario/facultades?nombre=Agente3"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.length()").value(2));
    }

    @DisplayName("Read employees grafo permits")
    @Test
    fun findGraphoFacultadesByUsuario() {
        client.perform(get("/iam/facultad/usuario/grafo/facultades?nombre=AdminIAM"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(2)) // Nodes and links
    }

    @DisplayName("Test that the usuario has this permit")
    @Test
    fun findHasPermitByUser() {
        client.perform(get("/iam/facultad/usuario/has/facultad?nombreUsuario=Agente3&nombreFacultad=VECTOR_PRECIOS"))
                .andExpect(status().isOk)
    }

    @DisplayName("  Read all employees with a permit in the same company")
    @Test
    fun findEmpleadosByFacultad() {
        client.perform(get("/iam/facultad/usuarios/by/facultad?nombreCompania=LegoSoft&nombreFacultad=GEN_CONTABLE"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(4)) // Nodes and links
    }

    @DisplayName("Read one facultad by nombre")
    @Test
    fun findFacultadByNombre() {
        client.perform(get("/iam/facultad/facultad/by/nombre?nombre=VECTOR_PRECIOS"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.nombre").value("VECTOR_PRECIOS"));
    }

    // @Test
    fun newFacultad(@Autowired mapper: ObjectMapper) {
        val newFacultadDTO = FacultadDTO(null, "NUEVA_FACULTAD","Nueva facultad",
                                    FacultadTipo.SIMPLE, "TEST", LocalDateTime.now(),
                                    true)
        var newFacultad: String? = null

        try {
            newFacultad = mapper.writeValueAsString(newFacultadDTO)
            println("======== Before new Facultad =>$newFacultad")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            assertThat(false)
        }
        client.perform(post("/iam/facultad/facultad/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newFacultad!!))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.nombre").value("NUEVA_FACULTAD"))
    }

    @DisplayName("Read one Rol by idRol")
    @Test
    fun findRolByIdRol() {
        client.perform(get("/iam/facultad/rol/by/idRol?idRol=0"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.nombre").value("Admin IAM"));
    }

    // @Test
    fun newRol(@Autowired mapper: ObjectMapper) {
        val newRolDTO = RolDTO(null, 1000, "Nuevo rol", "TEST",
                LocalDateTime.now(),true)
        var newRol: String? = null

        try {
            newRol = mapper.writeValueAsString(newRolDTO)
            println("======== Before new Rol =>$newRol")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            assertThat(false)
        }
        client.perform(post("/iam/facultad/rol/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newRol!!))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.nombre").value("Nuevo rol"))
    }

    // @Test
    fun addFacultadToRol(@Autowired mapper: ObjectMapper,
                         @Autowired rolService: RolService) {
        val rolDTO = rolService.findByIdRol(1000)

        assertThat(rolDTO.isPresent).isTrue()

        val assignFacultadDTO = AssignFacultadDTO("NUEVA_FACULTAD", rolDTO.get())
        var assignFacultad: String? = null
        try {
            assignFacultad = mapper.writeValueAsString(assignFacultadDTO)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            assertThat(false)
        }
        client.perform(post("/iam/facultad/rol/add/facultad")
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignFacultad!!))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.nombre").value("NUEVA_FACULTAD"))
    }

    // @Test
    fun unassignedFacultadToRol(@Autowired mapper: ObjectMapper,
                               @Autowired rolService: RolService) {
        val rolDTO = rolService.findByIdRol(1000)

        assertThat(rolDTO.isPresent).isTrue()

        val unAssignFacultadDTO = AssignFacultadDTO("NUEVA_FACULTAD", rolDTO.get())
        var unAssignFacultad: String? = null
        try {
            unAssignFacultad = mapper.writeValueAsString(unAssignFacultadDTO)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            assertThat(false)
        }
        client.perform(post("/iam/facultad/rol/delete/facultad")
                .contentType(MediaType.APPLICATION_JSON)
                .content(unAssignFacultad!!))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.nombre").value("NUEVA_FACULTAD"))
    }

    @DisplayName("Read one Profile by name")
    @Test
    fun findPerfilByNombre() {
        client.perform(get("/iam/facultad/perfil/by/nombre?nombre=Contab"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.nombre").value("Contab"));
    }

    @DisplayName("Read one Profile and return its permits")
    @Test
    fun findFacultadesByPerfil() {
        client.perform(get("/iam/facultad/perfil/facultades?nombre=Administrador IAM"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.length()").value(4));
    }

    @DisplayName("Read profile grafo permits")
    @Test
    fun findGrafoFacultadesByPerfil() {
        client.perform(get("/iam/facultad/perfil/grafo/facultades?nombre=Administrador IAM"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(2)) // Nodes and links
    }

    // @Test
    fun newPerfil(@Autowired mapper: ObjectMapper) {
        val newPerfilDTO = PerfilDTO(null, "Director", "Nuevo Director", true,
                false, "TEST", LocalDateTime.now())
        var newPerfil: String? = null

        try {
            newPerfil = mapper.writeValueAsString(newPerfilDTO)
            println("======== Before new Perfil =>$newPerfil")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            assertThat(false)
        }
        client.perform(post("/iam/facultad/perfil/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newPerfil!!))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.nombre").value("Director"))
    }

    // @Test
    fun addRolToPerfil(@Autowired mapper: ObjectMapper,
                       @Autowired perfilService: PerfilService) {
        val perfilDTO = perfilService.findByNombre("Director")

        assertThat(perfilDTO.isPresent).isTrue()

        val assignRolDTO = AssignRolDTO(1000, perfilDTO.get())
        var assignRol: String? = null
        try {
            assignRol = mapper.writeValueAsString(assignRolDTO)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            assertThat(false)
        }
        client.perform(post("/iam/facultad/perfil/add/rol")
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignRol!!))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.idRol").value(1000))
    }

    // @Test
    fun unAssignRolToPerfil(@Autowired mapper: ObjectMapper,
                            @Autowired perfilService: PerfilService) {
        val perfilDTO = perfilService.findByNombre("Director")

        assertThat(perfilDTO.isPresent).isTrue()

        val unAssignRolDTO = AssignRolDTO(1000, perfilDTO.get())
        var unAassignRol: String? = null
        try {
            unAassignRol = mapper.writeValueAsString(unAssignRolDTO)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            assertThat(false)
        }
        client.perform(post("/iam/facultad/perfil/delete/rol")
                .contentType(MediaType.APPLICATION_JSON)
                .content(unAassignRol!!))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.idRol").value(1000))
    }

    @DisplayName("Read perfiles byname and active")
    @Test
    fun findPerfilesNameActivo() {
        client.perform(get("/iam/facultad/perfiles/nombre/activo/count?nombre=AdminIAM&activo=1"))
                .andExpect(status().isOk)

    }
}
