package de.raidandfa.networker.packet.data;

public record NetworkEmptyData() implements INetworkPacketData {
    @Override
    public int getColor() {
        return 0;
    }
}
