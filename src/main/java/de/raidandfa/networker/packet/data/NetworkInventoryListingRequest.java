package de.raidandfa.networker.packet.data;

public record NetworkInventoryListingRequest() implements INetworkPacketData {

    @Override
    public int getColor() {
        return 0x00ffff;
    }
}
