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
 *  EventServiceTest.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.service.event

import com.ailegorreta.commons.event.EventDTO
import com.ailegorreta.commons.event.EventType
import com.ailegorreta.commons.utils.HasLogger
import com.ailegorreta.iamservice.config.ServiceConfig
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Service

/**
 * This service test is just to emulate how we can send an event for 'bup-service' in order that the
 * iam-service listen to it.
 *
 * It does NOT have to do enything with testing the EvenService.class
 *
 * @project: iam-service
 * @autho: rlh
 * @date September 2023
 */
@Service
class EventServiceTest(private val streamBridge: StreamBridge,
                       private val serviceConfig: ServiceConfig,
                       private val mapper: ObjectMapper): HasLogger {

    private val coreName = "bup-service"

    /**
     * This method sends an event using Spring cloud stream, i.e., streamBridge instance
     */
    fun sendEvent(correlationId: String = "CorrelationId-test",
                  userName: String = "Test",
                  eventName: String,
                  value: Any): EventDTO {
        val eventBody = mapper.readTree(mapper.writeValueAsString(value))
        val parentNode = mapper.createObjectNode()

        // Add the permit where notification will be sent
        parentNode.put("notificaFacultad", "NOTIFICA_BUP")
        // parentNode.put("datos",  eventBody!!.toString())
        parentNode.set<JsonNode>("datos", eventBody!!)

        val event = EventDTO(correlationId = correlationId ?: "NA",
                            eventType = EventType.DB_STORE,
                            username = userName,
                            eventName = eventName,
                            applicationName = serviceConfig.appName!!,
                            coreName = coreName,
                            eventBody = parentNode)

        logger.debug("Will send use stream bridge:$streamBridge")

        val res = streamBridge.send("producerTest-out-0", event)
        logger.debug("Result for sending the message via streamBridge:$res")

        return event
    }
}
