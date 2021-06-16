package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

// todo don't use deprecated code
public class ServerMenu implements Listener {
    private static OutlinePane backgroundPane = null;
    private static ChestGui gui = null;
    private static final Map<String, ChestGui> serverGroupGui = new HashMap<>();
    private static long lastUpdated;

    public static final ItemStack openMenuItem;  // Item to open the navigation

    static {
        lastUpdated = System.currentTimeMillis();

        openMenuItem = new ItemStack(Material.COMPASS);
        ItemMeta navigatorMeta = openMenuItem.getItemMeta();
        TextComponent itemDisplayName = Component.text(
                "Serverauswahl",
                TextColor.color(100, 255, 0))
                .decoration(TextDecoration.ITALIC, false);
        navigatorMeta.addItemFlags(ItemFlag.values());  // hide all item flags
        navigatorMeta.displayName(itemDisplayName);
        navigatorMeta.setUnbreakable(true);
        openMenuItem.setItemMeta(navigatorMeta);
    }

    public static void display(Player player){
        if (gui == null || backgroundPane == null) update();
        // Update every 10 seconds if needed
        if (lastUpdated < (System.currentTimeMillis() - 10000)) update();
        gui.show(player);
    }

    public static void update() {
        // Set update time to now so the next update would happen minimum 10 seconds in future
        lastUpdated = System.currentTimeMillis();

        if (backgroundPane == null) {
            ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(Component.empty());
            itemStack.setItemMeta(itemMeta);
            GuiItem backgroundItem = new GuiItem(itemStack);
            backgroundPane = new OutlinePane(0, 0, 9, 6, Pane.Priority.LOWEST);
            backgroundPane.addItem(backgroundItem);
            backgroundPane.setRepeat(true);
        }

        gui = new ChestGui(6, "Servermenü");
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        StaticPane headerPane = new StaticPane(4, 0, 1, 1, Pane.Priority.HIGHEST);
        OutlinePane mainPane = new OutlinePane(1, 2, 7, 3, Pane.Priority.MONITOR);
        mainPane.setGap(1);
        gui.addPane(backgroundPane);
        gui.addPane(headerPane);
        gui.addPane(mainPane);

        // ---- get main menu data ---------
        MenuData mainMenuData = ProxyApi.getMenuMain();
        sortEntries(mainMenuData);

        // ==== Add header item ==============================================
        MenuData.Entry headerEntry = mainMenuData.headerEntry;
        // set material and amount
        ItemStack headerItem = new ItemStack(
                Material.valueOf(headerEntry.itemMaterial),
                headerEntry.itemAmount);
        ItemMeta headerMeta = headerItem.getItemMeta();
        headerMeta.addItemFlags(ItemFlag.values());
        // set display name and item lore
        headerMeta.displayName(MiniMessage.get().parse(headerEntry.displayName)
                .decoration(TextDecoration.ITALIC, false));
        headerMeta.lore(headerEntry.lore.stream().map(str -> MiniMessage.get().parse(str)
                .decoration(TextDecoration.ITALIC, false)).collect(Collectors.toList()));
        headerItem.setItemMeta(headerMeta);
        headerPane.addItem(new GuiItem(headerItem), 0, 0);
        // ====================================================================

        for (Map.Entry<String, MenuData.Entry> groupDataEntry : mainMenuData.entries.entrySet()) {
            String groupName = groupDataEntry.getKey();
            MenuData.Entry groupEntry = groupDataEntry.getValue();
            // for each group in main menu
            // ==== Add group item ==============================================
            // set material and amount
            ItemStack groupItem = new ItemStack(
                    Material.valueOf(groupEntry.itemMaterial),
                    groupEntry.itemAmount);
            ItemMeta itemMeta = groupItem.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.values());
            // set display name
            itemMeta.displayName(MiniMessage.get().parse(groupEntry.displayName)
                .decoration(TextDecoration.ITALIC, false));
            // set item lore
            itemMeta.lore(groupEntry.lore.stream().map(str -> MiniMessage.get().parse(str)
                .decoration(TextDecoration.ITALIC, false)).collect(Collectors.toList()));
            // add glowing to current server group
            if (ServermanagerPaper.config.getMainGroup().equals(groupName))
                itemMeta.addEnchant(Enchantment.DIG_SPEED, 1, true);
            groupItem.setItemMeta(itemMeta);
            mainPane.addItem(new GuiItem(groupItem, event -> {
                switch (event.getClick()){
                    case LEFT:  // left or right click
                    case RIGHT:  // send to one server of this group
                        ProxyApi.postSendPlayerGroup(groupName, event.getWhoClicked().getName());
                        break;
                    case SHIFT_LEFT:  // shift left click
                    case SHIFT_RIGHT:  // open list of all servers in this group
                        serverGroupGui.get(groupName).show(event.getWhoClicked());
                }
            }));
            // ====================================================================

            // ---- Get group menu data ------------
            MenuData groupMenuData = ProxyApi.getMenuGroup(groupName);
            sortEntries(groupMenuData);
            // ---- Build group GUI ----------------
            ChestGui groupServersGui = new ChestGui(6, "Servermenü -> "+groupName);
            groupServersGui.setOnGlobalClick(event -> event.setCancelled(true));
            StaticPane groupHeaderPane = new StaticPane(4, 0, 1, 1, Pane.Priority.HIGHEST);
            OutlinePane groupMainPane = new OutlinePane(1, 2, 7, 3, Pane.Priority.MONITOR);
            groupMainPane.setGap(1);
            groupServersGui.addPane(backgroundPane);
            groupServersGui.addPane(groupMainPane);
            groupServersGui.addPane(groupHeaderPane);
            serverGroupGui.put(groupName, groupServersGui);

            // ==== Add header item ==============================================
            MenuData.Entry groupHeaderEntry = groupMenuData.headerEntry;
            // set material and amount
            ItemStack groupHeaderItem = new ItemStack(
                    Material.valueOf(groupHeaderEntry.itemMaterial),
                    groupHeaderEntry.itemAmount);
            ItemMeta groupHeaderMeta = groupHeaderItem.getItemMeta();
            groupHeaderMeta.addItemFlags(ItemFlag.values());
            // set display name and item lore
            groupHeaderMeta.displayName(MiniMessage.get().parse(groupHeaderEntry.displayName)
                .decoration(TextDecoration.ITALIC, false));
            groupHeaderMeta.lore(groupHeaderEntry.lore.stream().map(str -> MiniMessage.get().parse(str)
                .decoration(TextDecoration.ITALIC, false)).collect(Collectors.toList()));
            groupHeaderItem.setItemMeta(groupHeaderMeta);
            groupHeaderPane.addItem(new GuiItem(groupHeaderItem, event -> {
                switch (event.getClick()){
                    case LEFT:  // left or right click
                    case RIGHT:  // show the main GUI
                        gui.show(event.getWhoClicked());
                }
            }), 0, 0);
            // ====================================================================

            for (Map.Entry<String, MenuData.Entry> serverDataEntry : groupMenuData.entries.entrySet()) {
                final String serverName = serverDataEntry.getKey();
                final MenuData.Entry serverEntry = serverDataEntry.getValue();

                // for each server in group menu
                // ==== Add server item ==============================================
                // set material and amount
                ItemStack serverItem = new ItemStack(
                        Material.valueOf(serverEntry.itemMaterial),
                        serverEntry.itemAmount);
                ItemMeta serverItemMeta = serverItem.getItemMeta();
                serverItemMeta.addItemFlags(ItemFlag.values());
                // set display name
                serverItemMeta.displayName(MiniMessage.get().parse(serverEntry.displayName)
                .decoration(TextDecoration.ITALIC, false));
                // set item lore
                serverItemMeta.lore(serverEntry.lore.stream().map(str -> MiniMessage.get().parse(str)
                .decoration(TextDecoration.ITALIC, false)).collect(Collectors.toList()));
                // add glowing to current server
                if (ServermanagerPaper.config.getName().equals(serverName))
                    serverItemMeta.addEnchant(Enchantment.DIG_SPEED, 1, true);
                serverItem.setItemMeta(serverItemMeta);
                groupMainPane.addItem(new GuiItem(serverItem, event -> {
                    switch (event.getClick()){
                        case LEFT:  // left or right click
                        case RIGHT:  // send to this server
                            ProxyApi.postSendPlayerServer(serverName, event.getWhoClicked().getName());
                    }
                }));
                // ====================================================================
            }
        }
    }

    public static void sortEntries(MenuData menuData) {
        List<Map.Entry<String, MenuData.Entry>> entryList = new LinkedList<>(menuData.entries.entrySet());
        entryList.sort(Comparator.comparing(l -> l.getValue().priority));

        menuData.entries.clear();  // clear and fill in sorted order
        for (Map.Entry<String, MenuData.Entry> newEntry : entryList) {
            menuData.entries.put(newEntry.getKey(), newEntry.getValue());
        }
    }

    // ==== Event listener ============================================================
    @EventHandler
    public static void onInteract(PlayerInteractEvent event) {
        if (!openMenuItem.equals(event.getItem())) return;
        // If item is the server menu opener, open the menu
        display(event.getPlayer());
        event.setCancelled(true);
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getInventory().contains(openMenuItem)) return;
        event.getPlayer().getInventory().addItem(openMenuItem);
    }

    @EventHandler
    public static void onPlayerDropItem(PlayerDropItemEvent event) {
        if (openMenuItem.equals(event.getItemDrop().getItemStack()))
            event.setCancelled(true);
    }
}
