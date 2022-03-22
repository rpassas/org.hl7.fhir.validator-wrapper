package controller.validation

import model.ValidationResponse
import org.hl7.fhir.validation.cli.utils.VersionUtil
import org.hl7.fhir.validation.cli.model.ValidationRequest
import org.hl7.fhir.validation.cli.services.ValidationService
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.io.FileInputStream
import java.util.*

class ValidationControllerImpl : ValidationController, KoinComponent {

    private val validationService by inject<ValidationService>()

    override suspend fun validateRequest(validationRequest: ValidationRequest): ValidationResponse {
        return validationService.validateSources(validationRequest)
    }

    override suspend fun getValidatorVersion():String {
        return VersionUtil.getVersion()
    }
}