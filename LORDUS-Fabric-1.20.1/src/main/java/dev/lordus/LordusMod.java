package dev.lordus;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Locale;
import java.util.Random;

public final class LordusMod implements ModInitializer {
    private static final Random RANDOM = new Random();

    @Override
    public void onInitialize() {
        registerCommands();
        registerBossDrops();
        registerImmortalityHelmet();
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("lordus")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("summon")
                    .then(Commands.argument("realm", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            builder.suggest("nether");
                            builder.suggest("end");
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String realm = StringArgumentType.getString(context, "realm")
                                .toLowerCase(Locale.ROOT);
                            if (!realm.equals("nether") && !realm.equals("end")) {
                                context.getSource().sendFailure(Component.literal(
                                    "Kullanim: /lordus summon nether veya /lordus summon end"));
                                return 0;
                            }

                            ServerLevel level = context.getSource().getLevel();
                            BlockPos pos = BlockPos.containing(context.getSource().getPosition());
                            WitherBoss boss = EntityType.WITHER.create(level);
                            if (boss == null) return 0;

                            boss.moveTo(pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 4.0,
                                level.random.nextFloat() * 360.0F, 0.0F);
                            String title = realm.equals("nether")
                                ? "§b§lLORDUS - Mavi Orman"
                                : "§5§lLORDUS - End";
                            boss.setCustomName(Component.literal(title));
                            boss.setCustomNameVisible(true);
                            boss.setPersistenceRequired();

                            var health = boss.getAttribute(Attributes.MAX_HEALTH);
                            if (health != null) health.setBaseValue(600.0);
                            boss.setHealth(600.0F);

                            var damage = boss.getAttribute(Attributes.ATTACK_DAMAGE);
                            if (damage != null) damage.setBaseValue(24.0);

                            boss.addTag(realm.equals("nether") ? "lordus_nether" : "lordus_end");
                            level.addFreshEntity(boss);

                            context.getSource().sendSuccess(
                                () -> Component.literal("LORDUS uyandi!"), true);
                            return 1;
                        })
                    )
                )
            )
        );
    }

    private void registerBossDrops() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof WitherBoss boss)) return;
            boolean isLordus = boss.getTags().contains("lordus_nether")
                || boss.getTags().contains("lordus_end");
            if (!isLordus || !(boss.level() instanceof ServerLevel level)) return;

            drop(level, boss, named(Items.NETHERITE_SWORD,
                "§bLordus'un Tek Atan Kilici"), 1);
            drop(level, boss, named(Items.TURTLE_HELMET,
                "§dLordus'un Olumsuzluk Migferi"), 1);
            drop(level, boss, named(Items.NETHERITE_AXE,
                "§6Lordus Baltasi"), 50);
        });
    }

    private static ItemStack named(Item item, String name) {
        ItemStack stack = new ItemStack(item);
        stack.setHoverName(Component.literal(name));
        return stack;
    }

    private static void drop(ServerLevel level, LivingEntity boss,
                             ItemStack stack, int chancePercent) {
        if (RANDOM.nextInt(100) >= chancePercent) return;
        ItemEntity item = new ItemEntity(level,
            boss.getX(), boss.getY() + 1.0, boss.getZ(), stack);
        item.setDefaultPickUpDelay();
        level.addFreshEntity(item);
    }

    private void registerImmortalityHelmet() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
            if (!helmet.is(Items.TURTLE_HELMET)) return true;

            String name = helmet.getHoverName().getString();
            if (!name.contains("Olumsuzluk Migferi")) return true;

            helmet.shrink(1);
            entity.setHealth(Math.max(1.0F, Math.min(entity.getMaxHealth(), 4.0F)));
            entity.removeAllEffects();
            return false;
        });
    }
}
