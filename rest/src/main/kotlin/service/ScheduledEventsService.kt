package dev.kord.rest.service

import dev.kord.common.entity.DiscordGuildScheduledEvent
import dev.kord.common.entity.ScheduledEventStatus
import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.request.ScheduledEventCreateRequest
import dev.kord.rest.json.request.ScheduledEventModifyPatchRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

class ScheduledEventService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend fun getScheduledEvent(guildId: Snowflake, eventId: Snowflake): DiscordGuildScheduledEvent =
        call(Route.ScheduledGuildEventGet) {
            keys[Route.ScheduledEventId] = eventId
            keys[Route.GuildId] = guildId
        }

    suspend fun deleteScheduledEvent(guildId: Snowflake, eventId: Snowflake): Unit =
        call(Route.ScheduledGuildEventDelete) {
            keys[Route.ScheduledEventId] = eventId
            keys[Route.GuildId] = guildId
        }

    suspend fun modifyScheduledEvent(
        guildId: Snowflake,
        eventId: Snowflake,
        request: ScheduledEventModifyPatchRequest
    ): Unit = call(Route.ScheduledGuildEventDelete) {
        keys[Route.ScheduledEventId] = eventId
        keys[Route.GuildId] = guildId
        body(ScheduledEventModifyPatchRequest.serializer(), request)
    }


    suspend fun createScheduledEvent(
        guildId: Snowflake,
        request: ScheduledEventCreateRequest
    ): Unit = call(Route.ScheduledGuildEventDelete) {
        keys[Route.GuildId] = guildId
        body(ScheduledEventCreateRequest.serializer(), request)
    }


}