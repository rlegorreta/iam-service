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
 *  FacultadServiceTests.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */

package com.ailegorreta.iamservice.service.facultad

import com.ailegorreta.iamservice.EnableTestContainers
import com.ailegorreta.iamservice.model.FacultadTipo
import com.ailegorreta.iamservice.repository.FacultadRepository
import com.ailegorreta.iamservice.service.AbstractServiceTest
import com.ailegorreta.iamservice.service.facultad.dto.FacultadDTO
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime
import java.util.function.Consumer

/**
 * Test for the FacultadService.
 *
 * Warning: many Tests in this class updates the database do not run them
 * as a bulk test but run them individually.
 *
 * @author rlh
 * @project iam-service
 * @date September 2023
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableTestContainers
/* ^ This is a custom annotation to load the containers */
@ExtendWith(MockitoExtension::class)
@EmbeddedKafka(bootstrapServersProperty = "spring.kafka.bootstrap-servers")
/* ^ this is because: https://blog.mimacom.com/embeddedkafka-kafka-auto-configure-springboottest-bootstrapserversproperty/ */
@ActiveProfiles("integration-tests")
class FacultadServiceTests: AbstractServiceTest() {

    @Test
    fun `Add a new permit`(@Autowired facultadService : FacultadService) {
        val nombre = "SPEI"
        val facultadDTO = FacultadDTO(null, nombre, "Facultad de SPEIs",
                                      FacultadTipo.SIMPLE, "TEST", LocalDateTime.now(),true)

        assertThat(facultadService.add(facultadDTO))
            .satisfies(Consumer{ permit -> assertThat(permit?.nombre).isEqualTo(nombre) })
    }

}
