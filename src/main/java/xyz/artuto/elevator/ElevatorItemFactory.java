package xyz.artuto.elevator;

import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;

public class ElevatorItemFactory
{
    private final Elevator plugin;

    public ElevatorItemFactory(Elevator plugin)
    {
        this.plugin = plugin;
    }

    public void registerRecipe()
    {
        ItemStack itemStack = createElevatorItem();

        ShapedRecipe recipe = new ShapedRecipe(plugin.KEY, itemStack);
        recipe.shape("WWW", "WPW", "WWW");
        recipe.setIngredient('W', Material.WHITE_WOOL);
        recipe.setIngredient('P', Material.ENDER_PEARL);

        plugin.getServer().addRecipe(recipe);
    }

    public ItemStack createElevatorItem()
    {
        ItemStack itemStack = new ItemStack(Elevator.ELEVATOR_TYPE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(text("Elevator", Style.style(GOLD, ITALIC.as(FALSE))));
        itemMeta.getPersistentDataContainer().set(plugin.KEY, PersistentDataType.BYTE, (byte) 1);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
