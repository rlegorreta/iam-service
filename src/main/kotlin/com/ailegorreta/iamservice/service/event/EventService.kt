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
 *  EventService.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.event

import com.ailegorreta.commons.event.EventDTO
import com.ailegorreta.commons.event.EventType
import com.ailegorreta.commons.utils.HasLogger
import com.ailegorreta.iamservice.config.ServiceConfig
import com.ailegorreta.iamservice.service.compania.CompaniaService
import com.ailegorreta.iamservice.service.compania.dto.CompaniaDTO
import com.ailegorreta.iamservice.service.compania.dto.UsuarioDTO
import com.ailegorreta.resourceserver.utils.UserContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.cloud.stream.function.StreamBridge
import java.util.concurrent.CountDownLatch

/**
 * EventService that sends events to the kafka event machine server.
 *
 *  @author rlh
 *  @project : iam-service
 *  @date September 2023
 */
@Service
class EventService(private val streamBridge: StreamBridge,
                   private val serviceConfig: ServiceConfig,
                   private val mapper: ObjectMapper): HasLogger {

    private val coreName = "iam"

    /**
     * Send the event directly to a Kafka microservice using the EventConfig class or the .yml file if it is a
     * producer only.
     */
    fun sendEvent(correlationId: String? = UserContext.getCorrelationId(),
                  userName: String,
                  eventName: String,
                  value: Any,
                  eventType: EventType = EventType.DB_STORE): EventDTO {
        val eventBody = mapper!!.readTree(mapper!!.writeValueAsString(value))
        val parentNode = mapper!!.createObjectNode()

        // Add the permit where notification will be sent
        parentNode.put("notificaFacultad", "IAM-SERVICE-FACULTAD")
        parentNode.set<JsonNode>("datos", eventBody!!)

        var event = EventDTO(correlationId = correlationId ?: "No gateway, so no correlation id found",
            eventType = eventType,
            username = userName,
            eventName = eventName,
            applicationName = serviceConfig!!.appName!!,
            coreName = coreName,
            eventBody = parentNode)

        logger.info("Will send the event:${event.correlationId} name:${event.eventName} body:${event.eventBody}")
        streamBridge!!.send("producer-out-0",event)

        return event
    }
}

@Service
class EventServiceConsumer(private val companiaService: CompaniaService,
                           private val mapper: ObjectMapper): HasLogger {
    var latch = CountDownLatch(1)

    /**
     * This function receives an event from Kafka bup-service that we need to add a new Corporation
     */
    fun processEvent(eventDTO: EventDTO): EventDTO? {
        logger.info("Maybe we need to add a new Corporation $eventDTO")
        if (eventDTO.eventName.contains("NUEVO_CORPORATIVO")) {
            // we need to update the cache system variable
            val eventBody = eventDTO.eventBody as JsonNode      // as HashMap<*,*>
            val data = eventBody["datos"] as JsonNode          // as HashMap<*,*>
            val corporate = data["corporativo"] as JsonNode
            val corpDTO = mapper.treeToValue(corporate, CompaniaDTO::class.java)
            val administrator = data["administrador"]
            val adminDTO = mapper.treeToValue(administrator, UsuarioDTO::class.java)

            companiaService.newCorporate(corpDTO, adminDTO)
            logger.debug("We add a new Corporation:  ${corpDTO.nombre} with administrator ${adminDTO.nombre}")
            latch.countDown()       // just for testing purpose
        }

        return eventDTO
    }

}
