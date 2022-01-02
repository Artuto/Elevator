package xyz.artuto.elevator;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static org.bukkit.persistence.PersistentDataType.BYTE;

public final class Elevator extends JavaPlugin
{
    public final NamespacedKey KEY = Objects.requireNonNull(NamespacedKey.fromString("elevator", this));

    private ElevatorItemFactory itemFactory;

    @Override
    public void onEnable()
    {
        this.itemFactory = new ElevatorItemFactory(this);

        itemFactory.registerRecipe();
        getServer().getPluginManager().registerEvents(new ElevatorListener(this), this);
    }

    @Nullable
    public Block findNextElevator(Block below, BlockFace blockFace)
    {
        int distance = ElevatorUtil.calculateSearchDistance(blockFace, below.getLocation());
        if(distance == -1)
            return null;

        for(int i = 1; i < distance; i++)
        {
            Block nextBlock = below.getRelative(blockFace, i);
            if(nextBlock.getType().isAir())
                continue;
            if(nextBlock.getType() == ELEVATOR_TYPE && isElevator(nextBlock))
                return nextBlock;
        }

        return null;
    }

    @Nullable
    public Block getBlockBelow(Player player, boolean isSneaking, boolean shouldBeSneaking)
    {
        if(shouldBeSneaking)
        {
            if(!isSneaking)
                return null;
        }

        Block below = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if(below.getType() != ELEVATOR_TYPE)
            return null;
        if(!isElevator(below))
            return null;

        return below;
    }

    public boolean isElevator(Block block)
    {
        if(block.getState() instanceof PersistentDataHolder holder)
            return isElevator(holder);

        return false;
    }

    public boolean isElevator(PersistentDataHolder holder)
    {
        return holder.getPersistentDataContainer().getOrDefault(KEY, BYTE, (byte) 0) == (byte) 1;
    }

    public ElevatorItemFactory getItemFactory()
    {
        return itemFactory;
    }

    public static final Material ELEVATOR_TYPE = Material.WHITE_SHULKER_BOX;
}
