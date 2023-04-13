package ch.skyfy.revival.javaimpl;

import ch.skyfy.json5configlib.ConfigManager;
import ch.skyfy.revival.callback.EntityDamageCallback;
import ch.skyfy.revival.callback.EntityMoveCallback;
import ch.skyfy.revival.command.ReloadFilesCmd;
import ch.skyfy.revival.config.Configs;
import ch.skyfy.revival.persistent.Persistents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

class RevivalMod implements ModInitializer {

    public static String MOD_ID = "revival";
    public static Path CONFIG_DIRECTORY = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    public static Path PERSISTENTS_DIRECTORY = CONFIG_DIRECTORY.resolve("persistents");
    public static Logger LOGGER = LogManager.getLogger(RevivalMod.class);

    public RevivalMod() {
        ConfigManager.INSTANCE.loadConfigs(new Class[]{Configs.class});
    }

    @Override
    public void onInitialize() {
        registerCommands();
        registerEvents();
    }

    private void registerCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ReloadFilesCmd.Companion.register(dispatcher);
        });
    }

    private void registerEvents(){
        EntityDamageCallback.EVENT.register(this::onEntityDamage);
        EntityMoveCallback.EVENT.register(this::onEntityMoved);
        ServerPlayerEvents.AFTER_RESPAWN.register(this::afterRespawn);
    }

    private void onEntityDamage(LivingEntity livingEntity, DamageSource damageSource, float v) {
        if(!(livingEntity instanceof ServerPlayerEntity))return;

        if(livingEntity.getHealth() <= 0){
            livingEntity.getServer().sendMessage(Text.literal(""));
//            Persistents.DEATH_PLAYERS.up
        }
    }

    private ActionResult onEntityMoved(Entity entity, MovementType movementType, Vec3d vec3d) {
        return ActionResult.PASS;
    }

    private void afterRespawn(ServerPlayerEntity serverPlayerEntity, ServerPlayerEntity serverPlayerEntity1, boolean b) {

    }


}