package ch.skyfy.revival

import ch.skyfy.json5configlib.ConfigManager
import ch.skyfy.json5configlib.updateIterable
import ch.skyfy.revival.callback.EntityDamageCallback
import ch.skyfy.revival.callback.EntityMoveCallback
import ch.skyfy.revival.command.ReloadFilesCmd
import ch.skyfy.revival.config.Configs
import ch.skyfy.revival.data.Pos
import ch.skyfy.revival.item.ModItems
import ch.skyfy.revival.persistent.DeadPlayer
import ch.skyfy.revival.persistent.DeadPlayers
import ch.skyfy.revival.persistent.Persistents
import ch.skyfy.revival.utils.ModUtils
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.MovementType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.GameMode
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Path
import java.util.Timer
import java.util.TimerTask
import kotlin.io.path.*

@Suppress("MemberVisibilityCanBePrivate")
class RevivalMod : ModInitializer {

    companion object {
        const val MOD_ID: String = "revival"
        val CONFIG_DIRECTORY: Path = FabricLoader.getInstance().configDir.resolve(MOD_ID)
        val PERSISTENTS_DIRECTORY: Path = CONFIG_DIRECTORY.resolve("persistents")
        val LOGGER: Logger = LogManager.getLogger(RevivalMod::class.java)
    }

    init {
        ConfigManager.loadConfigs(arrayOf(Configs::class.java))
    }

    override fun onInitialize() {
        registerCommands()
        registerEvents()
        ModItems.registerModItems()

        // TODO add the revival item to loot tables like igloo loot table, etc.
    }

    private fun registerCommands() = CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
        ReloadFilesCmd.register(dispatcher)
    }

    private fun registerEvents() {
        EntityDamageCallback.EVENT.register(::onEntityDamage)
        EntityMoveCallback.EVENT.register(::onEntityMoved)
        ServerPlayerEvents.AFTER_RESPAWN.register(::afterRespawn)
    }

    private fun afterRespawn(oldPlayer: ServerPlayerEntity, newPlayer: ServerPlayerEntity, alive: Boolean) {
        if ((!Configs.CONFIG.serializableData.onlyUseOnHardcore && !newPlayer.server.isHardcore) || newPlayer.server.isHardcore) {
            Persistents.DEATH_PLAYERS.serializableData.list.firstOrNull { it.playerNameWithUUID == ModUtils.getPlayerNameWithUUID(newPlayer) }?.let {
                newPlayer.changeGameMode(GameMode.SPECTATOR)
                newPlayer.teleport(it.deathLocation.x.toDouble(), it.deathLocation.y.toDouble(), it.deathLocation.z.toDouble())
            }
        }
    }

    fun onEntityDamage(livingEntity: LivingEntity, damageSource: DamageSource, fl: Float) {
        if (livingEntity !is ServerPlayerEntity) return

        if (livingEntity.health <= 0) {
            livingEntity.server.sendMessage(Text.literal("Player ${livingEntity.name.string} is dead, his soul will wander around for the next ${Configs.CONFIG.serializableData.maximumWanderTimeInMinute} minutes\nafter which he will go to a more distant dimension (impossible to bring him back)"))

            Persistents.DEATH_PLAYERS.updateIterable(DeadPlayers::list) { list ->
                val playerNameWithUUID = ModUtils.getPlayerNameWithUUID(livingEntity)
                if (list.none { it.playerNameWithUUID == playerNameWithUUID }) {
                    list.add(DeadPlayer(playerNameWithUUID, Pos(livingEntity.blockX, livingEntity.blockY, livingEntity.blockZ), System.currentTimeMillis()))
                }
            }
        }
    }

    private fun onEntityMoved(entity: Entity, movementType: MovementType, vec3d: Vec3d): ActionResult {
        if (entity !is ServerPlayerEntity) return ActionResult.PASS

        // Dead players wander in an area near the place where they died and can't go any further
        Persistents.DEATH_PLAYERS.serializableData.list.firstOrNull { it.playerNameWithUUID == ModUtils.getPlayerNameWithUUID(entity) }?.let { deathPlayer ->
            val maximum = Configs.CONFIG.serializableData.maximumWanderBox
            val pos = deathPlayer.deathLocation

            if (entity.blockX > pos.x + maximum || entity.blockX < pos.x - maximum ||
                entity.blockY > pos.y + maximum || entity.blockY < pos.y - maximum ||
                entity.blockZ > pos.z + maximum || entity.blockZ < pos.z - maximum
            ) {
                entity.teleport(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                return ActionResult.FAIL
            }
        }
        return ActionResult.PASS
    }

}


