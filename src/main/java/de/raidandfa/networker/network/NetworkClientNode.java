package de.raidandfa.networker.network;

import com.google.common.graph.Network;
import de.raidandfa.networker.network.utils.AddressUtils;
import de.raidandfa.networker.packet.NetworkPacket;
import de.raidandfa.networker.packet.data.NetworkEmptyData;
import de.raidandfa.networker.packet.data.NetworkPingData;
import de.raidandfa.networker.packet.data.NetworkPongData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public class NetworkClientNode extends NetworkNode {
    protected byte[] address;
    public NetworkClientNode(BlockEntityType<? extends NetworkNode> entity, BlockPos p_155229_, BlockState p_155230_) {
        super(entity, p_155229_, p_155230_);
    }

    public NetworkClientNode(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntityRegistry.NETWORK_CLIENT_BENTITY.get(), p_155229_, p_155230_);
        this.address = AddressUtils.addressFromBlockPos(p_155229_, (byte) 0x01);
    }

    public String getAddressString(){
        return AddressUtils.addressToString(address);
    }

    @Override
    protected void saveAdditional(CompoundTag p_187471_) {
        p_187471_.putByteArray("Networker.NodeAddress", address);
        super.saveAdditional(p_187471_);
    }

    @Override
    public void load(CompoundTag p_155245_) {
        setAddress(p_155245_.getByteArray("Networker.NodeAddress"));
        super.load(p_155245_);
    }

    public void setAddress(byte[] address){
        if(address.length != 16) return;

        this.address=address;
        this.setChanged();
    }

    @Override
    protected void networkTick() {
        super.networkTick();
    }

    @Override
    public int getPortCount() {
        return 1;
    }

    /**
     * Called when this Client receives a packet to its address (or a multicast to its devid)
     * @param in
     */
    protected void onPacketReceived(ServerLevel sl, int port, NetworkPacket in, boolean isUnicast){
        System.out.printf("Packet received from: %s\n",AddressUtils.addressToString(in.source()));
        if(in.data() instanceof NetworkPingData){
            Vec3 blockcenter = getBlockPos().getCenter();
            sl.sendParticles(
                    ParticleTypes.BUBBLE_POP,
                    blockcenter.x,blockcenter.y+.5,blockcenter.z,
                    10,
                    0,0.1,0,
                    0);

            queuePacketToSend(port, new NetworkPacket(this.address,in.source(),new NetworkPongData()));
        }else{
            Vec3 blockcenter = getBlockPos().getCenter();
            sl.sendParticles(
                    ParticleTypes.BUBBLE_POP,
                    blockcenter.x,blockcenter.y+.5,blockcenter.z,
                    10,
                    0,0.1,0,
                    0);
        }
    }

    static boolean doesMulticastMatch(byte[] addr1, byte devid){
        return Arrays.equals(addr1, AddressUtils.getMulticastAddress(devid));
    }
    static boolean doesAddressMatch(byte[] addr1,byte[] addr2,boolean allowMulticast){
        if(allowMulticast && doesMulticastMatch(addr1,addr2[15])) return true;

        return Arrays.equals(addr1,addr2);
    }
    static boolean doesUnicastMatch(byte[] addr1,byte[] addr2){
        return doesAddressMatch(addr1,addr2,false);
    }
    @Override
    protected void onPacketInput(int port, NetworkPacket in) {
        if (!(level instanceof ServerLevel sl)) return;

        System.out.println("Received packet");
        if(doesMulticastMatch(in.dest(), this.address[15])){
            this.onPacketReceived(sl, port, in, false);
        }else if(doesUnicastMatch(in.dest(),this.address)){
            this.onPacketReceived(sl, port, in, true);
        }else{
            this.queuePacketToSend(port, in);
        }
    }

    public byte[] getAddress() {
        return this.address;
    }
}
