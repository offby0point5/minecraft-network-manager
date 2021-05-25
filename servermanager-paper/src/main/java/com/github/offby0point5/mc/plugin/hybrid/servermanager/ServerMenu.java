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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerMenu {  // todo update the menuData every X seconds
    private static OutlinePane backgroundPane = null;
    private static ChestGui gui = null;
    private static final Map<String, ChestGui> serverGroupGui = new HashMap<>();

    public static final ItemStack openMenuItem;  // Item to open the navigation

    static {
        openMenuItem = new ItemStack(Material.COMPASS);
        ItemMeta navigatorMeta = openMenuItem.getItemMeta();
        // todo make all custom item meta non italics and use TextColor for coloring
        TextComponent itemDisplayName = Component.text(
                "Serverauswahl",
                TextColor.color(100, 255, 0));
        itemDisplayName = itemDisplayName.decoration(TextDecoration.ITALIC, false);
        System.out.println(itemDisplayName);  // todo remove
        System.out.println(itemDisplayName.color()); // todo remove
        navigatorMeta.addItemFlags(ItemFlag.values());  // hide all item flags
        navigatorMeta.displayName(itemDisplayName);
        navigatorMeta.setUnbreakable(true);
        openMenuItem.setItemMeta(navigatorMeta);
    }

    public static void display(Player player){
        if (gui == null || backgroundPane == null) ServerMenu.update();
        gui.show(player);
    }

    public static void update() {
        if (backgroundPane == null) {
            ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(Component.empty());
            itemStack.setItemMeta(itemMeta);
            GuiItem backgroundItem = new GuiItem(itemStack);
            backgroundPane = new OutlinePane(0, 0, 9, 7, Pane.Priority.LOWEST);
            backgroundPane.addItem(backgroundItem);
            backgroundPane.setRepeat(true);
        }

        gui = new ChestGui(7, "Servermenü");
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        StaticPane headerPane = new StaticPane(0, 0, 1, 1);
        OutlinePane mainPane = new OutlinePane(1, 3, 7, 3);
        mainPane.setGap(1);
        gui.addPane(backgroundPane);
        gui.addPane(headerPane);
        gui.addPane(mainPane);

        // ---- get main menu data ---------
        MenuData mainMenuData = ProxyApi.getMenuMain();

        // ==== Add header item ==============================================
        MenuData.Entry headerEntry = mainMenuData.headerEntry;
        // set material and amount
        ItemStack headerItem = new ItemStack(
                Material.valueOf(headerEntry.itemMaterial),
                headerEntry.itemAmount);
        ItemMeta headerMeta = headerItem.getItemMeta();
        headerMeta.addItemFlags(ItemFlag.values());
        // set display name and item lore
        headerMeta.displayName(MiniMessage.get().parse(headerEntry.displayName));
        headerMeta.lore(headerEntry.lore.stream().map(str -> MiniMessage.get().parse(str)).collect(Collectors.toList()));
        headerPane.addItem(new GuiItem(headerItem), 1, 1);
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
            itemMeta.displayName(MiniMessage.get().parse(groupEntry.displayName));
            // set item lore
            itemMeta.lore(groupEntry.lore.stream().map(str -> MiniMessage.get().parse(str)).collect(Collectors.toList()));
            // todo add glowing to current server group
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
            // ---- Build group GUI ----------------
            ChestGui groupServersGui = new ChestGui(7, "Servermenü -> "+groupName);
            groupServersGui.setOnGlobalClick(event -> event.setCancelled(true));
            StaticPane groupHeaderPane = new StaticPane(0, 0, 1, 1);
            OutlinePane groupMainPane = new OutlinePane(1, 3, 7, 3);
            groupMainPane.setGap(1);
            groupServersGui.addPane(backgroundPane);
            groupServersGui.addPane(groupHeaderPane);
            groupServersGui.addPane(groupMainPane);
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
            groupHeaderMeta.displayName(MiniMessage.get().parse(groupHeaderEntry.displayName));
            groupHeaderMeta.lore(groupHeaderEntry.lore.stream().map(str -> MiniMessage.get().parse(str)).collect(Collectors.toList()));
            groupHeaderPane.addItem(new GuiItem(groupHeaderItem), 1, 1);
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
                serverItemMeta.displayName(MiniMessage.get().parse(serverEntry.displayName));
                // set item lore
                serverItemMeta.lore(serverEntry.lore.stream().map(str -> MiniMessage.get().parse(str)).collect(Collectors.toList()));
                // todo add glowing to current server
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
}
