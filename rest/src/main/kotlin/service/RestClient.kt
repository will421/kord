package dev.kord.rest.service

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.request.RequestBuilder
import dev.kord.rest.route.Route
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Services(
    val auditLog: DefaultAuditLogService,
    val channel: DefaultChannelService,
    val emoji: DefaultEmojiService,
    val guild: DefaultGuildService,
    val invite: DefaultInviteService,
    val user: DefaultUserService,
    val voice: VoiceService,
    val webhook: WebhookService,
    val application: DefaultApplicationService,
    val template: DefaultTemplateService,
    val interaction: DefaultInteractionService,
    val stageInstance: DefaultStageInstanceService
)

class RestClient(requestHandler: RequestHandler, services: Services) : RestService(requestHandler) {
    val auditLog: DefaultAuditLogService = services.auditLog
    val channel: DefaultChannelService = services.channel
    val emoji: DefaultEmojiService = services.emoji
    val guild: DefaultGuildService = services.guild
    val invite: DefaultInviteService = services.invite
    val user: DefaultUserService = services.user
    val voice: VoiceService = services.voice
    val webhook: WebhookService = services.webhook
    val application: DefaultApplicationService = services.application
    val template: DefaultTemplateService = services.template
    val interaction: DefaultInteractionService = services.interaction
    val stageInstance: DefaultStageInstanceService = services.stageInstance

    /**
     * Sends a request to the given [route]. This function exposes a direct call to the Discord api and allows
     * the user to send a custom [RequestBuilder.body].
     *
     * Unless such functionality is specifically needed, users are advised to use the safer [RestService] calls.
     *
     * @param route The route to which to send a request.
     * @param block The configuration for this request.
     */
    @OptIn(ExperimentalContracts::class)
    @KordUnsafe
    @KordExperimental
    suspend inline fun <T> unsafe(route: Route<T>, block: RequestBuilder<T>.() -> Unit): T {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        return call(route) {
            block()
        }
    }
}

fun RestClient(token: String, services: Services): RestClient {
    val requestHandler = KtorRequestHandler(token)
    return RestClient(requestHandler, services)
}
