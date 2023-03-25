package ch.skyfy.revival.item

import ch.skyfy.revival.RevivalMod.Companion.LOGGER
import ch.skyfy.revival.RevivalMod.Companion.MOD_ID
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.EnderPearlItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

@Suppress("MemberVisibilityCanBePrivate", "SameParameterValue")
object ModItems {

    val REVIVAL_ARTIFACT: Item = registerItem("revival_artifact", RevivalArtifactItem())


    private fun registerItem(name: String, item: Item) = Registry.register(Registries.ITEM, Identifier(MOD_ID, name), item)

    private fun addToItemGroup(itemGroup: ItemGroup, item: Item) = ItemGroupEvents.modifyEntriesEvent(itemGroup).register { it.add(item) }


    private fun addItemsToItemGroup(){
        addToItemGroup(ItemGroups.TOOLS, REVIVAL_ARTIFACT)
    }

    fun registerModItems(){
        LOGGER.info("Registering Mod Items for $MOD_ID")
        addItemsToItemGroup()
    }

}