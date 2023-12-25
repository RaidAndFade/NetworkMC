package de.raidandfa.networker.network;

import de.raidandfa.networker.packet.NetworkPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class NetworkRelayNode extends NetworkNode{
    public NetworkRelayNode(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntityRegistry.NETWORK_RELAY_BENTITY.get(), p_155229_, p_155230_);
    }

    @Override
    public int getPortCount() {
        return 2;
    }

    @Override
    protected void onPacketInput(int port, NetworkPacket in) {
        if(port == 0){
            queuePacketToSend(1,in);
        }else{
            queuePacketToSend(0,in);
        }
    }
}
