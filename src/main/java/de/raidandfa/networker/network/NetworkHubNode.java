package de.raidandfa.networker.network;

import de.raidandfa.networker.packet.NetworkPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class NetworkHubNode extends NetworkNode{

    boolean reversed = false;
    public NetworkHubNode(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntityRegistry.NETWORK_HUB_BENTITY.get(), p_155229_, p_155230_);
    }

    @Override
    public int getPortCount() {
        return 3;
    }

    @Override
    protected void onPacketInput(int port, NetworkPacket in) {
        int next_port = (reversed?new int[]{1,2,0}:new int[]{2,0,1})[port];
        queuePacketToSend(next_port,in);
    }

    @Override
    public InteractionResult useBlock(BlockState p60503, Level p60504, BlockPos p60505, Player p60506, InteractionHand p60507, BlockHitResult p60508) {
        reversed =! reversed;
        return super.useBlock(p60503, p60504, p60505, p60506, p60507, p60508);
    }
}
