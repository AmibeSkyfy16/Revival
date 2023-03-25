package ch.skyfy.revival.item

import ch.skyfy.json5configlib.updateIterable
import ch.skyfy.revival.config.Configs
import ch.skyfy.revival.persistent.DeadPlayers
import ch.skyfy.revival.persistent.Persistents
import ch.skyfy.revival.utils.ModUtils
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.GameMode
import net.minecraft.world.World

class RevivalArtifactItem : Item(FabricItemSettings().maxCount(1)) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (world.isClient) return super.use(world, user, hand)

        user.server!!.playerManager.playerList.filter { it.isSpectator }.minByOrNull { it.squaredDistanceTo(user.x, user.y, user.z) }?.let { spe ->

            Persistents.DEATH_PLAYERS.serializableData.list.firstOrNull { it.playerNameWithUUID == ModUtils.getPlayerNameWithUUID(spe) }?.let { deadPlayer ->
                if (System.currentTimeMillis() - deadPlayer.deathTimestamp / 1000 / 60 <= Configs.CONFIG.serializableData.maximumWanderTimeInMinute) {
                    spe.changeGameMode(GameMode.SURVIVAL)
                    user.getStackInHand(hand).count = 0
                    spe.health = Configs.CONFIG.serializableData.revivedPlayerHealth
                    Persistents.DEATH_PLAYERS.updateIterable(DeadPlayers::list) { list ->
                        list.remove(deadPlayer)
                    }
                } else {
                    user.sendMessage(Text.literal("Entity ${spe.name.string} cannot be revived, he wander for more than ${Configs.CONFIG.serializableData.maximumWanderTimeInMinute} minutes"))
                }
            }


            // TODO make a custom sound
//            spe.world.playSoundAtBlockCenter(user.blockPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 0.6f, 0.6f, true)

        }

        return super.use(world, user, hand)
    }

}