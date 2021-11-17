package dev.kord.rest.json.request

import dev.kord.common.entity.ScheduledEntityType
import dev.kord.common.entity.ScheduledEventMetadata
import dev.kord.common.entity.ScheduledEventStatus
import dev.kord.common.entity.ScheduledPrivacyLevel
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class ScheduledEventCreateRequest(
    @SerialName("channel_id")
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("entity_metadata")
    val entityMetadata: Optional<ScheduledEventMetadata> = Optional.Missing(),
    val name: String,
    @SerialName("privacy_level")
    val privacyLevel: ScheduledPrivacyLevel,
    @SerialName("scheduled_start_time")
    val scheduledStartTime: String,
    @SerialName("scheduled_end_time")
    val scheduledEndTime:Optional<String> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    @SerialName("entity_type")
    val entityType: ScheduledEntityType
)
@Serializable
data class ScheduledEventModifyPatchRequest(
    @SerialName("channel_id")
    val channelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("entity_metadata")
    val entityMetadata: Optional<ScheduledEventMetadata?> = Optional.Missing(),
    val name: Optional<String> = Optional.Missing(),
    @SerialName("privacy_level")
    val privacyLevel: ScheduledPrivacyLevel,
    @SerialName("scheduled_start_time")
    val scheduledStartTime: String,
    @SerialName("scheduled_end_time")
    val scheduledEndTime:Optional<String> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    @SerialName("entity_type")
    val entityType: ScheduledEntityType,
    val status: Optional<ScheduledEventStatus> = Optional.Missing()
)
