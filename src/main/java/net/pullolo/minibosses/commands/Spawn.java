package net.pullolo.minibosses.commands;

import net.pullolo.minibosses.entities.BossRarity;
import net.pullolo.minibosses.entities.MiniBoss;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Spawn implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("spawn")){
            return false;
        }
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length>0){
            try {
                BossRarity rarity = BossRarity.COMMON;
                Entity e = player.getWorld().spawnEntity(player.getLocation(), EntityType.valueOf(args[0].toUpperCase()));
                if (args.length>1){
                    rarity = BossRarity.valueOf(args[1].toUpperCase());
                }
                new MiniBoss((LivingEntity) e, rarity);
            } catch (Exception e){
                sender.sendMessage(ChatColor.RED + "Something went wrong!");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("spawn")){
            return null;
        }
        if (args.length==1){
            List<String> completion = new ArrayList<>();
            for (EntityType e : EntityType.values()){
                addToCompletion(e.toString().toLowerCase(), args[0], completion);
            }
            return completion;
        }
        if (args.length==2){
            List<String> completion = new ArrayList<>();
            for (BossRarity b : BossRarity.values()){
                addToCompletion(b.toString().toLowerCase(), args[1], completion);
            }
            return completion;
        }
        return null;
    }

    private void addToCompletion(String arg, String userInput, List<String> completion){
        if (arg.regionMatches(true, 0, userInput, 0, userInput.length()) || userInput.length() == 0){
            completion.add(arg);
        }
    }
}
