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
 *  IamServiceApplication.kt
 *
 *  Developed 2023 by LegoSoftSoluciones, S.C. www.legosoft.com.mx
 */
package com.ailegorreta.iamservice

import com.ailegorreta.commons.utils.HasLogger
import com.ailegorreta.iamservice.config.ServiceConfig
import com.ailegorreta.iamservice.repository.FacultadRepository
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import org.neo4j.driver.Driver
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * IAM repository service project. This server makes all IAMdb graph database access from any microservice and
 * administration.
 *
 * note: the auth-service reads the IAMdb graph database just for a matter of simplicity and efficiency
 *
 * @project : iam-service
 * @author rlh
 * @date September 2023
 */
@SpringBootApplication
@ComponentScan(basePackages = ["com.ailegorreta.resourceserver", "com.ailegorreta.iamservice"])
class IamServiceApplication {
	@Bean
	fun kotlinPropertyConfigurer(): PropertySourcesPlaceholderConfigurer {
		val propertyConfigurer = PropertySourcesPlaceholderConfigurer()

		propertyConfigurer.setPlaceholderPrefix("@{")
		propertyConfigurer.setPlaceholderSuffix("}")
		propertyConfigurer.setIgnoreUnresolvablePlaceholders(true)

		return propertyConfigurer
	}

	@Bean
	fun defaultPropertyConfigurer() = PropertySourcesPlaceholderConfigurer()

	@Bean
	fun mapperConfigurer() = Jackson2ObjectMapperBuilder().apply {
		serializationInclusion(JsonInclude.Include.NON_NULL)
		failOnUnknownProperties(true)
		featuresToDisable(*arrayOf(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS))
		indentOutput(true)
		modules(listOf(KotlinModule.Builder().build(), JavaTimeModule(), Jdk8Module()))
	}

	/**
	 * Class that checks if we need to initialize the Neo4j database for the first time.
	 * We check if exists a permit (facultad). If it is empty then we delete all nodes
	 * and initializes the data defined in the neo4j Flyway script.
	 *
	 * note: This class must be commented for production
	 */
	@Component
	class DataInitializer constructor(private val facultadRepository: FacultadRepository,
									  private val driver: Driver,
									  private val serviceConfig: ServiceConfig): ApplicationRunner, HasLogger {
		override fun run(args: ApplicationArguments?) {
			if (facultadRepository.count() == 0L) {
				logger.info("The Noe4j database is empty... We fill it with minimum security data")
				BufferedReader(InputStreamReader(this.javaClass.getResourceAsStream(
					"${serviceConfig.neo4jFlywayLocations}/iamDBstart.cypher"))).use { testReader ->
					driver.session().use { session ->
						// NOTE: This command will erase ALL database data. Comment if we don´t want to
						//       delete all data since the test.cypher utilizes MERGE instead of CREATE
						session.run("MATCH (n) DETACH DELETE n")

						val startCypher = testReader.readText()

						// consume all results from the driver
						session.run(startCypher)
							   .consume()
					}
				}
				logger.info("The database Neo4j has been initialized...")
				logger.info("Check that the constraints exists:")
				logger.info("  CREATE CONSTRAINT unique_compania FOR (compania:Compania) REQUIRE compania.nombre IS UNIQUE")
				logger.info("  CREATE CONSTRAINT unique_usuario FOR (usuario:Usuario) REQUIRE usuario.idUsuario IS UNIQUE")
				logger.info("  CREATE CONSTRAINT unique_usuario2 FOR (usuario:Usuario) REQUIRE usuario.nombreUsuario IS UNIQUE")
				logger.info("  CREATE CONSTRAINT unique_area FOR (area:Area) REQUIRE area.isArea IS UNIQUE")
				logger.info("  CREATE CONSTRAINT unique_grupo FOR (grupo:Grupo) REQUIRE grupo.nombre IS UNIQUE")
			} else
				logger.debug("The database Neo4j has already data...")
		}
	}


}

fun main(args: Array<String>) {
	runApplication<IamServiceApplication>(*args)
}
