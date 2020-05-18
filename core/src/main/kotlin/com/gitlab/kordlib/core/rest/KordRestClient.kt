package com.gitlab.kordlib.core.rest

import com.gitlab.kordlib.common.entity.DiscordPartialGuild
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.*
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.rest.json.response.VoiceRegion
import com.gitlab.kordlib.rest.request.RequestException
import com.gitlab.kordlib.rest.service.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class KordRestClient(val kord: Kord, val client: RestClient) : EntitySupplier {

    val auditLog: AuditLogService get() = client.auditLog
    val channel: ChannelService get() = client.channel
    val emoji: EmojiService get() = client.emoji
    val guild: GuildService get() = client.guild
    val invite: InviteService get() = client.invite
    val user: UserService get() = client.user
    val voice: VoiceService get() = client.voice
    val webhook: WebhookService get() = client.webhook
    val application: ApplicationService get() = client.application

    /**
     * Requests to get the guilds available to the current application.
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     */
    override val guilds: Flow<Guild>
        get() = paginateForwards(idSelector = DiscordPartialGuild::id, batchSize = 100) { position -> user.getCurrentUserGuilds(position, 100) }
                .map { guild.getGuild(it.id) }
                .map { GuildData.from(it) }
                .map { Guild(it, kord) }

    /**
     * Requests to get the regions available to the current application.
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     */
    override val regions: Flow<Region>
        get() = flow {
            client.voice.getVoiceRegions().forEach { emit(it) }
        }.map { RegionData.from(it) }.map { Region(it, kord) }

    /**
     * Requests to get the channel with the given [id].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the channel with the given [id], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getChannel(id: Snowflake): Channel? = catchNotFound { Channel.from(channel.getChannel(id.value).toData(), kord) }

    override suspend fun getMessagesAfter(messageId: Snowflake, limit: Int): Flow<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun getMessagesBefore(messageId: Snowflake, limit: Int): Flow<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun getMessagesAround(messageId: Snowflake, limit: Int): Flow<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun getChannelPins(channelId: Snowflake): Flow<Message> {
        TODO("Not yet implemented")
    }

    /**
     * Requests to get the guild with the given [id].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the guild with the given [id], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getGuild(id: Snowflake): Guild? = catchNotFound { Guild(guild.getGuild(id.value).toData(), kord) }

    /**
     * Requests to get the member with the given [userId] in the [guildId].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the member with the given [userId], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member? = catchNotFound {
        val memberData = guild.getGuildMember(guildId = guildId.value, userId = userId.value).toData(guildId = guildId.value, userId = userId.value)
        val userData = user.getUser(userId.value).toData()
        return Member(memberData, userData, kord)
    }

    /**
     * Requests to get the message with the given [messageId] in the [channelId].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the message with the given [messageId], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message? = catchNotFound {
        Message(channel.getMessage(channelId = channelId.value, messageId = messageId.value).toData(), kord)
    }

    /**
     * Requests to get the user linked to the current [ClientResources.token].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     * @throws RequestException when the request failed.
     */
    override suspend fun getSelf(): User = User(user.getCurrentUser().toData(), kord)

    /**
     * Requests to get the user with the given [id].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * @return the user with the given [id], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getUser(id: Snowflake): User? = catchNotFound { User(user.getUser(id.value).toData(), kord) }

    /**
     * Requests to get the role with the given [roleId] in the given [guildId].
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     *
     * Note that this will effectively request all roles at once and then filter on the given id
     *
     * @return the role with the given [roleId], or null if the request returns a 404.
     * @throws RequestException when the request failed.
     */
    override suspend fun getRole(guildId: Snowflake, roleId: Snowflake): Role? = catchNotFound {
        val response = guild.getGuildRoles(guildId.value)
                .firstOrNull { it.id == roleId.value } ?: return@catchNotFound null

        return Role(RoleData.from(guildId.value, response), kord)
    }

    override suspend fun getGuildBan(guildId: Snowflake, userId: Snowflake): Ban {
        val response = guild.getGuildBan(guildId.value, userId.value)
        val data = BanData.from(response)
        return Ban(data, kord)

    }

    override suspend fun getGuildRoles(guildId: Snowflake): Flow<Role> =
            guild.getGuildRoles(guildId.value).asFlow().map { Role(RoleData.from(guildId.value, it), kord) }


    override suspend fun getGuildBans(guildId: Snowflake): Flow<Ban> =
            guild.getGuildBans(guildId.value).asFlow().map { Ban(BanData.from(it), kord) }

    override suspend fun getGuildMembers(guildId: Snowflake, limit: Int): Flow<Member> =
            guild.getGuildMembers(guildId.value).asFlow().map { Member(MemberData.from(it.user!!.id, guildId.value, it), UserData.from(it.user!!), kord) }


    override suspend fun getGuildVoiceRegions(guildId: Snowflake): Flow<VoiceRegion> =
            guild.getGuildVoiceRegions(guildId.value).asFlow()


    override suspend fun getReactors(channelId: Snowflake, messageId: Snowflake, emoji: ReactionEmoji): Flow<User> =
            paginateForwards(batchSize = 100, idSelector = { it.id }) { position ->
                kord.rest.channel.getReactions(
                        channelId = channelId.value,
                        messageId = messageId.value,
                        emoji = emoji.formatted,
                        limit = 100,
                        position = position
                )
            }.map { UserData.from(it) }.map { User(it, kord) }

    override suspend fun getEmoji(guildId: Snowflake, emojiId: Snowflake): ReactionEmoji? {
        TODO("Not yet implemented")
    }

    override suspend fun getEmojis(guildId: Snowflake): Flow<ReactionEmoji> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentUser(): User? {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentUserGuilds(): Flow<Guild> {
        TODO("Not yet implemented")
    }

    override suspend fun getChannelWebhooks(channelId: Snowflake): Flow<Webhook> {
        TODO("Not yet implemented")
    }

    override suspend fun getGuildWebhooks(guildId: Snowflake): Flow<Webhook> {
        TODO("Not yet implemented")
    }

    override suspend fun getWebhook(webhookId: Snowflake): Webhook? {
        TODO("Not yet implemented")
    }

    override suspend fun getWebhookWithToken(webhookId: Snowflake, token: String): Webhook? {
        TODO("Not yet implemented")
    }

    /**
     * Requests to get the information of the current application.
     *
     * Entities will be fetched from Discord directly, ignoring any cached values.
     * @throws RequestException when the request failed.
     */
    suspend fun getApplicationInfo(): ApplicationInfo {
        val response = application.getCurrentApplicationInfo()
        return ApplicationInfo(ApplicationInfoData.from(response), kord)
    }

}