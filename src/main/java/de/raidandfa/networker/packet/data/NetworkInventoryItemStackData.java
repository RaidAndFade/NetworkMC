package de.raidandfa.networker.packet.data;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record NetworkInventoryItemStackData(ItemStack itemStack) implements INetworkPacketData {

    @Override
    public int getColor() {
        return 0x808000;
    }

    @Override
    public void destroyMe(Level l, Vec3 pos) {
        l.addFreshEntity(new ItemEntity(l,pos.x,pos.y,pos.z,this.itemStack));
    }
}
