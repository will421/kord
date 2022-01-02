package dev.kord.core.cache.data

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public class PartialGuildData(
    public val id: Snowflake,
    public val name: String,
    public val icon: String? = null,
    public val owner: OptionalBoolean = OptionalBoolean.Missing,
    public val permissions: Optional<Permissions> = Optional.Missing(),
    public val features: List<GuildFeature>,
    public val welcomeScreen: Optional<WelcomeScreenData> = Optional.Missing(),
    @SerialName("vanity_url_code") public val vanityUrlCode: Optional<String?> = Optional.Missing(),
    public val description: Optional<String?> = Optional.Missing(),
    public val banner: Optional<String?> = Optional.Missing(),
    public val splash: Optional<String?> = Optional.Missing(),
    @SerialName("nsfw_level") public val nsfwLevel: Optional<NsfwLevel> = Optional.Missing(),
    @SerialName("verification_level")
    public val verificationLevel: Optional<VerificationLevel> = Optional.Missing()

) {
    public companion object {
        public fun from(partialGuild: DiscordPartialGuild): PartialGuildData = with(partialGuild) {
            PartialGuildData(
                id,
                name,
                icon,
                owner,
                permissions,
                features,
                welcomeScreen.map { WelcomeScreenData.from(it) },
                vanityUrlCode,
                description,
                banner,
                splash,
                nsfwLevel,
                verificationLevel,
            )
        }
    }
}
