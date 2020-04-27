package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.behavior.RoleBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.cache.data.EmojiData
import com.gitlab.kordlib.core.toSnowflakeOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

/**
 * An instace of a [Discord emoji](https://discordapp.com/developers/docs/resources/emoji#emoji-object) belonging to a specific guild.
 */
class GuildEmoji(val data: EmojiData, val guildId: Snowflake, override val kord: Kord, override val strategy: EntitySupplyStrategy = kord.resources.defaultStrategy
) : Entity, Strategilizable {


    override val id: Snowflake
        get() = Snowflake(data.id)

    /**
     * Whether is emoji is animated.
     */
    val isAnimated: Boolean get() = data.animated

    /**
     * Whether is emote is managed by Discord instead of the guild members.
     */
    val isManaged: Boolean get() = data.managed

    /**
     * The name of this emoji.
     *
     * This property can be null when trying to get the name of an emoji that was deleted.
     */
    val name: String? get() = data.name

    /**
     * Whether this emoji needs to be wrapped in colons.
     */
    val requiresColons: Boolean get() = data.requireColons

    /**
     * The ids of the [roles][Role] for which this emoji was whitelisted.
     */
    val roleIds: Set<Snowflake> get() = data.roles.asSequence().map { Snowflake(it) }.toSet()

    /**
     * The behaviors of the [roles][Role] for which this emoji was whitelisted.
     */
    val roleBehaviors: Set<RoleBehavior> get() = data.roles.asSequence().map { RoleBehavior(guildId = guildId, id = id, kord = kord) }.toSet()

    /**
     * The [roles][Role] for which this emoji was whitelisted.
     */
    val roles: Flow<Role> get() = roleIds.asFlow().map { strategy.supply(kord).getRole(guildId, id) }.filterNotNull()

    /**
     * The behavior of the [Member] who created the emote, if present.
     */
    val member: MemberBehavior? get() = userId?.let { MemberBehavior(guildId, it, kord) }

    /**
     * The id of the [User] who created the emote, if present.
     */
    val userId: Snowflake? get() = data.user?.id.toSnowflakeOrNull()

    /**
     * The [User] who created the emote, if present.
     */
    val user: UserBehavior? get() = userId?.let { UserBehavior(it, kord) }

    /**
     * Requests to get the [Member] who created the emote, if present.
     */
    suspend fun getMember(): Member? = userId?.let { strategy.supply(kord).getMember(guildId = guildId, userId = it) }

    /**
     * Requests to get the [User] who created the emote, if present.
     */
    suspend fun getUser(): User? = userId?.let { strategy.supply(kord).getUser(it) }

    /**
     * returns a new [GuildEmoji] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */
    fun withStrategy(strategy: EntitySupplyStrategy) = GuildEmoji(data, guildId, kord, strategy)

}

