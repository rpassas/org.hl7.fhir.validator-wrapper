package controller.validation

import constants.VALIDATION_ENDPOINT
import constants.HL7_VALIDATION_ENDPOINT
import constants.RESOURCE_VALIDATION_ENDPOINT
import constants.ID_VALIDATION_ENDPOINT
import constants.DEFAULT_SESSION_ENDPOINT
import constants.VALIDATOR_VERSION_ENDPOINT
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import model.FhirVersionsResponse
import model.ValidationRequest
import model.CliContext
import model.asString
import org.koin.ktor.ext.inject
import utils.badFileEntryExists
import utils.buildRequest

const val DEBUG_NUMBER_FILES = "Received %d files to validate."
const val NO_FILES_PROVIDED_MESSAGE = "No files for validation provided in request."
const val INVALID_FILE_MESSAGE = "Improperly formatted file content!"

fun Route.validationModule() {

    val validationController by inject<ValidationController>()
    val validationPaths = arrayOf(VALIDATION_ENDPOINT, HL7_VALIDATION_ENDPOINT, RESOURCE_VALIDATION_ENDPOINT, ID_VALIDATION_ENDPOINT)
    
    validationPaths.forEach { path -> post(path) {
        val logger = call.application.environment.log
        val request = buildRequest(call.receiveText())
        val profileParam = call.request.queryParameters["profile"]
        val igParam = call.request.queryParameters["ig"]
        val profile: List<String?>  = listOf(profileParam)
        val ig: List<String?>  = listOf(igParam)
        if (profile.get(0) != null) {
            request.getCliContext().setProfiles(profile)
        }
        """
        if (ig.get(0) != null) {
            request.getCliContext().setIgs(ig)
            val guide: String? = ig.get(0)
                if (guide != null) {
                    val sessionId = validationController.initSession(guide)
                    request.setSessionId(sessionId)
                }
        } else {
            logger.info("WARNING: No IG provided in request")
            val id = validationController.getDefaultSessionId()
            if (request.getSessionId() == null &&  id != "") {
                request.setSessionId(validationController.getDefaultSessionId())
            }
        }
        """
        logger.info("Received Validation Request. FHIR Version: ${request.cliContext.sv} IGs: ${request.cliContext.igs} Profiles: ${request.cliContext.profiles} Session: ${request.sessionId} Memory (free/max): ${java.lang.Runtime.getRuntime().freeMemory()}/${java.lang.Runtime.getRuntime().maxMemory()}")
        logger.debug(DEBUG_NUMBER_FILES.format(request.filesToValidate.size))
        request.filesToValidate.forEachIndexed { index, file ->
            logger.debug("file [$index] ->\n${file.asString()}")
        }

        when {
            request.filesToValidate == null || request.filesToValidate.isEmpty() -> {
                call.respond(HttpStatusCode.BadRequest, NO_FILES_PROVIDED_MESSAGE)
            }
            badFileEntryExists(logger, request.filesToValidate) -> {
                call.respond(HttpStatusCode.BadRequest, INVALID_FILE_MESSAGE)
            }
            else -> {
                try {
                    call.respond(HttpStatusCode.OK, validationController.validateRequest(request))
                } catch (e: Exception) {
                    logger.error(e.localizedMessage)
                    call.respond(HttpStatusCode.InternalServerError, e.localizedMessage)
                } catch (e: Error){
                    logger.error(e.localizedMessage)
                    call.respond(HttpStatusCode.InternalServerError, e.localizedMessage)
                }
            }
        }
    } }
    
    post(DEFAULT_SESSION_ENDPOINT){
        val igs: Set<String?> = call.receiveText().toSet()
        val sessionId = validationController.initSession(igs)
        if (sessionId != null) {
            call.respond(HttpStatusCode.OK, listOf<String>(sessionId))
        } else {
            call.respond(HttpStatusCode.OK, "No Session")
        }
 
    }

    get(VALIDATOR_VERSION_ENDPOINT) {
        call.respond(HttpStatusCode.OK, validationController.getValidatorVersion())
    }
}