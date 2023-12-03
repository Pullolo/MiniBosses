package net.pullolo.minibosses.misc;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ParticleApi {

    public ArrayList<Entity> drawColoredLine(Location l1, Location l2, double precision, Color[] colors, float size, double lineOffset){
        Random r = new Random();
        ArrayList<Entity> lineThrough = new ArrayList<>();
        Location startPos = l1.clone();
        Location loc = l1.clone();
        double step = precision/0.1;
        Vector dir = l2.toVector().subtract(l1.toVector()).normalize().multiply((double) 1/step);
        loc.add(dir.clone().normalize().multiply(lineOffset));
        for (int i = 0; i<startPos.distance(l2)*step; i++){
            for (Entity e : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)){
                if (!lineThrough.contains(e)){
                    lineThrough.add(e);
                }
            }
            loc.add(dir);
            spawnColoredParticles(loc, colors[r.nextInt(colors.length)], size, 1, 0, 0, 0);
        }
        return lineThrough;
    }


    public void spawnParticles(Location loc, Particle particle, int amount, double offsetX, double offsetY, double offsetZ, double speed){
        loc.getWorld().spawnParticle(particle, loc, amount, offsetX, offsetY, offsetZ, speed);
    }

    public void spawnColoredParticles(Location loc, Color color, float size, int amount, double offsetX, double offsetY, double offsetZ){
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, size);
        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, amount, offsetX, offsetY, offsetZ, 1, dustOptions, true);
    }

    public void spawnColoredParticles(Location loc, Color[] colors, float size, int amount, double offsetX, double offsetY, double offsetZ){
        Random r = new Random();
        Particle.DustOptions dustOptions = new Particle.DustOptions(colors[r.nextInt(colors.length)], size);
        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, amount, offsetX, offsetY, offsetZ, 1, dustOptions);
    }
}
