package de.raidandfa.networker.packet.data;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface INetworkPacketData {

    int getColor();

    default void destroyMe(Level l, Vec3 pos){}
}
