package dev.kord.rest.service

import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

class DefaultVoiceService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun getVoiceRegions() = call(Route.VoiceRegionsGet)

}
