package dev.kord.rest.service

import dev.kord.rest.json.response.ApplicationInfoResponse
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

class DefaultApplicationService(handler: RequestHandler) : RestService(handler) {

    suspend fun getCurrentApplicationInfo(): ApplicationInfoResponse = call(Route.CurrentApplicationInfo)

}