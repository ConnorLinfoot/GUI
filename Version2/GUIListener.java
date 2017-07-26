
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (GUI.guis.containsKey(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
                return;
            GUI gui = GUI.guis.get(event.getWhoClicked().getUniqueId());
            String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()) != null ? ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()) : "";
            Integer currentPage = GUI.pageTracker.containsKey(event.getWhoClicked().getUniqueId()) ? GUI.pageTracker.get(event.getWhoClicked().getUniqueId()) : 0;
            if (itemName.contains("Next Page")) {
                GUI.ignoreClosing.add(event.getWhoClicked().getUniqueId());
                gui.open((Player) event.getWhoClicked(), currentPage + 1);
            } else if (itemName.contains("Previous Page")) {
                GUI.ignoreClosing.add(event.getWhoClicked().getUniqueId());
                gui.open((Player) event.getWhoClicked(), currentPage - 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (GUI.ignoreClosing.contains(event.getPlayer().getUniqueId())) {
            GUI.ignoreClosing.remove(event.getPlayer().getUniqueId());
            return;
        }
        if (GUI.guis.containsKey(event.getPlayer().getUniqueId()))
            GUI.guis.remove(event.getPlayer().getUniqueId());
        if (GUI.pageTracker.containsKey(event.getPlayer().getUniqueId()))
            GUI.pageTracker.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (GUI.ignoreClosing.contains(event.getPlayer().getUniqueId()))
            GUI.ignoreClosing.remove(event.getPlayer().getUniqueId());
        if (GUI.guis.containsKey(event.getPlayer().getUniqueId()))
            GUI.guis.remove(event.getPlayer().getUniqueId());
        if (GUI.pageTracker.containsKey(event.getPlayer().getUniqueId()))
            GUI.pageTracker.remove(event.getPlayer().getUniqueId());
    }

}
