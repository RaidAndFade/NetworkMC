package de.raidandfa.networker.packet.data;

public record NetworkPongData() implements INetworkPacketData {

    @Override
    public int getColor() {
        return 0x0000ff;
    }
}
