package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.Optional
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
public class ActionRowBuilder : MessageComponentBuilder {
    public val components: MutableList<ActionRowComponentBuilder> = mutableListOf()

    public inline fun interactionButton(
        style: ButtonStyle,
        customId: String,
        builder: ButtonBuilder.InteractionButtonBuilder.() -> Unit
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(
            ButtonBuilder.InteractionButtonBuilder(style, customId).apply(builder)
        )
    }

    public inline fun linkButton(
        url: String,
        builder: ButtonBuilder.LinkButtonBuilder.() -> Unit
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(
            ButtonBuilder.LinkButtonBuilder(url).apply(builder)
        )
    }

    /**
     * Creates and adds a select menu with the [customId] and configured by the [builder].
     * An ActionRow with a select menu cannot have any other select menus or buttons.
     */
    public inline fun selectMenu(customId: String, builder: SelectMenuBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(SelectMenuBuilder(customId).apply(builder))
    }

    override fun build(): DiscordComponent =
        DiscordComponent(
            ComponentType.ActionRow,
            components = Optional.missingOnEmpty(components.map(ActionRowComponentBuilder::build))
        )
}
