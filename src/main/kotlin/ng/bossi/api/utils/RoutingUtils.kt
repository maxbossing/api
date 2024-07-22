package ng.bossi.api.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.database.model.Application
import ng.bossi.api.database.model.SingleLicenseStatus
import kotlin.reflect.KClass

suspend inline fun ApplicationCall.applicationCall(
  block: (applicationId: Long, application: Application) -> Unit
) {
  val applicationId = parameters["applicationId"]?.toLongOrNull()

  if (applicationId == null) {
    respond(HttpStatusCode.BadRequest, "Invalid ApplicationId!")
    return
  }

  val application = DatabaseController.applicationService.read(applicationId)


  if (application == null) {
    respond(HttpStatusCode.NotFound, "Application not found!")
    return
  }

  block(applicationId, application)
}

suspend fun ApplicationCall.parameter(name: String): String? = request.queryParameters[name].let {
  it ?: respond(HttpStatusCode.BadRequest, "Missing Parameter $name")
  it
}

suspend fun ApplicationCall.longParameter(name: String): Long? {
  val param = parameter(name) ?: return null
  return param.toLongOrNull().let {
    it ?: respond(HttpStatusCode.BadRequest, "Malformed Parameter $name")
    it
  }
}

suspend fun ApplicationCall.boolParameter(name: String): Boolean? {
  val param = parameter(name) ?: return null
  return param.toBooleanStrictOrNull().let {
    it ?: respond(HttpStatusCode.BadRequest, "Malformed Parameter $name")
    it
  }
}

inline fun <reified T : Enum<T>> safeValueOf(type: String): T? = java.lang.Enum.valueOf(T::class.java, type)

suspend inline fun <reified T: Enum<T>> ApplicationCall.enumParameter(name: String): T? {
  val param = parameter(name) ?: return null
  return safeValueOf<T>(param).let {
    it ?: respond(HttpStatusCode.BadRequest, "Malformed Parameter $name")
    it
  }
}