package xyz.artuto.elevator;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class ElevatorUtil
{
    public static void doTeleport(Block nextElevator, Player player)
    {
        Location playerLocation = player.getLocation();
        Location location = new Location(playerLocation.getWorld(), nextElevator.getX() + 0.5,
                nextElevator.getY() + 1.5, nextElevator.getZ() + 0.5, playerLocation.getYaw(),
                playerLocation.getPitch());

        if(!(isSafe(nextElevator.getLocation())))
        {
            player.sendActionBar(Component.text("Couldn't teleport you because it's not safe!", RED));
            return;
        }

        player.teleport(location);
        player.playSound(location, Sound.BLOCK_PISTON_EXTEND, 100F, 0F);
    }

    public static int calculateSearchDistance(BlockFace blockFace, Location location)
    {
        int distance = -1;
        int y = location.getBlockY();
        World world = location.getWorld();

        switch(blockFace)
        {
            case DOWN:
            {
                if(y < world.getMinHeight())
                    return -1;

                distance = y + 1;
                break;
            }
            case UP:
            {
                if(y >= world.getMaxHeight())
                    return -1;

                distance = world.getMaxHeight() - y;
            }
        }

        return distance;
    }

    private static boolean isSafe(Location location)
    {
        for(int i = 1; i < 3; i++)
        {
            if(location.getBlock().getRelative(BlockFace.UP, i).getType().isCollidable())
                return false;
        }

        return true;
    }
}
