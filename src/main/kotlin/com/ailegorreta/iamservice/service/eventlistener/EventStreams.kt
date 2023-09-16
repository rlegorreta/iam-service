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
 *  EventStreams.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.eventlistener

import com.ailegorreta.commons.event.EventDTO
import com.ailegorreta.commons.event.EventErrorDTO
import com.ailegorreta.commons.utils.HasLogger
import com.ailegorreta.iamservice.exception.EventStreamsException
import com.ailegorreta.iamservice.model.Negocio
import com.ailegorreta.iamservice.repository.AreaAsignadaRepository
import com.ailegorreta.iamservice.service.compania.CompaniaService
import com.ailegorreta.iamservice.service.compania.UsuarioCompaniaService
import com.ailegorreta.iamservice.service.compania.dto.CompaniaDTO
import com.ailegorreta.iamservice.service.compania.dto.NewCorporativoDTO
import com.ailegorreta.iamservice.service.compania.dto.UsuarioDTO
import com.ailegorreta.iamservice.service.event.EventService
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Event Logger listener for the new Customers microservice. Events read:
 *
 * - When a new Customer is added we need to create the Master administrator.
 * - After creation an authorization is required to activate the Customer
 * - When a new user is assigned to a Company
 *
 * @author rlh
 * @project : iam-service
 * @date July 2023
 */

@Component
class EventStreams constructor(private val companiaService: CompaniaService,
                               private val eventService: EventService,
                               private val usuarioCompaniaService: UsuarioCompaniaService,
                               private val areaAsignadaRepository: AreaAsignadaRepository,
                               private val mapper: ObjectMapper): HasLogger {

    @Value("\${spring.application.name}")
    private val appName: String? = null

    /**
     * This listener is for new corporates. We need to insert the Master Administrator
     * and the Company node in the Neo4j database.
     * condition = "headers['eventName']=='CreaCliente' && headers['applicationName']=='bup'")
     */
    fun newCorporate(eventRequest: EventDTO) {
        logger.info("Event for new CrearCustomer received:${eventRequest.eventBody}")

        try {
            val jsonNode = mapper.readValue(eventRequest.eventBody.toString(), JsonNode::class.java)
            // now try to get all NewCorporativoDTO
            val negocio = Negocio.getNegocio(jsonNode.get("tipoCliente").asInt())

            if (negocio != Negocio.NOT_EXIST) {
                val companiaDTO = CompaniaDTO(nombre = jsonNode.get("nombreCorto").asText(),
                    padre = true,
                    negocio = negocio,
                    usuarioModificacion = eventRequest.username,
                    fechaModificacion = LocalDateTime.parse(jsonNode.get("fechaAlta").asText()),
                    idPersona = jsonNode.get("id").asLong())

                // Try to read a user with its email
                var usuarioDTO = tryGetUsuario(jsonNode, eventRequest.username)

                usuarioDTO.companias = arrayListOf(companiaDTO)

                val newCorporativoDTO = NewCorporativoDTO(companiaDTO, usuarioDTO)

                companiaService.newCorporate(newCorporativoDTO.companiaDTO, newCorporativoDTO.usuarioDTO)
            } else
                logger.info("Nota => el Cliente NO fue dado de alta ya que no pertenece a ningún negocio que tenga el sistema")
        } catch (e: IOException) {
            // Some error existed that kept the database un synchronized so we send the event in order to be listened
            // by the IT department and fixed manually
            tryToSendEventError("AL SINCRONIZAR ALTA", e.localizedMessage, eventRequest)
            throw EventStreamsException("Cannot construct the new company:${e.message}")
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("ERROR: For some reason we could not save the new Customer:${e.message}")
            logger.error("ERROR: Try to send an ERROR event")
            tryToSendEventError("AL SINCRONIZAR ALTA", e.localizedMessage, eventRequest)
        }
    }

    /**
     * Try to get the user from its mail from one contact.
     *
     * The try to get the User from fr IdUsuario ("id") in the Json file.
     *
     * If not get a default administrator
     */
    private fun tryGetUsuario(jsonNode: JsonNode, userName: String): UsuarioDTO {
        val contactos = jsonNode.get("contactos")

        if (contactos.isArray()) {
            contactos.forEach{
                val usuario = usuarioCompaniaService.findByMail(it.get("correo").asText())

                if (usuario.isPresent)
                    return usuario.get()
            }
        }
        val usuario = usuarioCompaniaService.findByIdUsuario(jsonNode.get("id").asLong())

        if (usuario.isPresent)
            return usuario.get()

        // not found any user then generate a default user
        return UsuarioDTO(idUsuario = jsonNode.get("id").asLong(),
            nombreUsuario = jsonNode.get("nombreCorto").asText(),
            nombre = jsonNode.get("razonSocial").asText(),
            apellido = jsonNode.get("apellido").asText(),
            telefono = "00-000-000000",
            mail = "staff@" + jsonNode.get("nombreCorto").asText() + ".com.mx",
            interno = false,
            activo = false,
            administrador = true,
            usuarioModificacion = userName,
            zonaHoraria = ZoneId.of("America/Mexico").toString())
    }

    /**
     * This listener is for authorization. We just changes the status of the Corporative,
     * in order to ser activo = true.
     * condition = "headers['eventName']=='AutorizacionCliente' && headers['applicationName']=='bup'")
     */
    fun activateNewCorporate(eventRequest: EventDTO) {
        logger.info("Event for the activation for a new Customer received:${eventRequest.eventBody}")

        try {
            val jsonNode = mapper.readValue(eventRequest.eventBody.toString(), JsonNode::class.java)

            // Check what was activated, if it is a Customer
            val tipo = jsonNode.get("tipo").asText()
            // Check if accepted
            val accion = jsonNode.get("accion").asText()

            if (tipo == "CreaCliente") {           // we update the Company
                val idCompany = jsonNode.get("id").asLong()

                // Check the existence of Company
                val compania = companiaService.findByIdPersona(idCompany)

                if (compania.isPresent) {
                    val companiaDTO = compania.get()

                    companiaService.activateCompany(companiaDTO, accion == "acepta")
                } else
                    tryToSendEventError("NO SE ENCONTRO LA COMPANIA A ACTIVAR", "la acción no es'acepta'", eventRequest)
            } else
                tryToSendEventError("TIPO DE OPERACION ILEGAL", tipo, eventRequest)
            if (accion != "acepta")
                tryToSendEventError("NO_SE_ACTIVO_COMPANIA RECHAZADO", accion, eventRequest)
        } catch (e: IOException) {
            // Some error existed that kept the database un synchronized so we send the event in order to be listened
            // by the IT department and fixed manually
            tryToSendEventError("AL ACTIVAR PARTICIPANTE/AREA", e.message!!, eventRequest)
            throw EventStreamsException("Cannot activate the new Participante/Area:${e.message}")
        } catch (e: Exception) {
            logger.error("ERROR: For some reason we could not activate the new Companya:${e.message}")
            logger.error("ERROR: Try to send an ERROR event")
            tryToSendEventError("AL ACTIVAR COMPANIA", e.message!!, eventRequest)
            e.printStackTrace()
        }
    }

    /**
     * Something wrong happened, so we tried to send and event error to the eventLogger
     *
     * But first, since we are listener we need to get a token
     */
    private fun tryToSendEventError(sysError: String, message: String,
                                    eventRequest: EventDTO
    ) {
        try {
            // UserContext.setCorrelationId(eventRequest.correlationId)

            eventService.sendEvent(correlationId = eventRequest.correlationId,
                userName = appName!!,
                eventName = "ERROR_SISTEMA: $sysError",
                value = EventErrorDTO(message = message,
                    cause = "No se pudo sincronizar las base de datos del IAM",
                    description = eventRequest.eventBody.toString())
            )
        } catch (e: Exception) {
            logger.error("ERROR: No se pudo enviar el evento con el error:${e.message}")
            e.printStackTrace()
        }
    }
}
