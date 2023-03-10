package io.github.cunnydevelopment.cunnyaddon;

import com.mojang.logging.LogUtils;
import io.github.cunnydevelopment.cunnyaddon.hud.CoordsPlus;
import io.github.cunnydevelopment.cunnyaddon.hud.GelbooruScroller;
import io.github.cunnydevelopment.cunnyaddon.modules.chat.PlaceHolder;
import io.github.cunnydevelopment.cunnyaddon.modules.chat.Spam;
import io.github.cunnydevelopment.cunnyaddon.modules.combat.Surround;
import io.github.cunnydevelopment.cunnyaddon.modules.misc.CunnyPresence;
import io.github.cunnydevelopment.cunnyaddon.modules.misc.Global;
import io.github.cunnydevelopment.cunnyaddon.modules.misc.PacketPlace;
import io.github.cunnydevelopment.cunnyaddon.modules.render.NametagsPlus;
import io.github.cunnydevelopment.cunnyaddon.utility.*;
import io.github.cunnydevelopment.cunnyaddon.utility.blocks.BlockHandling;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.external.ModuleReference;
import io.github.cunnydevelopment.cunnyaddon.utility.modules.internal.CompatibilityConfig;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class Cunny extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static Cunny INSTANCE;

    @Override
    public void onInitialize() {
        INSTANCE = this;
        // Using nanoseconds instead of milliseconds because milliseconds could be negative.
        long started = System.nanoTime();

        FileSystem.mkdir(FileSystem.COMPATIBILITY_PATH);

        // Various initialization methods, removing could break features or crash the Add-On;
        StringUtils.init();
        ModuleReference.load();
        SpecialEffects.load();

        // Modules
        // Chat
        new PlaceHolder();
        new Spam();
        // Combat
        new Surround();
        // Misc
        new CunnyPresence();
        new PacketPlace();
        new Global();
        // Render
        new NametagsPlus();
        // Modules end here.

        // Events are automatically added once the instance is initialized.
        new CompatibilityConfig();
        new ModuleReference();
        new BlockHandling();
        new MidTickHandler();

        // HUDs are automatically added once the instance is initialized.
        new CoordsPlus();
        new GelbooruScroller();

        LOG.info("Cunny Addon took {}ns to load", System.nanoTime() - started);
    }


    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(Categories.COMBAT);
        Modules.registerCategory(Categories.MOVEMENT);
        Modules.registerCategory(Categories.MISC);
        Modules.registerCategory(Categories.CHAT);
        Modules.registerCategory(Categories.RENDER);
        Modules.registerCategory(Categories.EXPLOITS);
        Modules.registerCategory(Categories.UNKNOWN);
    }


    @Override
    public String getPackage() {
        return "io.github.cunnydevelopment.cunnyaddon";
    }
}
