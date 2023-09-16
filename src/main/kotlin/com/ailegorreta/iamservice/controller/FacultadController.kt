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
 *  FacultadController.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.controller

import com.ailegorreta.iamservice.service.facultad.FacultadService
import com.ailegorreta.iamservice.service.facultad.PerfilService
import com.ailegorreta.iamservice.service.facultad.RolService
import com.ailegorreta.iamservice.service.facultad.UsuarioService
import com.ailegorreta.iamservice.service.facultad.dto.*
import org.springframework.web.bind.annotation.*

/**
 * Controller for all REST services for the daily operation (not
 * Administration) for the IAM
 *
 * @author rlh
 * @project : iam-service
 * @date July 2023
 *
 */
// @CrossOrigin
@RestController
@RequestMapping("/iam/facultad")
class FacultadController (val facultadService: FacultadService,
                          val rolService: RolService,
                          val perfilService: PerfilService,
                          val usuarioService: UsuarioService) {

    /**
     * Usuarios
     */
    @GetMapping("/usuarios")
    fun findUsuarios(@RequestParam(required = true) page: Int,
                     @RequestParam(required = true) size: Int,
                     @RequestParam(required = false) sort: String? = null) = usuarioService.findAll(page, size, sort)

    @GetMapping("/usuarios/activo")
    fun findUsuariosByActivoIs(@RequestParam(required = true) activo: Int,
                               @RequestParam(required = true) page: Int,
                               @RequestParam(required = true) size: Int,
                               @RequestParam(required = false) sort: String? = null) =
        usuarioService.findByActivoIs(activo.equals(1), page, size, sort)

    @GetMapping("/usuario/activo/count")
    fun countUsuariosByActvoIs(@RequestParam(required = true) activo: Int) =
        usuarioService.countByActvoIs(activo.equals(1))

    @GetMapping("/usuarios/nombre")
    fun findUsuariosByNombreUsuarioContains(@RequestParam(required = true) nombre: String,
                                            @RequestParam(required = true) page: Int,
                                            @RequestParam(required = true) size: Int,
                                            @RequestParam(required = false) sort: String? = null) =
        usuarioService.findByNombreUsuarioContains(nombre, page, size, sort)

    @GetMapping("/usuarios/nombre/count")
    fun countUsuariosByNombreUsuarioContains(@RequestParam(required = true) nombre: String) =
        usuarioService.countByNombreUsuarioContains(nombre)

    @GetMapping("/usuarios/nombre/activo")
    fun findUsuariosByNombreContainsAndActivoIs(@RequestParam(required = true) nombre: String,
                                                @RequestParam(required = true) activo: Int,
                                                @RequestParam(required = true) page: Int,
                                                @RequestParam(required = true) size: Int,
                                                @RequestParam(required = false) sort: String? = null) =
        facultadService.findByNombreContainsAndActivoIs(nombre, activo.equals(1), page, size, sort)

    @GetMapping("/usuarios/nombre/activo/count")
    fun countUsuariosByNombreContainsAndActivoIs(@RequestParam(required = true) nombre: String,
                                                 @RequestParam(required = true) activo: Int) =
        facultadService.countByNombreContainsAndActivoIs(nombre, activo.equals(1))

    @GetMapping("/usuarios/nombreusuario")
    fun findUsuariosByNombreUsuarioContains(@RequestParam(required = true) nombreUsuario: String) =
        usuarioService.findByNombreUsuarioContains(nombreUsuario)

    @GetMapping("/usuario/by/id")
    fun findUsuarioById(@RequestParam(required = true) id: Long) =
        usuarioService.findById(id)

    @GetMapping("/usuario/by/idUsuario")
    fun findByIdUsuario(@RequestParam(required = true) idUsuario: Long) =
        usuarioService.findByIdUsuario(idUsuario)

    @GetMapping("/usuario/by/nombreusuario")
    fun findByNombreUsuario(@RequestParam(required = true) nombreUsuario: String) =
        usuarioService.findByNombreUsuario(nombreUsuario)

    @GetMapping("/usuarios/by/compania")
    fun findUsuariosByCompania(@RequestParam(required = true) nombreCompania: String) =
        usuarioService.findUsuariosByCompania(nombreCompania)

    @GetMapping("/usuario/facultades")
    fun usuarioFacultades(@RequestParam(required = true) nombre: String) =
        facultadService.findUsuarioFacultades(nombre)

    @GetMapping("/usuarios/by/facultad")
    fun findEmpleadosByFacultad(@RequestParam(required = true) nombreCompania: String,
                                @RequestParam(required = true) nombreFacultad: String) =
        usuarioService.findEmpleadosByFacultad(nombreCompania, nombreFacultad)

    @GetMapping("/usuario/grafo/facultades")
    fun usuarioGraphFacultades(@RequestParam(required = true) nombre: String) =
        facultadService.graphUsuarioFacultades(nombre)

    @GetMapping("/usuario/has/facultad")
    fun hasUsuarioFacultad(@RequestParam(required = true) nombreUsuario: String,
                           @RequestParam(required = true) nombreFacultad: String) =
        facultadService.hasUsuarioFacultad(nombreUsuario, nombreFacultad)

    @PostMapping("/usuario/asigna/perfil")
    fun assignProfile(@RequestBody assignPerfilDTO: AssignPerfilDTO) =
        usuarioService.assignProfile(assignPerfilDTO)

    @PostMapping("/usuario/asigna/facultad")
    fun assignPermit(@RequestBody assignFacultadlDTO: AssignPerfilDTO) =
        usuarioService.assignFacultad(assignFacultadlDTO)

    @PostMapping("/usuario/desasigna/facultad")
    fun unAssignPermit(@RequestBody desAssignFacultadlDTO: AssignPerfilDTO) =
        usuarioService.unAssignFacultad(desAssignFacultadlDTO)

    @PostMapping("/usuario/prohibe/facultad")
    fun forbidPermit(@RequestBody forbidFacultadlDTO: AssignPerfilDTO) =
        usuarioService.forbidFacultad(forbidFacultadlDTO)

    @PostMapping("/usuario/desprohibe/facultad")
    fun unForbidPermit(@RequestBody desForbidFacultadlDTO: AssignPerfilDTO) =
        usuarioService.unForbidFacultad(desForbidFacultadlDTO)

    /**
     *  Facultades
     */
    @GetMapping("/facultades")
    fun findFacultades(@RequestParam(required = true) page: Int,
                       @RequestParam(required = true) size: Int,
                       @RequestParam(required = false) sort: String? = null) =
        facultadService.findAll(page, size, sort)

    @GetMapping("/facultades/count")
    fun countFacultades() = facultadService.count()

    @GetMapping("/facultades/activo")
    fun findFacultadesByActivoIs(@RequestParam(required = true) activo: Int,
                                 @RequestParam(required = true) page: Int,
                                 @RequestParam(required = true) size: Int,
                                 @RequestParam(required = false) sort: String? = null) =
        facultadService.findByActivoIs(activo.equals(1), page, size, sort)

    @GetMapping("/facultades/activo/count")
    fun countFacultadesByActvoIs(@RequestParam(required = true) activo: Int) =
        facultadService.countByActvoIs(activo.equals(1))

    @GetMapping("/facultades/nombre")
    fun findFacultadesByNombreContains(@RequestParam(required = true) nombre: String,
                                       @RequestParam(required = true) page: Int,
                                       @RequestParam(required = true) size: Int,
                                       @RequestParam(required = false) sort: String? = null) =
        facultadService.findByNombreContains(nombre, page, size, sort)

    @GetMapping("/facultades/nombre/count")
    fun countFacultadesByNombreContains(@RequestParam(required = true) nombre: String) =
        facultadService.countByNombreContains(nombre)

    @GetMapping("/facultades/nombre/activo")
    fun findFacultadesByNombreContainsAndActivoIs(@RequestParam(required = true) nombre: String,
                                                  @RequestParam(required = true) activo: Int,
                                                  @RequestParam(required = true) page: Int,
                                                  @RequestParam(required = true) size: Int,
                                                  @RequestParam(required = false) sort: String? = null) =
        facultadService.findByNombreContainsAndActivoIs(nombre, activo.equals(1), page, size, sort)

    @GetMapping("/facultades/nombre/activo/count")
    fun countFacultadesByNombreContainsAndActivoIs(@RequestParam(required = true) nombre: String,
                                                   @RequestParam(required = true) activo: Int) =
        facultadService.countByNombreContainsAndActivoIs(nombre, activo.equals(1))

    @GetMapping("/facultad/by/id")
    fun findFacultadById(@RequestParam(required = true) id: Long) =  facultadService.findById(id)

    @GetMapping("/facultad/by/nombre")
    fun findFacultadByNombre(@RequestParam(required = true) nombre: String) =
        facultadService.findByNombre(nombre)

    @PostMapping("/facultad/add")
    fun addFacultad(@RequestBody facultadDTO: FacultadDTO) = facultadService.add(facultadDTO)

    /**
     *   Roles
     */
    @GetMapping("/roles")
    fun findAllRoles(@RequestParam(required = true) page: Int,
                     @RequestParam(required = true) size: Int,
                     @RequestParam(required = false) sort: String? = null) =
        rolService.findAll(page, size, sort)

    @GetMapping("/roles/count")
    fun countAllRoles() = rolService.count()

    @GetMapping("/roles/activo")
    fun findRolesByActivoIs(@RequestParam(required = true) activo: Int,
                            @RequestParam(required = true) page: Int,
                            @RequestParam(required = true) size: Int,
                            @RequestParam(required = false) sort: String? = null) =
        rolService.findByActivoIs(activo.equals(1), page, size, sort)

    @GetMapping("/roles/activo/count")
    fun countRolesByActvoIs(@RequestParam(required = true) activo: Int) =
        rolService.countByActvoIs(activo.equals(1))

    @GetMapping("/roles/nombre")
    fun findRolesByNombreContains(@RequestParam(required = true) nombre: String,
                                  @RequestParam(required = true) page: Int,
                                  @RequestParam(required = true) size: Int,
                                  @RequestParam(required = false) sort: String? = null) =
        rolService.findByNombreContains(nombre, page, size, sort)

    @GetMapping("/roles/nombre/count")
    fun countRolesByNombreContains(@RequestParam(required = true) nombre: String) =
        rolService.countByNombreContains(nombre)

    @GetMapping("/roles/nombre/activo")
    fun findRolesByNombreContainsAndActivoIs(@RequestParam(required = true) nombre: String,
                                             @RequestParam(required = true) activo: Int,
                                             @RequestParam(required = true) page: Int,
                                             @RequestParam(required = true) size: Int,
                                             @RequestParam(required = false) sort: String? = null) =
        rolService.findByNombreContainsAndActivoIs(nombre, activo.equals(1), page, size, sort)

    @GetMapping("/roles/nombre/activo/count")
    fun countRolesByNombreContainsAndActivoIs(@RequestParam(required = true) nombre: String,
                                              @RequestParam(required = true) activo: Int) =
        rolService.countByNombreContainsAndActivoIs(nombre, activo.equals(1))

    @GetMapping("/rol/by/id")
    fun findRolById(@RequestParam(required = true) id: Long) = rolService.findById(id)

    @GetMapping("rol/by/idRol")
    fun findRolByIdRol(@RequestParam(required = true) idRol: Long) = rolService.findByIdRol(idRol)

    @PostMapping("rol/add")
    fun addRol(@RequestBody rolDTO: RolDTO) = rolService.add(rolDTO)

    @PostMapping("rol/add/facultad")
    fun assignFacultad(@RequestBody assignFacultad: AssignFacultadDTO) =
        rolService.assignPermit(assignFacultad.nombre, assignFacultad.rolDTO)

    @RequestMapping("rol/delete/facultad", method = [RequestMethod.POST, RequestMethod.DELETE])
    fun unAssignFacultad(@RequestBody unAssignFacultad: AssignFacultadDTO) = rolService.unAssignPermit(unAssignFacultad.nombre, unAssignFacultad.rolDTO)

    /**
     * Perfiles
     */
    @GetMapping("/perfiles")
    fun findPerfiles(@RequestParam(required = true) page: Int,
                     @RequestParam(required = true) size: Int,
                     @RequestParam(required = false) sort: String? = null) =
        perfilService.findAll(page, size, sort)

    @GetMapping("/perfiles/count")
    fun countAllPerfiles() = perfilService.count()

    @GetMapping("/perfiles/activo")
    fun findPerfilesByActivoIs(@RequestParam(required = true) activo: Int,
                               @RequestParam(required = true) page: Int,
                               @RequestParam(required = true) size: Int,
                               @RequestParam(required = false) sort: String? = null) =
        perfilService.findByActivoIs(activo.equals(1), page, size, sort)

    @GetMapping("/perfiles/activo/count")
    fun countPerfilesByActivoIs(@RequestParam(required = true) activo: Int) =
        perfilService.countByActvoIs(activo.equals(1))

    @GetMapping("/perfiles/nombre")
    fun findPerfilesByNombreContains(@RequestParam(required = true) nombre: String,
                                     @RequestParam(required = true) page: Int,
                                     @RequestParam(required = true) size: Int,
                                     @RequestParam(required = false) sort: String? = null) =
        perfilService.findByNombreContains(nombre, page, size, sort)

    @GetMapping("/perfiles/nombre/count")
    fun countPerfilesByNombreContains(@RequestParam(required = true) nombre: String) =
        perfilService.countByNombreContains(nombre)

    @GetMapping("/perfiles/nombre/activo")
    fun findPerfilesByNombreContainsAndActivoIs(@RequestParam(required = true) nombre: String,
                                                @RequestParam(required = true) activo: Int,
                                                @RequestParam(required = true) page: Int,
                                                @RequestParam(required = true) size: Int,
                                                @RequestParam(required = false) sort: String? = null) =
        perfilService.findByNombreContainsAndActivoIs(nombre, activo.equals(1), page, size, sort)

    @GetMapping("/perfiles/nombre/activo/count")
    fun countPerfilesByNombreContainsAndActivo(@RequestParam(required = true) nombre: String,
                                               @RequestParam(required = true) activo: Int) =
        perfilService.countByNombreContainsAndActivoIs(nombre, activo.equals(1))

    @GetMapping("/perfil/by/id")
    fun findPerfilById(@RequestParam(required = true) id: Long) =  perfilService.findById(id)

    @GetMapping("/perfil/by/nombre")
    fun findPerfilByNombre(@RequestParam(required = true) nombre: String) =
        perfilService.findByNombre(nombre)

    @GetMapping("/perfil/by/nombre/detail")
    fun findPerfilByNombreDetail(@RequestParam(required = true) nombre: String) =
        perfilService.findByNombre(nombre, true)

    @GetMapping("/perfil/facultades")
    fun getPerfilFacultades(@RequestParam(required = true) nombre: String) =
        perfilService.getPerfilFacultades(nombre)

    @GetMapping("/perfil/grafo/facultades")
    fun graphPerfilFacultades(@RequestParam(required = true) nombre: String) =
        perfilService.graphPerfilFacultades(nombre)

    @PostMapping("perfil/add")
    fun addPerfil(@RequestBody perfilDTO: PerfilDTO) =
        perfilService.add(perfilDTO)

    @PostMapping("perfil/add/rol")
    fun assignRol(@RequestBody assignRol: AssignRolDTO) =
        perfilService.assignRole(assignRol.idRol, assignRol.perfilDTO)

    @RequestMapping("perfil/delete/rol", method = [RequestMethod.POST, RequestMethod.DELETE])
    fun unAssignRol(@RequestBody unAssignRol: AssignRolDTO) =
        perfilService.unAssignRole(unAssignRol.idRol, unAssignRol.perfilDTO)

}
