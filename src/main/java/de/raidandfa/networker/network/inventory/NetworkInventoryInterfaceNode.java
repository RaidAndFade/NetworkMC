package de.raidandfa.networker.network.inventory;

import de.raidandfa.networker.network.BlockEntityRegistry;
import de.raidandfa.networker.network.NetworkClientNode;
import de.raidandfa.networker.network.utils.AddressUtils;
import de.raidandfa.networker.packet.NetworkPacket;
import de.raidandfa.networker.packet.data.NetworkInventoryItemRequest;
import de.raidandfa.networker.packet.data.NetworkInventoryItemStackData;
import de.raidandfa.networker.packet.data.NetworkInventoryListingRequest;
import de.raidandfa.networker.packet.data.NetworkInventoryListingResponse;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

// This class receives inventory queries and replies to them with the content of neighboring inventories
// (and potentially takes items out of those inventories)
public class NetworkInventoryInterfaceNode extends NetworkClientNode {
    public NetworkInventoryInterfaceNode(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntityRegistry.NETWORK_INVENTORY_INTERFACE_BENTITY.get(), p_155229_, p_155230_);
        this.address = AddressUtils.addressFromBlockPos(p_155229_, (byte) 0x11);
    }

    NetworkInventoryListingResponse createInventoryResponse(){
        assert level != null;
        NetworkInventoryListingResponse nir = new NetworkInventoryListingResponse(new HashMap<>());

        BlockPos[] neighbors = {
                this.worldPosition.offset(1, 0, 0),
                this.worldPosition.offset(-1, 0, 0),
                this.worldPosition.offset(0, 1, 0),
                this.worldPosition.offset(0, -1, 0),
                this.worldPosition.offset(0, 0, 1),
                this.worldPosition.offset(0, 0, -1)
        };

        for(BlockPos bp : neighbors){
            BlockEntity be = level.getBlockEntity(bp);
            if (be instanceof Container c) {
                for (int i = 0; i < c.getContainerSize(); i++) {
                    ItemStack is = c.getItem(i);
                    if(is.getCount() > 0 && c.canTakeItem(c,i,is)){
                        nir.inventoryContents().compute(is.getItem(), (it, e) -> (e == null ? 0 : e) + is.getCount());
                    }
                }
            }
        }

        return nir;
    }

    NetworkPacket fulfilItemRequest(NetworkPacket np){
        if(!(np.data() instanceof NetworkInventoryItemRequest niq)) return np;
        ItemStack resp = new ItemStack(niq.item(),niq.count());
        System.out.print("Request received ");
        System.out.println(niq);

        BlockPos[] neighbors = {
                this.worldPosition.offset(1, 0, 0),
                this.worldPosition.offset(-1, 0, 0),
                this.worldPosition.offset(0, 1, 0),
                this.worldPosition.offset(0, -1, 0),
                this.worldPosition.offset(0, 0, 1),
                this.worldPosition.offset(0, 0, -1)
        };

        for(BlockPos bp : neighbors) {
            BlockEntity be = level.getBlockEntity(bp);
            if (be instanceof Container c) {
                for (int i = 0; i < c.getContainerSize(); i++) {
                    ItemStack is = c.getItem(i);
                    if(is.getItem().equals(niq.item()) && is.getCount()>=niq.count()){
                        return new NetworkPacket(this.address, np.source(), new NetworkInventoryItemStackData(c.removeItem(i,resp.getCount())));
                    }
                }
            }
        }
        return np;
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
    protected void onPacketReceived(ServerLevel sl, int port, NetworkPacket in, boolean isUnicast){
        if(in.data() instanceof NetworkInventoryListingRequest nlr) {
            this.queuePacketToSend(port, new NetworkPacket(this.address, in.source(), this.createInventoryResponse()));
        }else if(in.data() instanceof NetworkInventoryItemRequest niq){
            this.queuePacketToSend(port, this.fulfilItemRequest(in));
        }else{
            super.onPacketReceived(sl, port, in, isUnicast);
        }
    }

}
