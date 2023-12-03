package net.pullolo.minibosses.events;

import net.pullolo.minibosses.entities.MiniBoss;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Random;

import static net.pullolo.minibosses.entities.MiniBoss.miniBosses;

public class EntityEvents implements Listener {
    @EventHandler
    public void onMobSpawn(EntitySpawnEvent event){
        Random r = new Random();
        if (event.getEntity() instanceof Monster){
            if (r.nextInt(240)==0) new MiniBoss((LivingEntity) event.getEntity());
        }
    }

    @EventHandler
    public void chunkUnload(ChunkUnloadEvent event){
        for (Entity e : event.getChunk().getEntities()){
            if (!(e instanceof LivingEntity)) continue;
            if (miniBosses.containsKey(e)){
                miniBosses.get(e).removeTicker();
                miniBosses.remove(e);
                e.remove();
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if (miniBosses.containsKey(event.getEntity())){
            miniBosses.get(event.getEntity()).onDeath(event.getDrops());
            miniBosses.remove(event.getEntity());
        }

    }
}
