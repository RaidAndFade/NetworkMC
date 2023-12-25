package de.raidandfa.networker.packet.data;

import net.minecraft.world.item.Item;

import java.util.Map;

public record NetworkInventoryListingResponse(Map<Item, Integer> inventoryContents) implements INetworkPacketData {

    @Override
    public int getColor() {
        return 0xff00ff;
    }
}
