package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import com.guilib.buttons.ActionButton;
import com.guilib.buttons.CloseButton;
import com.guilib.gui.GUI;
import com.guilib.gui.GUIItem;
import com.guilib.gui.GUIManager;
import com.guilib.gui.GuiLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class ServerMenu {
    private final ItemStack navItem;

    protected ServerMenu(EventNode<Event> rootEventNode) {
        try {
            GuiLib.init();
        } catch (IllegalStateException ignore) { }
        // create navItem
        navItem = ItemStack.builder(Material.COMPASS)
                .displayName(Component.text("Serverchooser", NamedTextColor.GREEN))
                .build();
        // create main gui
        GUI mainMenu = new GUI(InventoryType.CHEST_6_ROW, "Server navigator",
                ItemStack.of(Material.BLACK_STAINED_GLASS_PANE), "menu-main");
        GUIManager.registerGUI(mainMenu);
        mainMenu.addButton(getSlot(9, 6), new CloseButton());

        EventNode<Event> eventNode = EventNode.all("server-menu-inventory");
        rootEventNode.addChild(eventNode);
        eventNode.addListener(PlayerSpawnEvent.class, event -> {
            // add navigator item to hotbar
            event.getPlayer().getInventory().setItemStack(5, navItem);
        });
        eventNode.addListener(ItemDropEvent.class, event -> {
            if (event.getItemStack().equals(navItem)) event.setCancelled(true);
        });
        eventNode.addListener(PlayerUseItemEvent.class, event -> {
            playerInteractEvent(event.getPlayer(), event.getItemStack());
            event.setCancelled(true);
        });
        eventNode.addListener(PlayerUseItemOnBlockEvent.class, event -> {
            playerInteractEvent(event.getPlayer(), event.getItemStack());
            // todo why is this not cancellable?
        });

        update();
    }

    private void playerInteractEvent(Player player, ItemStack itemStack) {
        player.sendMessage(itemStack.getDisplayName()); // todo remove
        if (navItem.equals(itemStack)) {
            // todo open server menu
            GUIManager.getGuiById("menu-main").open(player);
        }
    }

    public void update() {
        GUI mainGui = GUIManager.getGuiById("menu-main");
        mainGui.addButton(getSlot(1, 0), new ActionButton(GUIItem.itemWithName(Material.WHITE_CONCRETE,
                Component.text("Hello World!\nLore line 1\nLore line 2")), event -> {
            event.getPlayer().sendMessage(Component.text("You clicked"));  // todo implement
        }));
    }

    private static int getSlot(int x, int y) {
        return (y*9)+x;
    }
}
