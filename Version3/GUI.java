import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Connor Linfoot's GUI Library
 *
 * @version 3.0
 */

public class GUI {
    public static HashMap<UUID, GUI> guis = new HashMap<>();
    public static HashMap<UUID, Integer> pageTracker = new HashMap<>();
    public static ArrayList<UUID> ignoreClosing = new ArrayList<>();

    private String title;
    private ArrayList<ItemStack> items = new ArrayList<>();
    private HashMap<Integer, ClickHandler> clickHandlers = new HashMap<>();
    private Integer currentItem = 0;
    private ClickHandler goBackHandler = null;
    private boolean hidePages = false;

    public GUI(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<ItemStack> getItems() {
        return items;
    }

    public void addItem(ItemStack itemStack) {
        addItem(itemStack, null);
    }

    public void addItem(ItemStack itemStack, ClickHandler clickHandler) {
        items.add(itemStack);
        clickHandlers.put(currentItem, clickHandler);
        currentItem++;
    }

    private Integer getPlayerPage(Player player) {
        if (pageTracker.containsKey(player.getUniqueId()))
            return pageTracker.get(player.getUniqueId());
        return 1;
    }

    public void handleClick(Player player, Integer slot) {
        if (slot == 48 && getPlayerPage(player) <= 1 && getGoBackHandler() != null) {
            getGoBackHandler().doClick(player, this);
        } else if (slot == 48 && getPlayerPage(player) > 1) {
            GUI.ignoreClosing.add(player.getUniqueId());
            open(player, getPlayerPage(player) - 1);
        } else if (slot == 50 ) {
            GUI.ignoreClosing.add(player.getUniqueId());
            open(player, getPlayerPage(player) + 1);
        } else {
            Integer item = calculateItemFromSlot(player, slot);
            if (clickHandlers.containsKey(item) && clickHandlers.get(item) != null) {
                ClickHandler clickHandler = clickHandlers.get(item);
                clickHandler.doClick(player, this);
            }
        }
    }

    private Integer calculateItemFromSlot(Player player, Integer slot) {
        int actualI = 0;
        Integer page = getPlayerPage(player);

        if (page > 1) {
            actualI = 21 * (page - 1);
        }

        // We start at slot 10
        for (int i = 10; i < 54; i++) {
            if (i == 17 || i == 18 || i == 26 || i == 27 || i >= 35)
                continue;
            if (i == slot) {
                // GOT IT
                return actualI;
            }
            actualI++;
        }
        return null;
    }

    public boolean isHidePages() {
        return hidePages;
    }

    public void setHidePages(boolean hidePages) {
        this.hidePages = hidePages;
    }

    public ClickHandler getGoBackHandler() {
        return goBackHandler;
    }

    public void setGoBackHandler(ClickHandler goBackHandler) {
        this.goBackHandler = goBackHandler;
    }

    private Inventory create(Integer page) {
        if (page < 1) page = 1;
        int itemsPerPage = 21;
        int pages = 1;
        if (getItems().size() > itemsPerPage)
            pages = (int) Math.ceil(getItems().size() / itemsPerPage) + 1;
        int startFrom = ((page - 1) * itemsPerPage);

        int size = 45;
        if (getGoBackHandler() != null || page > 1 || pages > page)
            size = size + 9;

        String title = getTitle();
        if (!isHidePages()) {
            title += " (Page " + page + "/" + pages + ")";
        }
        Inventory inventory = Bukkit.createInventory(null, size, title);

        int ii = 10;
        for (int i = startFrom; i < startFrom + itemsPerPage; i++) {
            if (ii == 17)
                ii = 19;
            if (ii == 26)
                ii = 28;
            if (i + 1 > getItems().size())
                break;
            inventory.setItem(ii, getItems().get(i));
            ii++;
        }

        if (page == 1 && getGoBackHandler() != null) {
            ItemStack back = new ItemStack(Material.ARROW);
            ItemMeta backMeta = back.getItemMeta();
            backMeta.setDisplayName(ChatColor.YELLOW + "Go Back");
            back.setItemMeta(backMeta);
            inventory.setItem(48, back);
        } else if (page > 1) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
            prev.setItemMeta(prevMeta);
            inventory.setItem(48, prev);
        }

        if (pages > page) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
            prev.setItemMeta(prevMeta);
            inventory.setItem(50, prev);
        }

        return inventory;
    }

    public void open(Player player) {
        open(player, 1);
    }

    public void open(Player player, Integer page) {
        guis.put(player.getUniqueId(), this);
        pageTracker.put(player.getUniqueId(), page);
        player.openInventory(create(page));
    }

    public abstract static class ClickHandler {
        public abstract void doClick(Player player, GUI gui);
    }

}