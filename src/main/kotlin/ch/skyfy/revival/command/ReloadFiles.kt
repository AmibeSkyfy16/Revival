package ch.skyfy.revival.command

import ch.skyfy.json5configlib.ConfigManager
import ch.skyfy.revival.RevivalMod
import ch.skyfy.revival.config.Configs
import com.mojang.brigadier.Command
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import java.util.concurrent.CompletableFuture

class ReloadFilesCmd : Command<ServerCommandSource> {

    companion object {
        fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
            val cmd = literal("revival").requires { source -> source.hasPermissionLevel(4) }
                .then(
                    literal("reload").then(
                        argument("fileName", StringArgumentType.string()).suggests(Companion::getConfigFiles).executes(ReloadFilesCmd())
                    )
                )
            dispatcher.register(cmd)
        }

        @Suppress("UNUSED_PARAMETER")
        private fun <S : ServerCommandSource> getConfigFiles(commandContext: CommandContext<S>, suggestionsBuilder: SuggestionsBuilder): CompletableFuture<Suggestions> {
            val list = mutableListOf<String>()
            list.add(Configs.CONFIG.relativePath.fileName.toString())
            list.add("ALL")
            return CommandSource.suggestMatching(list, suggestionsBuilder)
        }
    }

    override fun run(context: CommandContext<ServerCommandSource>): Int {
        val fileName = StringArgumentType.getString(context, "fileName")
        val list = mutableListOf<Boolean>()
        if (fileName == "ALL") {
            list.add(ConfigManager.reloadConfig(Configs.CONFIG))
        } else {
            when (fileName) {
                "config.json5" -> list.add(ConfigManager.reloadConfig(Configs.CONFIG))
            }
        }

        if (list.contains(false)) {
            context.source.sendFeedback(Text.literal("Configuration could not be reloaded"), false)
            RevivalMod.LOGGER.warn("Configuration could not be reloaded")
        } else {
            context.source.sendFeedback(Text.literal("The configuration was successfully reloaded"), false)
            RevivalMod.LOGGER.info("The configuration was successfully reloaded")
        }

        return SINGLE_SUCCESS
    }

}