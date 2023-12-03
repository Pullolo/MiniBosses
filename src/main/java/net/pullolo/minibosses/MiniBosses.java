package net.pullolo.minibosses;

import net.pullolo.minibosses.commands.Spawn;
import net.pullolo.minibosses.events.EntityEvents;
import net.pullolo.minibosses.misc.ParticleApi;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Logger;

import static net.pullolo.minibosses.entities.MiniBoss.miniBosses;

public final class MiniBosses extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    public static ParticleApi particleApi;
    public static JavaPlugin miniBossPlugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        miniBossPlugin=this;
        particleApi = new ParticleApi();
        getServer().getPluginManager().registerEvents(new EntityEvents(), this);
        registerCommand(new Spawn(), "spawn");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (LivingEntity entity : new ArrayList<>(miniBosses.keySet())){
            entity.remove();
            miniBosses.remove(entity);
        }
    }

    public static Logger getLog(){
        return log;
    }

    private void registerCommand(CommandExecutor cmd, String cmdName){
        if (cmd instanceof TabCompleter){
            getCommand(cmdName).setExecutor(cmd);
            getCommand(cmdName).setTabCompleter((TabCompleter) cmd);
        } else {
            throw new RuntimeException("Provided object is not a command executor and a tab completer at the same time!");
        }
    }
}
