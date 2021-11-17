package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class DiscordGuildScheduledEvent(
    val id: Snowflake,
    @SerialName("guild_name")
    val guildId: Snowflake?,
    val creatorId: OptionalSnowflake = OptionalSnowflake.Missing,
    val name: String,
    val description: Optional<String> = Optional.Missing(),
    @SerialName("scheduled_start_time")
    val scheduledStartTime: String,
    @SerialName("scheduled_end_time")
    val scheduledEndTime: String?,
    @SerialName("privacy_level")
    val privacyLevel: ScheduledPrivacyLevel,
    val status: ScheduledEventStatus,
    val entityType: ScheduledEntityType,
    val entityId: Snowflake?,
    val entityMetadata: ScheduledEventMetadata?,
    val creator: Optional<DiscordUser> = Optional.Missing(),
    val userCount: OptionalInt = OptionalInt.Missing

)
@Serializable(ScheduledPrivacyLevel.PrivacyLevelSerializer::class)
sealed class ScheduledPrivacyLevel(val code: Int) {
    object Public : ScheduledPrivacyLevel(1)
    object GuildOnly : ScheduledPrivacyLevel(2)
    class Unknown(code: Int) : ScheduledPrivacyLevel(code)

    internal object PrivacyLevelSerializer : KSerializer<ScheduledPrivacyLevel> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("privacyLevel", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ScheduledPrivacyLevel {
            val code = decoder.decodeInt()
            return values.firstOrNull { it.code == code } ?: Unknown(code)
        }

        override fun serialize(encoder: Encoder, value: ScheduledPrivacyLevel) {
            encoder.encodeInt(value.code)
        }
    }

    companion object {
        val values: Set<ScheduledPrivacyLevel>
            get() = setOf(
                Public,
                GuildOnly
            )
    }
}

@Serializable(ScheduledEventStatus.Serializer::class)
sealed class ScheduledEventStatus(val code: Int) {
    object Scheduled: ScheduledEventStatus(1)
    object Active : ScheduledEventStatus(2)
    object Completed : ScheduledEventStatus(3)
    object Canceled: ScheduledEventStatus(4)
    class Unknown(code: Int) : ScheduledEventStatus(code)

    internal object Serializer : KSerializer<ScheduledEventStatus> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("scheduledEventStatus", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ScheduledEventStatus {
            val code = decoder.decodeInt()
            return values.firstOrNull { it.code == code } ?: Unknown(code)
        }

        override fun serialize(encoder: Encoder, value: ScheduledEventStatus) {
            encoder.encodeInt(value.code)
        }
    }

    companion object {
        val values: Set<ScheduledEventStatus>
            get() = setOf(
                Scheduled,
                Active,
                Completed,
                Canceled
            )
    }
}

@Serializable(ScheduledEntityType.Serializer::class)
sealed class ScheduledEntityType(val code: Int) {
    object None: ScheduledEntityType(0)
    object StageInstance : ScheduledEntityType(1)
    object Voice : ScheduledEntityType(2)
    object External: ScheduledEntityType(3)
    class Unknown(code: Int) : ScheduledEntityType(code)

    internal object Serializer : KSerializer<ScheduledEntityType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("scheduledEntityType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ScheduledEntityType {
            val code = decoder.decodeInt()
            return values.firstOrNull { it.code == code } ?: Unknown(code)
        }

        override fun serialize(encoder: Encoder, value: ScheduledEntityType) {
            encoder.encodeInt(value.code)
        }
    }

    companion object {
        val values: Set<ScheduledEntityType>
            get() = setOf(
                None,
                StageInstance,
                Voice,
                External
            )
    }
}

@Serializable
data class ScheduledEventMetadata(
    val speakerIds: Optional<List<Snowflake>> = Optional.Missing(),
    val location: Optional<String> = Optional.Missing()
)
