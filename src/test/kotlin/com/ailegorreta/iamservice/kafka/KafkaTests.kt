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
 *  KafkaTests.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice.kafka

import com.ailegorreta.commons.utils.HasLogger
import com.ailegorreta.iamservice.EnableTestContainers
import com.ailegorreta.iamservice.config.EventConfig
import com.ailegorreta.iamservice.config.ServiceConfig
import com.ailegorreta.iamservice.model.Negocio
import com.ailegorreta.iamservice.service.compania.dto.CompaniaDTO
import com.ailegorreta.iamservice.service.compania.dto.NuevoCorporativoDTO
import com.ailegorreta.iamservice.service.compania.dto.UsuarioDTO
import com.ailegorreta.iamservice.service.event.EventServiceConsumer
import com.ailegorreta.iamservice.service.event.EventServiceTest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/**
 * For a good test slices for testing @SpringBootTest, see:
 * https://reflectoring.io/spring-boot-test/
 * https://www.diffblue.com/blog/java/software%20development/testing/spring-boot-test-slices-overview-and-usage/
 *
 * This class test all context with @SpringBootTest annotation and checks that everything is loaded correctly.
 * Also creates the classes needed for all slices in @TestConfiguration annotation
 *
 * Testcontainers:
 *
 * Use for test containers Neo4j & Kafka following the next's ticks:
 *
 * - As little overhead as possible:
 * - Containers are started only once for all tests
 * - Containers are started in parallel
 * - No requirements for test inheritance
 * - Declarative usage.
 *
 * see article: https://maciejwalkowiak.com/blog/testcontainers-spring-boot-setup/
 *
 * Also for a problem with bootstrapServerProperty
 * see: https://blog.mimacom.com/embeddedkafka-kafka-auto-configure-springboottest-bootstrapserversproperty/
 *
 * @author rlh
 * @project : iam-service
 * @date September 2023
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableTestContainers
/* ^ This is a custom annotation to load the containers */
@ExtendWith(MockitoExtension::class)
@EmbeddedKafka(bootstrapServersProperty = "spring.kafka.bootstrap-servers")
/* ^ this is because: https://blog.mimacom.com/embeddedkafka-kafka-auto-configure-springboottest-bootstrapserversproperty/ */
@Import(ServiceConfig::class)
@ActiveProfiles("integration-tests")
@DirtiesContext                /* will make sure this context is cleaned and reset between different tests */
class KafkaTests: HasLogger {

    @MockBean
    private var reactiveJwtDecoder: ReactiveJwtDecoder? = null			// Mocked the security JWT

    /**
     * Do not use @MockBean because it initialises an empty ReactiveClientRegistration
     * Repository, so it crashed when create a WebClient.
     */
    @Autowired
    lateinit var reactiveClientRegistrationRepository: ReactiveClientRegistrationRepository

    @MockBean
    var authorizedClientRepository: OAuth2AuthorizedClientRepository?= null

    /**
     * For Kafka all are consumer events, the producer we use Kafka template instance
     */
    @Autowired
    var consumer: EventServiceConsumer? = null

    @Autowired
    var producer: EventServiceTest? = null		// EventServiceTest producer that emulates den a message

    @Autowired
    var mapper: ObjectMapper? = null

    @Autowired
    var eventConfig: EventConfig? = null

    /**
     * This test send an event 'NUEVO_CORPORATIVO' in order to insert a new Corporation.
     *
     * Testing with Spring Cloud stream, i.e., used the configuration defines in the application.yml file and not
     * in the Kafka test container, so we not use the Kafka template.
     */
    @Test
    fun whenSendingWithDefaultTemplate_thenMessageReceivedNewcorporate() {
        val nombre = "New Corporate"
        val idUsuario = 11000L
        val corpDTO = CompaniaDTO(0L, nombre, true, Negocio.GOBIERNO,
                                    "TEST")
        val adminDTO = UsuarioDTO(0L, idUsuario, "rlh", "Ricardo","Legorreta",
                            "5591495042", "staff@legosoft.com.mx",
                            false, true, true,
                            LocalDate.now(),"America/Mexico", "TEST")

        val newCorporation = NuevoCorporativoDTO(corporativo = corpDTO, administrador = adminDTO)
        val eventBody = mapper!!.readTree(mapper!!.writeValueAsString(newCorporation))

        logger.debug("Event body: $eventBody")
        producer!!.sendEvent(eventName = "NUEVO_CORPORATIVO", value = eventBody)

        val messageConsumed = consumer!!.latch.await(10, TimeUnit.SECONDS)
        logger.debug("After message consumed $messageConsumed")

        assertTrue(messageConsumed)
    }

}
