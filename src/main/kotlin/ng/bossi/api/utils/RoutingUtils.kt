package ng.bossi.api.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import ng.bossi.api.database.DatabaseController
import ng.bossi.api.model.Application
import ng.bossi.api.model.Resource

suspend inline fun ApplicationCall.applicationCall(
  block: (applicationId: Long, applicationName: String, application: Application) -> Unit,
) {
  val applicationName = parameters["applicationName"]
  if (applicationName.isNullOrBlank()){
    respond(HttpStatusCode.BadRequest, "Application name must not be empty")
    return
  }

  val application = DatabaseController.applicationService.readByName(applicationName)

  if (application == null) {
    respond(HttpStatusCode.NotFound, "Application not found!")
    return
  }

  block(application.first, application.second.name, application.second)
}

suspend inline fun ApplicationCall.resourceCall(
  block: (resourceId: Long, resource: Resource) -> Unit,
) {
  val resourceIdParam = parameters["resourceId"]

  if (resourceIdParam.isNullOrBlank()){
    respond(HttpStatusCode.BadRequest, "Resource id must not be empty")
    return
  }

  val resourceId = resourceIdParam.toLongOrNull()
  if (resourceId == null) {
    respond(HttpStatusCode.BadRequest, "Invalid Resource Id!")
    return
  }

  val resource = DatabaseController.resourceService.read(resourceId)

  if (resource == null) {
    respond(HttpStatusCode.NotFound, "Resource not found!")
    return
  }

  block(resourceId, resource)
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

suspend inline fun <reified T : Enum<T>> ApplicationCall.enumParameter(name: String): T? {
  val param = parameter(name) ?: return null
  return safeValueOf<T>(param).let {
    it ?: respond(HttpStatusCode.BadRequest, "Malformed Parameter $name")
    it
  }
}