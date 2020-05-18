package com.gitlab.kordlib.core.cache

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.DataEntryCache
import com.gitlab.kordlib.cache.api.data.DataDescription
import com.gitlab.kordlib.cache.api.delegate.DelegatingDataCache
import com.gitlab.kordlib.cache.api.delegate.EntrySupplier
import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.cache.map.MapLikeCollection
import com.gitlab.kordlib.cache.map.internal.MapEntryCache
import com.gitlab.kordlib.cache.map.lruLinkedHashMap
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplier
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.rest.json.response.GetPruneResponse
import com.gitlab.kordlib.rest.json.response.VoiceRegion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

typealias Generator<I, T> = (cache: DataCache, description: DataDescription<T, I>) -> DataEntryCache<out T>

class KordCache(val kord: Kord, val cache: DataCache) : DataCache by cache, EntitySupplier {

    val channels: Flow<Channel>
        get() = find<ChannelData>().asFlow().map { Channel.from(it, kord) }

    override val guilds: Flow<Guild>
        get() = find<GuildData>().asFlow().map { Guild(it, kord) }

    override val regions: Flow<Region>
        get() = find<RegionData>().asFlow().map { Region(it, kord) }

    val roles: Flow<Role>
        get() = find<RoleData>().asFlow().map { Role(it, kord) }

    val users: Flow<User>
        get() = find<UserData>().asFlow().map { User(it, kord) }

    @Suppress("EXPERIMENTAL_API_USAGE")
    val members: Flow<Member>
        get() = find<UserData>().asFlow().flatMapConcat { userData ->
            find<MemberData> { MemberData::userId eq userData.id }
                    .asFlow().map { Member(it, userData, kord) }
        }

    override suspend fun getChannel(id: Snowflake): Channel? {
        val data = find<ChannelData> { ChannelData::id eq id.longValue }.singleOrNull() ?: return null
        return Channel.from(data, kord)
    }

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

    override suspend fun getGuild(id: Snowflake): Guild? {
        val data = find<GuildData> { GuildData::id eq id.longValue }.singleOrNull() ?: return null
        return Guild(data, kord)
    }

    override suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member? {
        val userData = find<UserData> { UserData::id eq userId.longValue }.singleOrNull() ?: return null

        val memberData = find<MemberData> {
            MemberData::userId eq userId.longValue
            MemberData::guildId eq guildId.longValue
        }.singleOrNull() ?: return null

        return Member(memberData, userData, kord)
    }

    override suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message? {
        val data = find<MessageData> { MessageData::id eq messageId.longValue }.singleOrNull() ?: return null

        return Message(data, kord)
    }

    override suspend fun getRole(guildId: Snowflake, roleId: Snowflake): Role? {
        val data = find<RoleData> {
            RoleData::id eq roleId.longValue
            RoleData::guildId eq guildId.longValue
        }.singleOrNull() ?: return null

        return Role(data, kord)
    }

    override suspend fun getGuildBan(guildId: Snowflake, userId: Snowflake): Ban? {
        TODO("Not yet implemented")
    }

    override suspend fun getGuildRoles(guildId: Snowflake): Flow<Role> {
        return find<RoleData>() {
            RoleData::guildId eq guildId
        }.asFlow().map { Role(it, kord) }
    }

    override suspend fun getGuildBans(guildId: Snowflake): Flow<Ban> {
        TODO("Not yet implemented")
    }

    override suspend fun getGuildMembers(guildId: Snowflake, limit: Int): Flow<Member> {
        TODO("Not yet implemented")
    }

    override suspend fun getGuildMembers(guildId: Snowflake): Flow<Member> {
        return  find<UserData>().asFlow().flatMapConcat { userData ->
            find<MemberData> {
                MemberData::userId eq userData.id
                MemberData::guildId eq guildId
            }
                    .asFlow().map { Member(it, userData, kord) }
        }
    }

    override suspend fun getGuildVoiceRegions(guildId: Snowflake): Flow<VoiceRegion> {

    }

    override suspend fun getGuildPruneCount(guildId: Snowflake, days: Int): GetPruneResponse? {
        TODO("Not yet implemented")
    }

    override suspend fun getReactors(channelId: Snowflake, messageId: Snowflake, emoji: ReactionEmoji): Flow<User> {
        val message = find<MessageData> {
                MessageData::id eq messageId.value
                MessageData::channelId eq channelId.value
        }.singleOrNull() ?: return emptyFlow()
        message.reactions.map { it.id }
    }

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

    suspend fun getRole(id: Snowflake): Role? {
        val data = find<RoleData> { RoleData::id eq id.longValue }.singleOrNull() ?: return null

        return Role(data, kord)
    }

    override suspend fun getSelf(): User? = getUser(kord.selfId)

    override suspend fun getUser(id: Snowflake): User? {
        val data = find<UserData> { UserData::id eq id.longValue }.singleOrNull() ?: return null

        return User(data, kord)
    }

}

class KordCacheBuilder {
    var defaultGenerator: Generator<Any, Any> = { cache, description ->
        MapEntryCache(cache, description, MapLikeCollection.fromThreadSafe(ConcurrentHashMap()))
    }

    private val descriptionGenerators: MutableMap<DataDescription<*, *>, Generator<*, *>> = mutableMapOf()

    fun <T : Any, I : Any> lruCache(size: Int = 100): Generator<T, I> = { cache, description ->
        MapEntryCache(cache, description, MapLikeCollection.lruLinkedHashMap(size))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any, I : Any> forDescription(description: DataDescription<T, I>, generator: Generator<T, I>?) {
        if (generator == null) {
            descriptionGenerators.remove(description)
            return
        }
        descriptionGenerators[description] = generator as Generator<*, *>
    }

    fun build(): DataCache = DelegatingDataCache(EntrySupplier.invoke { cache, description ->
        val generator = descriptionGenerators[description] ?: defaultGenerator
        generator(cache, description)
    })

}