package de.raidandfa.networker.packet;

import de.raidandfa.networker.packet.data.INetworkPacketData;
import de.raidandfa.networker.particles.NetworkParticleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NetworkPacket {

    public byte[] source() {
        return source;
    }

    public byte[] dest() {
        return dest;
    }

    public INetworkPacketData data() {
        return data;
    }

    final byte[] source;
    final byte[] dest;
    final INetworkPacketData data;
    public int ttl;

    public NetworkPacket(byte[] source, byte[] dest, INetworkPacketData data, int ttl){
        this.source = source;this.dest = dest; this.data = data; this.ttl = ttl;
    }

    public NetworkPacket(byte[] source, byte[] dest, INetworkPacketData data){
        this(source.clone(),dest.clone(),data,3);
    }

    public int getColor() {
        return data.getColor();
    }

    public void destroyMe(Level l, Vec3 pos) { data.destroyMe(l,pos); }
}
