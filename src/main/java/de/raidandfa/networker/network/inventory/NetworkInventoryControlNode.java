package de.raidandfa.networker.network.inventory;

import de.raidandfa.networker.network.BlockEntityRegistry;
import de.raidandfa.networker.network.NetworkClientNode;
import de.raidandfa.networker.network.utils.AddressUtils;
import de.raidandfa.networker.packet.NetworkPacket;
import de.raidandfa.networker.packet.data.NetworkInventoryItemRequest;
import de.raidandfa.networker.packet.data.NetworkInventoryItemStackData;
import de.raidandfa.networker.packet.data.NetworkInventoryListingResponse;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.HashMap;
import java.util.Map;

public class NetworkInventoryControlNode extends NetworkClientNode {

    Map<Item,Integer> cur_map;

    public NetworkInventoryControlNode(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntityRegistry.NETWORK_INVENTORY_CONTROL_BENTITY.get(), p_155229_, p_155230_);
        this.address = AddressUtils.addressFromBlockPos(p_155229_, (byte) 0x10);
        this.cur_map = new HashMap<>();
    }

    @Override
    protected void saveAdditional(CompoundTag p_187471_) {
        super.saveAdditional(p_187471_);
    }

    @Override
    public void load(CompoundTag p_155245_) {
        super.load(p_155245_);
    }

    @Override
    protected void onPacketReceived(ServerLevel sl, int port, NetworkPacket in, boolean isUnicast) {
        if(in.data() instanceof NetworkInventoryListingResponse nlr){
            System.out.println(in);
        }else if(isUnicast && in.data() instanceof NetworkInventoryItemStackData nir) {
            sl.addFreshEntity(
                new ItemEntity(
                    sl,
                    this.getBlockPos().getX(),this.getBlockPos().getY(),this.getBlockPos().getZ(),
                    nir.itemStack(),
                    0,0,0
                )
            );
        }else{
            super.onPacketReceived(sl, port, in, isUnicast);
        }
    }

    @Override
    public InteractionResult useBlock(BlockState p60503, Level p60504, BlockPos p60505, Player p60506, InteractionHand p60507, BlockHitResult p60508) {
        System.out.println(p60508);
        System.out.println(p60507);
        System.out.println(p60506);

        this.queuePacketToSend(0,
                new NetworkPacket(this.address, AddressUtils.getMulticastAddress((byte) 0x11),
                        new NetworkInventoryItemRequest(Items.REDSTONE,5)));
        return InteractionResult.SUCCESS;
    }
}
