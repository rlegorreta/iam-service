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
 *  EstadisticaController.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.controller

import com.ailegorreta.iamservice.service.facultad.StatService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for all REST services for the different statistics queries
 * that can be done to the IAM.
 *
 * note: This is the beginning for different dashboards. Actually the IAM-UI
 *       front does not utilize it.
 *
 * @author rlh
 * @project : iam-service
 * @date July 2023
 *
 */
// @CrossOrigin
@RestController
@RequestMapping("/iam/estadistica")
class EstadisticaController (val statService: StatService) {

    @GetMapping("/facultades/inactivas/porcentaje")
    fun porcFacultadesInactivas() = statService.porcFacultadesInactivas()

    @GetMapping("/roles/inactivos/porcentaje")
    fun porcRolesInactivos() = statService.porcRolesInactivos()

    @GetMapping("/perfiles/inactivos/porcentaje")
    fun porcPerfilesInactivos() = statService.porcPerfilesInactivos()

    @GetMapping("/usuarios/inactivos/porcentaje")
    fun porcUsuariosInactivos() = statService.porcUsuariosInactivos()
}