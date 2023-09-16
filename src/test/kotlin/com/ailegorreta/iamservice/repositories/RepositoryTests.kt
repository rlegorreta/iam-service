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
 *  RepositoryTests.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.repositories

import com.ailegorreta.commons.utils.HasLogger
import com.ailegorreta.iamservice.EnableTestContainers
import com.ailegorreta.iamservice.model.Area
import com.ailegorreta.iamservice.model.FacultadTipo
import com.ailegorreta.iamservice.repository.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.neo4j.driver.Driver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.test.context.ActiveProfiles
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.util.function.Consumer

@DataNeo4jTest
@EnableTestContainers
/* ^ This is a custom annotation to load the containers */
@ExtendWith(MockitoExtension::class)
@ActiveProfiles("integration-tests")
class RepositoryTests: HasLogger {
    @MockBean
    private val streamBridge: StreamBridge? = null

    @MockBean
    private var reactiveJwtDecoder: ReactiveJwtDecoder? = null            // Mocked the security JWT

    @Autowired
    private lateinit var areaAsignadaRepository: AreaAsignadaRepository

    /**
     * Adding what the database has been created in the Data Initialization, we add another cypher for testing purpose
     * only.
     */
    @BeforeEach
    fun setup(@Autowired driver: Driver) {
        processCypher(driver, "/clearTestOne.cypher", true)
    }

    private fun processCypher(driver: Driver, fileName: String, commandByLine: Boolean) {
        BufferedReader(InputStreamReader(this.javaClass.getResourceAsStream(fileName))).use { testReader ->
            logger.info("Start process $fileName")
            driver.session().use { session ->
                do {
                    val cypher: String? = if (commandByLine) testReader.readLine()
                    else testReader.readText()

                    if (!cypher.isNullOrBlank())
                        session.run(cypher)
                            .consume()
                } while (commandByLine && !cypher.isNullOrBlank())
            }
            logger.info("Finish process $fileName")
        }
    }

    @Test
    fun `Check all where created in iamDBstart`(@Autowired facultadRepository: FacultadRepository,
                                                @Autowired rolRepository: RolRepository,
                                                @Autowired perfilRepository: PerfilRepository,
                                                @Autowired companiaRepository: CompaniaRepository,
                                                @Autowired areaRepository: AreaRepository,
                                                @Autowired grupoRepository: GrupoRepository,
                                                @Autowired usuarioRepository: UsuarioRepository) {
        assertThat(facultadRepository.findAll().count()).isEqualTo(29L)
        facultadRepository.findAll().forEach {
            assertThat(it).satisfies(Consumer { facultad -> assertThat(facultad.tipo).isEqualTo(FacultadTipo.SIMPLE.toString()) })
        }
        assertThat(rolRepository.findAll().count()).isEqualTo(15L)
        assertThat(perfilRepository.findAll().count()).isEqualTo(12L)
        assertThat(companiaRepository.findAll().count()).isEqualTo(9L)
        assertThat(companiaRepository.findByNombre("LMASS Desarrolladores SA de CV"))
            .satisfies(Consumer { company -> assertThat(company?.padre).isTrue() })
        assertThat(areaRepository.findAll().count()).isEqualTo(8L)
        assertThat(grupoRepository.findAll().count()).isEqualTo(5L)
        assertThat(grupoRepository.findByNombre("Admin LMASS"))
            .satisfies(Consumer { group -> assertThat(group?.activo).isTrue() })
        assertThat(usuarioRepository.findByNombreUsuario("adminALL"))
            .satisfies(Consumer { usuario -> assertThat(usuario?.activo).isTrue() })
        assertThat(usuarioRepository.findByNombreUsuario("adminIAM"))
            .satisfies(Consumer { usuario -> assertThat(usuario?.activo).isTrue() })
        assertThat(usuarioRepository.findAll().count()).isEqualTo(15L)
    }

    @Test
    fun `Check all where created with TestONE`(@Autowired driver: Driver,
                                                @Autowired facultadRepository: FacultadRepository,
                                                @Autowired rolRepository: RolRepository,
                                                @Autowired perfilRepository: PerfilRepository,
                                                @Autowired companiaRepository: CompaniaRepository,
                                                @Autowired areaRepository: AreaRepository,
                                                @Autowired grupoRepository: GrupoRepository,
                                                @Autowired usuarioRepository: UsuarioRepository) {
        processCypher(driver, "/testOne.cypher", false)
        assertThat(facultadRepository.findAll().count()).isEqualTo(32L)
        facultadRepository.findAll().forEach {
            assertThat(it).satisfies(Consumer { facultad -> assertThat(facultad.tipo).isEqualTo(FacultadTipo.SIMPLE.toString()) })
        }
        assertThat(rolRepository.findAll().count()).isEqualTo(17L)
        assertThat(perfilRepository.findAll().count()).isEqualTo(12L)
        assertThat(companiaRepository.findAll().count()).isEqualTo(13L)
        assertThat(areaRepository.findAll().count()).isEqualTo(17L)
        assertThat(companiaRepository.findByNombre("LMASS Desarrolladores SA de CV"))
            .satisfies(Consumer { company -> assertThat(company?.padre).isTrue() })
        assertThat(companiaRepository.findByNombre("BlackRock"))
            .satisfies(Consumer { compania -> assertThat(compania?.padre).isTrue() })
        assertThat(grupoRepository.findAll().count()).isEqualTo(10L)
        assertThat(grupoRepository.findByNombre("Admin LMASS"))
            .satisfies(Consumer { group -> assertThat(group?.activo).isTrue() })
        assertThat(usuarioRepository.findByNombreUsuario("adminALL"))
            .satisfies(Consumer { usuario -> assertThat(usuario?.activo).isTrue() })
        assertThat(usuarioRepository.findByNombreUsuario("adminIAM"))
            .satisfies(Consumer { usuario -> assertThat(usuario?.activo).isTrue() })
        assertThat(usuarioRepository.findByNombreUsuario("adminACMEJr"))
            .satisfies(Consumer { usuario -> assertThat(usuario?.activo).isTrue() })
        assertThat(usuarioRepository.findAll().count()).isEqualTo(25L)
    }

    @Test
    fun `Check custom queries for companiaRepository`(@Autowired driver: Driver,
                                                        @Autowired companiaRepository: CompaniaRepository,
                                                        @Autowired usuarioRepository: UsuarioRepository,
                                                        @Autowired areaRepository: AreaRepository) {
        processCypher(driver, "/testOne.cypher", false)
        // Read all Companies that are customers and they are active
        assertThat(companiaRepository.countAllCustomers()).isEqualTo(11L)
        // Read companies by sector and are active or not
        assertThat(companiaRepository.countByNegocioAndActivo("FINANCIERA", true)).isEqualTo(2L)
        // Read all company with a partial name with NOT in sector and active
        assertThat(companiaRepository.countByNombreContainsAndNegocioIsNotAndActivo("ACME", "NA", true)).isEqualTo(3L)
        // Read all company with a partial name with in sector and active
        assertThat(companiaRepository.countByNombreContainsNegocioAndActivo("ACME", "INDUSTRIAL", true)).isEqualTo(3L)
        // Read company subsidiaries
        assertThat(companiaRepository.findSubsidiarias("ACME SA de CV").size).isEqualTo(2L)
        // Find companies that the Administrator user can see
        assertThat(companiaRepository.findCompaniasPermiteByAdministrador("adminACME").size).isEqualTo(1L)
        // Find subsidiaries that the Administrator user can see
        assertThat(companiaRepository.findCompaniasPermiteSubsidiariasByAdministrador("adminACME").size).isEqualTo(2L)
        // Find companies that the Administrator user can see without company subsidiaries
        assertThat(companiaRepository.findCompaniasPermiteSinHerenciByAdministrador("adminACMEJr").size).isEqualTo(1L)
        // Find company by area
        assertThat(companiaRepository.findCompaniaByArea("Tesorería ACME"))
            .satisfies(Consumer { company -> assertThat(company?.nombre).isEqualTo("ACME SA de CV") })
        // Find companies where the employee work
        val user = usuarioRepository.findByNombreUsuario("adminACMEJr")

        assertThat(user)
        assertThat(companiaRepository.findCompaniasByEmpleado(user!!.id!!).size).isEqualTo(1L)

        // Mutation methods
        // Add a new area and assign it to a company
        areaRepository.save(
            Area(
                nombre = "New Area", activo = true, idArea = 0L, idPersona = 0L,
                fechaModificacion = LocalDateTime.now(), usuarioModificacion = "TEST"
            )
        )
        assertThat(companiaRepository.addArea("ACME SA de CV", "newArea")).isEqualTo(0L)
        assertThat(companiaRepository.deleteArea("ACME SA de CV", "newArea")).isEqualTo(0L)
    }

    @Test
    fun `Check custom queries for facultadRepository`(@Autowired facultadRepository: FacultadRepository) {
        // Find permits by active or not active
        assertThat(facultadRepository.countByActvoIs(true)).isEqualTo(29L)
        // Find count by name contains
        assertThat(facultadRepository.countByNombreContains("Admin")).isEqualTo(2L)
        // Find count by name contains and active or not active
        assertThat(facultadRepository.countByNombreContainsAndActivoIs("Admin", true)).isEqualTo(2L)
    }

    /**
     * This test method can be executed to see a user permits.
     * hint: If we do not set the environment variables to the Noeo4j test container, we can poin to a 'real' Neo4j
     *       database and watch all user permits. Very usable
     */
    @Test
    fun `See a user permits`(@Autowired facultadRepository: FacultadRepository) {
        // Very frequent used method: find user permits
        assertThat(facultadRepository.findUsuarioFacultades("adminALL"))
            .satisfies(Consumer { permits ->
                run {
                    permits.forEach { permit -> println(">> Permit:${permit.nombre} description:${permit.descripcion}") }
                    assertThat(permits.isNotEmpty()).isTrue()
                }
            })
    }

    @Test
    fun `Check custom queries for grupoRepository`(@Autowired grupoRepository: GrupoRepository,
                                                   @Autowired usuarioRepository: UsuarioRepository) {
        val group = grupoRepository.findByNombre("Admin ACME")

        assertThat(group)
        // Count group members
        assertThat(grupoRepository.countMiembros(group!!.id!!)).isEqualTo(1L)
        // Find group using employee ID
        // Find companies where the employee work
        val user = usuarioRepository.findByNombreUsuario("adminACME")

        assertThat(user)
        assertThat(grupoRepository.findGruposByEmpleado(user!!.id!!).size).isEqualTo(1L)
    }

    @Test
    fun `Check custom queries for perfilRepository`(@Autowired perfilRepository: PerfilRepository,
                                                    @Autowired rolRepository: RolRepository) {
        val profile = perfilRepository.findByNombre("Dios del mrk.place")

        assertThat(profile.isPresent)
        // Find a profile with all its relationships by Id
        assertThat(perfilRepository.findDepthById(profile.get().id!!))
            .satisfies(Consumer { p -> run {
                val profileDepth = p.get()

                assertThat(profileDepth.roles!!.size).isEqualTo(13L)
                assertThat(profileDepth.roles!!.first().facultades!!.size).isEqualTo(1L)
            } })
        // Find a profile with all its relationships by name
        assertThat(perfilRepository.findDepthByNombre("Dios del mrk.place"))
            .satisfies(Consumer { p -> run {
                val profileDepth = p.get()

                assertThat(profileDepth.roles!!.size).isEqualTo(13L)
                assertThat(profileDepth.roles!!.first().facultades!!.size).isEqualTo(1L)
            } })
        // Find count of profile by active or not active
        assertThat(perfilRepository.countByActivoIs(true)).isEqualTo(10L)
        // Find count of profile by name contains
        assertThat(perfilRepository.countByNombreContains("Dios")).isEqualTo(2L)
        // Find count of profile by name contains and active or not active
        assertThat(perfilRepository.countByNombreContainsAndActivoIs("Dios", true)).isEqualTo(2L)
        // Find count of profile by name contains and active or not active (no pageable)
        assertThat(perfilRepository.findByNombreContainsAndActivoIs_("Dios", true).size).isEqualTo(2L)
        // Mutation methods
        val role = rolRepository.findByNombreContainsAndActivoIs_("Dios", true).first()

        assertThat(role)
        assertThat(perfilRepository.addRol(id = profile.get().id!!, idRol = role.id!!)).isEqualTo(1L)
        assertThat(perfilRepository.unAssignRole(id = profile.get().id!!, idRol = role.id!!)).isNull()
    }

    @Test
    fun `Check custom queries for rolRepository`(@Autowired rolRepository: RolRepository,
                                                 @Autowired facultadRepository: FacultadRepository) {
        // Find count of roles by active or not active
        assertThat(rolRepository.countByActivoIs(true)).isEqualTo(15L)
        // Find count of roles by name contains
        assertThat(rolRepository.countByNombreContains("ios")).isEqualTo(1L)
        // Find count of roles by name contains and active or not active
        assertThat(rolRepository.countByNombreContainsAndActivoIs("ios", true)).isEqualTo(1L)
        // Find count of profile by name contains and active or not active (no pageable)
        assertThat(rolRepository.findByNombreContainsAndActivoIs_("ios", true).size).isEqualTo(1L)

        // Mutation methods
        val permit = facultadRepository.findByNombre("adminIAM")
        val role = rolRepository.findByNombreContainsAndActivoIs_("Dios", true).first()

        assertThat(role)
        assertThat(permit.isPresent)
        assertThat(rolRepository.addPermit(id = role.id!!, idFacultad = permit.get().id!!)).isEqualTo(1L)
        assertThat(rolRepository.unAssignPermit(id = role.id!!, nombre = permit.get().nombre)).isNull()
    }

    @Test
    fun `Check custom queries for usuarioRepository1`(@Autowired driver: Driver,
                                                     @Autowired usuarioRepository: UsuarioRepository) {
        processCypher(driver, "/testOne.cypher", false)
        // Find count of users by active or not active
        assertThat(usuarioRepository.countByActivoIs(true)).isEqualTo(24L)
        // Find count of users by name contains
        assertThat(usuarioRepository.countByNombreUsuarioContains("ALL")).isEqualTo(1L)
        // Find count of users by name contains and active or not active
        assertThat(usuarioRepository.countByNombreUsuarioContainsAndActivoIs("ALL", true)).isEqualTo(1L)
        // Find By Id but avoid circular relationship with supervisor
        val user = usuarioRepository.findByNombreUsuario("adminACMEJr")

        assertThat(user)
        assertThat(usuarioRepository.findByIdSupervisor(user!!.id!!)).isPresent
        // Find users by supervisor
        assertThat(usuarioRepository.findUsuariosBySupervisor("adminACME").size).isEqualTo(1L)
    }

    @Test
    fun `Check custom queries for usuarioRepository2`(@Autowired usuarioRepository: UsuarioRepository) {
        // Mutation methods
        val user = usuarioRepository.findByNombreUsuario("adminACME")

        assertThat(user)
        assertThat(usuarioRepository.update(id = user!!.id!!, nombre = "MODIFIED", nombreUsuario = user.nombreUsuario,
                    telefono = user.telefono, mail = user.mail, interno = user.interno, activo = user.activo,
                    administrador = user.administrador, fechaIngreso = user.fechaIngreso,
                    usuarioModificacion = user.usuarioModificacion, fechaModificacion = user.fechaModificacion))
            .satisfies(Consumer { userMod -> assertThat(userMod?.nombre).isEqualTo("MODIFIED") })
        // Validate supervisor relationship
        val user2 = usuarioRepository.findByNombreUsuario("adminACMET")

        assertThat(user2)
        assertThat(usuarioRepository.addSupervisor(user2!!.id!!, user.id!!)).isEqualTo("SUPERVISOR")
        assertThat(usuarioRepository.deleteSupervisor(user2.id!!)).isNull()
    }

    @Test
    fun `Check custom queries for usuarioRepository3`(@Autowired usuarioRepository: UsuarioRepository) {
        // Find employees that can be seen by the administrator
        assertThat(usuarioRepository.findEmpleadosPermiteByAdministrador("adminACMEB").size).isEqualTo(2L)
        // Find employees that can be seen by the administrator and employees of the subsidiaries
        assertThat(usuarioRepository.findEmpleadosPermiteSubsidiariasByAdministrador("adminACME").size).isEqualTo(4L)
        // Find the administrators by the administrator name
        assertThat(usuarioRepository.findAdministradoresByAdministrador("adminIAM").size).isEqualTo(2L)
        // Find employees that can be seen by the master administrator
        assertThat(usuarioRepository.findEmpleadosPermiteByAdministradorMaestro("adminACME").size).isEqualTo(2L)
        // Find employees by allowed subsidiaries from master administrator
        assertThat(usuarioRepository.findEmpleadosPermiteSubsidiariasByAdministradorMaestro("adminACME").size).isEqualTo(4L)
        // Find the administrators by the master administrator name
        assertThat(usuarioRepository.findAdministradoresByAdministradorMaestro("adminIAM").size).isEqualTo(2L)
    }

    @Test
    fun `Check custom queries for usuarioRepository4`(@Autowired driver: Driver,
                                                      @Autowired usuarioRepository: UsuarioRepository) {
        processCypher(driver, "/testOne.cypher", false)
        // Find employees that can be seen by the administrator and employees of the subsidiaries without inheritance
        assertThat(usuarioRepository.findEmpleadosPermiteSinHerenciaByAdministrador("adminACMEJr").size).isEqualTo(3L)
    }

    @Test
    fun `Check custom queries for usuarioRepository5`(@Autowired usuarioRepository: UsuarioRepository) {
        // Find employees that have a permit in this company
        assertThat(usuarioRepository.findEmpleadosByFacultad("ACME SA de CV", "masterAdmin").size).isEqualTo(1L)
        // Find employees by company
        assertThat(usuarioRepository.findUsuariosByCompania("AI/ML SA de CV").size).isEqualTo(2L)
        // Un assigned an era to an employee
    }

    @Test
    fun `Check area assign for usuarioRepository6`(@Autowired usuarioRepository: UsuarioRepository,
                                                   @Autowired areaRepository: AreaRepository) {
        // Mutation methods
        val user = usuarioRepository.findByNombreUsuario("adminACME")
        val area = areaRepository.findByNombre("Administración ACME Bodega")

        assertThat(user)
        assertThat(area).isPresent
        // Assign the area to the user but without authorization
        assertThat(usuarioRepository.assignArea(user!!.id!!, idArea = area.get().idArea, false)).isEqualTo(1L)
        // Now find pending authorizations
        assertThat(usuarioRepository.findSolicitudesAsignacion().size).isEqualTo(1L)
        // Approve the pending authorization
        assertThat(usuarioRepository.aproveSolicitudAsignacion(user.nombreUsuario, idArea = area.get().idArea, true))
            .isEqualTo(user.nombreUsuario)
        // Now find the user assigned to this area
        assertThat(usuarioRepository.findEmpleadosAssigned(area.get().idArea).size).isEqualTo(1L)
        // Lastly we un assign the area to the user
        assertThat(usuarioRepository.unAssignArea(user.id!!, idArea = area.get().idArea)).isEqualTo(1L)
        // And check that was un assigned
        assertThat(usuarioRepository.findEmpleadosAssigned(area.get().idArea).size).isEqualTo(0L)
    }

    @Test
    fun `Check employee works in a company for usuarioRepository7`(@Autowired usuarioRepository: UsuarioRepository) {
        // Mutation methods
        val user = usuarioRepository.findByNombreUsuario("adminACME")

        assertThat(user)

        // Assign an employee to a company
        assertThat(usuarioRepository.addCompania(user!!.id!!, "IXE BANCO")).isEqualTo(0L)
        // Now delete the employee for this company
        assertThat(usuarioRepository.deleteCompania(user!!.id!!, "IXE BANCO")).isEqualTo(0L)
    }

    @Test
    fun `Check employee is member of a group for usuarioRepository7`(@Autowired usuarioRepository: UsuarioRepository,
                                                                     @Autowired grupoRepository: GrupoRepository) {
        // Mutation methods
        val user = usuarioRepository.findByNombreUsuario("adminACME")
        val group = grupoRepository.findByNombre("Admin AI/ML")

        assertThat(user)
        assertThat(group)

        // Assign an employee to be a member of the group
        assertThat(usuarioRepository.addGrupo(user!!.id!!, group!!.nombre)).isEqualTo(0L)
        // Now delete the employee for this group
        assertThat(usuarioRepository.deleteGrupo(user.id!!, group.id!!)).isEqualTo(0L)
    }

    @Test
    fun `Check employee profile for usuarioRepository8`(@Autowired usuarioRepository: UsuarioRepository,
                                                        @Autowired perfilRepository: PerfilRepository) {
        // Mutation methods
        val user = usuarioRepository.findByNombreUsuario("adminACME")
        val profile = perfilRepository.findByNombre("Dios del mrk.place")

        assertThat(user)
        assertThat(profile).isPresent

        // Add an employee to have a new profile (not checking that he(she) has already one. This is done in the service
        assertThat(usuarioRepository.addPerfil(user!!.idUsuario, profile.get().id!!)).isEqualTo(1L)
        // Now delete the employee profile
        assertThat(usuarioRepository.deletePerfil(user.id!!, profile.get().id!!)).isEqualTo(1L)
        // Assign an employee to have a new profile (not checking that he(she) has already one.
        // This is done in the service. The difference with add profile is that fechaModificacion and UsuarioModificacion
        assertThat(usuarioRepository.assignPerfil(user.idUsuario, profile.get().id!!,
                                                  LocalDateTime.now(), "TEST")).isEqualTo(1L)
        // Now un assign the profile
        assertThat(usuarioRepository.unAssignPerfil(user.idUsuario, profile.get().id!!)).isEqualTo(1L)
    }

    @Test
    fun `Check employee special permits for usuarioRepository9`(@Autowired usuarioRepository: UsuarioRepository,
                                                                @Autowired facultadRepository: FacultadRepository) {
        // Mutation methods
        val user = usuarioRepository.findByNombreUsuario("adminACME")
        val permit = facultadRepository.findByNombre("monitorMail")

        assertThat(user)
        assertThat(permit).isPresent

        // Assign an extra permit to an employee.
        assertThat(usuarioRepository.assignFacultad(user!!.idUsuario, permit.get().id!!,
            LocalDateTime.now(), "TEST")).isEqualTo(1L)
        // Now un assign the extra permit
        assertThat(usuarioRepository.unAssignFacultad(user.idUsuario, permit.get().id!!,
                                                    LocalDateTime.now(), "TEST")).isEqualTo(1L)
        // Assign to forbid a permit to an employee.
        // Not checking is done (i.e., that exist). This is done by the service
        assertThat(usuarioRepository.forbidFacultad(user.idUsuario, permit.get().id!!,
                                                    LocalDateTime.now(), "TEST")).isEqualTo(1L)
        // Now un assign the forbidden permit
        assertThat(usuarioRepository.unAssignFacultad(user.idUsuario, permit.get().id!!,
            LocalDateTime.now(), "TEST")).isEqualTo(0L)
    }

}
