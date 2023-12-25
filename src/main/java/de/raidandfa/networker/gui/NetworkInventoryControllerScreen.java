package de.raidandfa.networker.gui;

import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;

public class NetworkInventoryControllerScreen extends ContainerScreen {
    public NetworkInventoryControllerScreen(ChestMenu p_98409_, Inventory p_98410_, Component p_98411_) {
        super(p_98409_, p_98410_, p_98411_);
    }

    @Override
    public boolean mouseClicked(double p_97748_, double p_97749_, int p_97750_) {
        return super.mouseClicked(p_97748_, p_97749_, p_97750_);
    }
}
