package de.raidandfa.networker.packet.data;

public record NetworkPingData() implements INetworkPacketData {
    @Override
    public int getColor() {
        return 0x00ff00;
    }
}
