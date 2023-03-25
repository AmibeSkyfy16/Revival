import ch.skyfy.revival.config.Condition
import ch.skyfy.revival.config.NearConfig
import ch.skyfy.revival.config.Operator

import kotlin.test.Test

class Tests {

    @Test
    fun test() {

        val nearConfig1: NearConfig = NearConfig(
            "block.minecraft.blue_ice2",
            5,
            3,
            listOf(
                Condition(
                    Operator.OR,
                    NearConfig(
                        "block.minecraft.packet_ice2",
                        5,
                        4,
                        listOf(
                            Condition(
                            Operator.AND,
                            NearConfig(
                                "blue_ice",
                                5,
                                4,
                                listOf()
                            )
                        )
                    )
                )
//                Condition(
//                    Operator.AND,
//                    NearConfig(
//                        "block.minecraft.ice",
//                        5,
//                        4,
//                        listOf()
//                    )
                )
            )
        )

        println(isAllowed(nearConfig1)) // Must be false

    }

    private fun isAllowed2(blockTranslationKey: String, radius: Int, numberOfBlocks: Int): Boolean {
        // TODO check if there are a greater or equals number of block in the specified radius
        if (blockTranslationKey.contains("blue_ice")) {
            return true
        }
        if (blockTranslationKey.contains("block.minecraft.ice")) {
            return true
        }
        return false
    }

    private fun isAllowed(nearConfig: NearConfig, rootResult: Boolean = false): Boolean {
        var rootResult2 = isAllowed2(nearConfig.name, nearConfig.radius, nearConfig.numberOfBlock)

//        if (nearConfig.childs.any { it.operator == Operator.AND }) {

        nearConfig.childs.filter { it.operator == Operator.AND }.forEach {
//            rootResult2 = rootResult2 && isAllowed2(it.nearConfig.name, it.nearConfig.radius, it.nearConfig.numberOfBlock)
            rootResult2 = rootResult2 && isAllowed(it.nearConfig, rootResult2)
        }
        nearConfig.childs.filter { it.operator == Operator.OR }.forEach {
//            rootResult2 = rootResult2 || isAllowed2(it.nearConfig.name, it.nearConfig.radius, it.nearConfig.numberOfBlock)
            rootResult2 = rootResult2 || isAllowed(it.nearConfig,rootResult2)
        }


//        if (nearConfig.childs.filter { it.operator == Operator.AND }.any { isAllowed(it.nearConfig, rootResult2) }) {
//            rootResult2 = rootResult && true
//        } else {
//            rootResult2 = false
//        }
//
//        if (nearConfig.childs.filter { it.operator == Operator.OR }.any { isAllowed(it.nearConfig, rootResult2) }) {
//            rootResult2 = true
//        } else {
//            rootResult2 = rootResult || false
//        }


        return rootResult2
    }

}