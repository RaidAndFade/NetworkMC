package de.raidandfa.networker.packet.data;

import net.minecraft.world.item.Item;

public record NetworkInventoryItemRequest(Item item, int count) implements INetworkPacketData {

    @Override
    public int getColor() {
        return 0x80ff00;
    }
}
