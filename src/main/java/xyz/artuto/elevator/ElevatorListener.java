package xyz.artuto.elevator;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataHolder;

import static org.bukkit.persistence.PersistentDataType.BYTE;
import static xyz.artuto.elevator.Elevator.ELEVATOR_TYPE;

public class ElevatorListener implements Listener
{
    private final Elevator plugin;

    public ElevatorListener(Elevator plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();
        if(block.getType() != ELEVATOR_TYPE)
            return;

        if(!plugin.isElevator(block))
            return;

        event.setDropItems(false);

        if(event.getPlayer().getGameMode() != GameMode.CREATIVE)
            block.getWorld().dropItemNaturally(block.getLocation(), plugin.getItemFactory().createElevatorItem());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Block block = event.getBlock();
        BlockState state = block.getState();
        if(block.getType() != ELEVATOR_TYPE)
            return;
        if(!plugin.isElevator(event.getItemInHand().getItemMeta()))
            return;
        if(state instanceof PersistentDataHolder holder)
        {
            holder.getPersistentDataContainer().set(plugin.KEY, BYTE, (byte) 1);
            state.update();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onJump(PlayerJumpEvent event)
    {
        Player player = event.getPlayer();
        Block below = plugin.getBlockBelow(player, player.isSneaking(), false);
        if(below == null)
            return;

        Block nextElevator = plugin.findNextElevator(below, BlockFace.UP);
        if(nextElevator == null)
            return;

        ElevatorUtil.doTeleport(nextElevator, player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onOpen(PlayerInteractEvent event)
    {
        Block clicked = event.getClickedBlock();
        if(clicked == null || event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if(clicked.getType() != ELEVATOR_TYPE)
            return;

        if(plugin.isElevator(clicked))
        {
            Player player = event.getPlayer();
            player.getWorld().playEffect(clicked.getLocation(), Effect.SMOKE, BlockFace.UP);
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event)
    {
        if(!event.isSneaking())
            return;

        Player player = event.getPlayer();
        Block below = plugin.getBlockBelow(player, true, true);
        if(below == null)
            return;

        Block nextElevator = plugin.findNextElevator(below, BlockFace.DOWN);
        if(nextElevator == null)
            return;

        ElevatorUtil.doTeleport(nextElevator, player);
    }
}
