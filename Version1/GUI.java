
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * You can use the static "guis" variable to create a listener for the clicks.
 **/

public class GUI {
    public static HashMap<String, GUI> guis = new HashMap<>();
    private String title;
    private ArrayList<ItemStack> items = new ArrayList<>();
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

    public void addItem(ItemStack... itemStack) {
        Collections.addAll(items, itemStack);
    }

    public boolean isHidePages() {
        return hidePages;
    }

    public void setHidePages(boolean hidePages) {
        this.hidePages = hidePages;
    }

    public Inventory create(int page) {
        return create(page, false);
    }

    public Inventory create(int page, boolean showGoBack) {
        if (page < 1) page = 1;
        int itemsPerPage = 21;
        int pages = 1;
        if (getItems().size() > itemsPerPage)
            pages = (int) Math.ceil(getItems().size() / itemsPerPage) + 1;
        int startFrom = ((page - 1) * itemsPerPage);

        int size = 45;
        if (showGoBack || page > 1 || pages > page)
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

        if (page == 1 && showGoBack) {
            ItemStack back = new ItemStack(Material.ARROW);
            ItemMeta backMeta = back.getItemMeta();
            backMeta.setDisplayName(ChatColor.YELLOW + "Go Back");
            back.setItemMeta(backMeta);
            inventory.setItem(48, back);
        } else if (page > 1 && !isHidePages()) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
            prev.setItemMeta(prevMeta);
            inventory.setItem(48, prev);
        }

        if (pages > page && !isHidePages()) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
            prev.setItemMeta(prevMeta);
            inventory.setItem(50, prev);
        }

        return inventory;
    }
}