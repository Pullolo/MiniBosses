package net.pullolo.minibosses.entities;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static net.pullolo.minibosses.MiniBosses.*;

public class MiniBoss {
    public static final HashMap<LivingEntity, MiniBoss> miniBosses = new HashMap<>();
    private final LivingEntity entity;
    private BossRarity rarity = null;

    private BukkitRunnable ticker;

    public MiniBoss(LivingEntity entity, BossRarity rarity){
        this.entity = entity;
        this.rarity = rarity;
        setAttributesAndSpawn();
    }

    public MiniBoss(LivingEntity entity) {
        this.entity = entity;
        this.rarity = rollRarity(new Random());
        setAttributesAndSpawn();
    }

    private void setAttributesAndSpawn(){
        miniBosses.put(entity, this);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300, 0));
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()*getMultiplier(rarity)*1.6);
        entity.setHealth(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue((100+getMultiplier(rarity)*10)/500);
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue()*getMultiplier(rarity)*0.8);
        entity.getEquipment().setItemInMainHandDropChance(0);
        entity.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_AXE));
        notifyPlayersOnSpawn();
        createSpawnParticles();
        ticker = new BukkitRunnable() {
            short i = 0;
            int s = 1;
            @Override
            public void run() {
                if (!entity.isValid()){
                    cancel();
                    miniBosses.remove(entity);
                    entity.remove();
                }
                if (s%6==0 && i==0 && playersClose(10)){
                    explode();
                }

                particleApi.spawnParticles(entity.getLocation().clone().add(0, 3, 0), Particle.FLAME, 1, 0.1, 0.1, 0.1, 0);
                particleApi.spawnColoredParticles(entity.getLocation().clone().add(0, 0.2, 0), getRarityColors(rarity), 1, 40, 0.5, 0, 0.5);

                i++;
                if (i>20){
                    s++;
                    i=0;
                }
            }
        };
        ticker.runTaskTimer(miniBossPlugin, 0, 1);
    }

    public void removeTicker(){
        ticker.cancel();
        ticker=null;
    }

    private boolean playersClose(double distance){
        for (Entity e : entity.getNearbyEntities(distance, distance, distance)){
            if (e instanceof Player) return true;
        }
        return false;
    }

    private void explode(){
        particleApi.spawnParticles(entity.getLocation().clone(), Particle.EXPLOSION_NORMAL, 80, 1, 1, 1, 1);
        for (Entity e : entity.getNearbyEntities(5, 5, 5)){
            if (!(e instanceof Player)) continue;
            ((Player) e).playSound(e, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            ((Player) e).damage(2*getMultiplier(rarity), e);
            e.setVelocity(entity.getLocation().toVector().subtract(e.getLocation().toVector()).normalize().multiply(-1));
        }
    }

    private BossRarity rollRarity(Random r){
        int val = r.nextInt(100)+1;
        if (val>98) return BossRarity.LEGENDARY;
        else if (val>90) return BossRarity.EPIC;
        else if (val>80) return BossRarity.RARE;
        else if (val>65) return BossRarity.UNCOMMON;
        else return BossRarity.COMMON;
    }

    private Double getMultiplier(BossRarity rarity){
        switch (rarity){
            case LEGENDARY:
                return 8.0;
            case EPIC:
                return 6.8;
            case RARE:
                return 4.2;
            case UNCOMMON:
                return 3.9;
            default:
                return 2.2;
        }
    }

    public void notifyPlayersOnSpawn(){
        for (Player p : Bukkit.getOnlinePlayers()){
            if (p.getLocation().distance(entity.getLocation())<224){
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', getSpawnMessage()));
                p.playSound(p, Sound.ENTITY_WITHER_SPAWN, 1, 1.8f);
            }
        }
    }

    private void createSpawnParticles(){
        Location l1 = entity.getLocation().clone();
        Location l2 = entity.getLocation().clone();
        l1.setY(-64);
        l2.setY(320);
        particleApi.drawColoredLine(l1, l2, 0.1, getRarityColors(rarity), 10, 0);
    }

    private String getSpawnMessage(){
        String s = "&c☠ &fNew ";
        switch (rarity){
            case LEGENDARY:
                s+="&aL&3E&cG&6&eE&bN&dD&9A&6R&4Y";
                break;
            case EPIC:
                s+="&5EPIC";
                break;
            case RARE:
                s+="&9RARE";
                break;
            case UNCOMMON:
                s+="&aUNCOMMON";
                break;
            default:
                s+="&7COMMON";
                break;
        }
        s+=" &fMob has spawned nearby!";
        return s;
    }

    private Color[] getRarityColors(BossRarity rarity){
        Color[] colors;
        switch (rarity){
            case LEGENDARY:
                colors = new Color[5];
                colors[0]=Color.AQUA;
                colors[1]=Color.LIME;
                colors[2]=Color.PURPLE;
                colors[3]=Color.RED;
                colors[4]=Color.YELLOW;
                return colors;
            case EPIC:
                colors = new Color[1];
                colors[0]=Color.PURPLE;
                return colors;
            case RARE:
                colors = new Color[1];
                colors[0]=Color.BLUE;
                return colors;
            case UNCOMMON:
                colors = new Color[1];
                colors[0]=Color.LIME;
                return colors;
            default:
                colors = new Color[1];
                colors[0]=Color.GRAY;
                return colors;
        }
    }

    public BossRarity getRarity() {
        return rarity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void onDeath(List<ItemStack> drops) {
        removeTicker();
        if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent){
            if (((EntityDamageByEntityEvent) entity.getLastDamageCause()).getDamager() instanceof Player){
                Player p = (Player) ((EntityDamageByEntityEvent) entity.getLastDamageCause()).getDamager();
                p.giveExp(new Random().nextInt(30)+1);
            }
            setDrops(drops);
        }
    }

    private void setDrops(List<ItemStack> drops){
        Random r = new Random();
        switch (rarity){
            case LEGENDARY:
                switch (r.nextInt(6)+1){
                    case 1:
                        drops.add(new ItemStack(Material.NETHERITE_INGOT, r.nextInt(6)+1));
                        break;
                    case 2:
                        drops.add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 3));
                        break;
                    case 3:
                        ItemStack ds = new ItemStack(Material.NETHERITE_SWORD);
                        ds.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                        drops.add(ds);
                        break;
                    case 4:
                        ItemStack men = new ItemStack(Material.ENCHANTED_BOOK);
                        ItemMeta menMeta = men.getItemMeta();
                        menMeta.addEnchant(Enchantment.MENDING, 1, false);
                        men.setItemMeta(menMeta);
                        drops.add(men);
                        break;
                    case 5:
                        ItemStack db = new ItemStack(Material.DIAMOND_BOOTS);
                        db.addEnchantment(Enchantment.PROTECTION_FALL, 4);
                        drops.add(db);
                        break;
                    case 6:
                        drops.add(new ItemStack(Material.TOTEM_OF_UNDYING));
                        break;
                }
                break;
            case EPIC:
                switch (r.nextInt(4)+1){
                    case 1:
                        drops.add(new ItemStack(Material.DIAMOND_BLOCK, r.nextInt(4)+1));
                        break;
                    case 2:
                        drops.add(new ItemStack(Material.ENCHANTING_TABLE, 1));
                        break;
                    case 3:
                        drops.add(new ItemStack(Material.TOTEM_OF_UNDYING));
                        break;
                    case 4:
                        drops.add(new ItemStack(Material.NETHERITE_CHESTPLATE));
                        break;
                }
                break;
            case RARE:
                switch (r.nextInt(3)+1){
                    case 1:
                        drops.add(new ItemStack(Material.DIAMOND, r.nextInt(8)+3));
                        break;
                    case 2:
                        ItemStack ds = new ItemStack(Material.DIAMOND_SWORD);
                        ds.addEnchantment(Enchantment.DAMAGE_ALL, r.nextInt(4)+1);
                        drops.add(ds);
                        break;
                    case 3:
                        drops.add(new ItemStack(Material.EMERALD, r.nextInt(21)+10));
                        drops.add(new ItemStack(Material.GOLD_INGOT, 4));
                        break;
                }
                break;
            case UNCOMMON:
                switch (r.nextInt(2)+1){
                    case 1:
                        drops.add(new ItemStack(Material.IRON_INGOT, r.nextInt(14)+6));
                        break;
                    case 2:
                        drops.add(new ItemStack(Material.IRON_INGOT, r.nextInt(12)+3));
                        drops.add(new ItemStack(Material.GOLDEN_APPLE, 2));
                        break;
                }
                break;
            case COMMON:
                switch (r.nextInt(2)+1){
                    case 1:
                        drops.add(new ItemStack(Material.IRON_INGOT, r.nextInt(12)+4));
                        drops.add(new ItemStack(Material.COAL, r.nextInt(8)+12));
                        break;
                    case 2:
                        drops.add(new ItemStack(Material.GOLDEN_APPLE, r.nextInt(2)+2));
                        ItemStack dr = new ItemStack(Material.ENCHANTED_BOOK);
                        ItemMeta drMeta = dr.getItemMeta();
                        drMeta.addEnchant(Enchantment.DURABILITY, 2, false);
                        dr.setItemMeta(drMeta);
                        drops.add(dr);
                        break;
                }
                break;
        }
        if (r.nextBoolean()) drops.add(new ItemStack(Material.ARROW, 5));
        if (r.nextInt(4)==0) drops.add(new ItemStack(Material.EXPERIENCE_BOTTLE, 12));
        if (r.nextInt(12)==0) drops.add(new ItemStack(Material.PHANTOM_MEMBRANE, 2));
        if (r.nextInt(10)==0) drops.add(new ItemStack(Material.ENDER_PEARL, 3));
        if (r.nextInt(15)==0) drops.add(new ItemStack(Material.TRIDENT, 1));
        if (r.nextInt(6)==0) drops.add(new ItemStack(Material.EMERALD, 7));
        if (r.nextInt(20)==0) drops.add(new ItemStack(Material.NETHERITE_SCRAP, 1));
        return;
    }
}
