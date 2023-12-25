package de.raidandfa.networker.network;

import com.google.common.graph.Network;
import de.raidandfa.networker.NetworkerMod;
import de.raidandfa.networker.link.NetworkLink;
import de.raidandfa.networker.packet.NetworkPacket;
import de.raidandfa.networker.packet.data.NetworkPingData;
import de.raidandfa.networker.particles.NetworkPacketParticle;
import de.raidandfa.networker.particles.NetworkParticleType;
import de.raidandfa.networker.particles.ParticleRegistry;
import net.minecraft.client.particle.CritParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NetworkNode extends BlockEntity {

    public NetworkPacket[] port_egress;
    public NetworkPacket[] queued_egress;
    public NetworkLink[] port_connections;

    final static int TICKS_PER_NETTICK = 20;

    public NetworkNode(BlockEntityType<? extends NetworkNode> entity, BlockPos p_155229_, BlockState p_155230_) {
        super(entity, p_155229_, p_155230_);
        this.port_egress = new NetworkPacket[this.getPortCount()];
        this.queued_egress = new NetworkPacket[this.getPortCount()];
        this.port_connections = new NetworkLink[this.getPortCount()];
    }

    private void loadConnections(CompoundTag nbt){
        CompoundTag connectionTags = (CompoundTag) nbt.get("connections");

        if(connectionTags != null){
            for (int i = 0; i < getPortCount(); i++) {
                if((connectionTags.get("connection-"+i) instanceof CompoundTag cn_nbt))
                    port_connections[i] = NetworkLink.fromNBT(this.level,cn_nbt);
            }
        }
    }

    private void saveConnections(CompoundTag nbt){
        CompoundTag connectionTags = new CompoundTag();

        for (int i = 0; i < getPortCount(); i++) {
            if(port_connections[i] != null)
                connectionTags.put("connection-"+i,NetworkLink.toNBT(port_connections[i]));
        }

        nbt.put("connections",connectionTags);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        this.saveConnections(nbt);
        super.saveAdditional(nbt);
    }

    CompoundTag load_when_level_loads;

    @Override
    public void setLevel(Level p_155231_) {
        super.setLevel(p_155231_);
    }

    @Override
    public void load(CompoundTag nbt) {
        load_when_level_loads=nbt;
        super.load(nbt);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if(level==null) return;
        if(blockEntity instanceof  NetworkNode nn){
            if(nn.load_when_level_loads != null){
                if(!level.isClientSide) {
                    nn.loadConnections(nn.load_when_level_loads);
                    nn.load_when_level_loads = null;
                    level.sendBlockUpdated(nn.getBlockPos(), nn.getBlockState(), nn.getBlockState(), 2);
                }else{
                    nn.loadConnections(nn.load_when_level_loads);
                    nn.load_when_level_loads = null;
                }
            }
            if(!level.isClientSide)
                nn.networkTickOuter();
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag ct = new CompoundTag();
        this.saveConnections(ct);
        return ct;
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt){
        CompoundTag tag = pkt.getTag();
        assert tag != null;
        this.loadConnections(tag);

        System.out.println(level);
    }

    private void connectFromNodeToNode(NetworkNode nn, int my_index, int their_index){
        assert !Objects.requireNonNull(nn.getLevel()).isClientSide;
        assert level != null;

        NetworkLink nl = new NetworkLink();
        nl.left=this;nl.left_index=my_index;
        nl.right=nn;nl.right_index=their_index;
        this.port_connections[my_index]=nl;
        nn.port_connections[their_index]=nl;

        level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
    }

    public boolean connectTo(NetworkNode nn) {
        if(this.getBlockPos().distManhattan(nn.getBlockPos()) > 10) return false; // too far.

        int my_index, their_index;
        if((my_index=firstEmptyPort()) > -1){
            if((their_index=nn.firstEmptyPort()) > -1) {
                connectFromNodeToNode(nn, my_index, their_index);
                return true;
            }
        }
        return false;
    }

    public int firstEmptyPort() {
        for(int i=0;i<this.getPortCount();i++){
            if(this.port_connections[i]==null) return i;
        }
        return -1;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        for(int i=0;i<this.getPortCount();i++){
            if(this.port_connections[i]!=null){
                this.port_connections[i].delete();
            }
        }
    }

    private void networkTickOuter() {
        // this is server only.
        if (!(level instanceof ServerLevel sl)) return;

        // for now, draw particles every 4th tick to show packets:
        if(level.getGameTime()%4 != 0){
            for (int i = 0; i < this.getPortCount(); i++) {
                NetworkPacket np = queued_egress[i];
                NetworkLink nl = port_connections[i];
                if (np != null && nl != null) {
                    double cur_pos = (level.getGameTime() % 20) / 20d;
                    Vec3 start_pos = nl.left == this? nl.getStartPoint():nl.getEndPoint();
                    Vec3 end_pos = nl.left == this? nl.getEndPoint():nl.getStartPoint();

                    Vec3 packet_pos = start_pos.subtract(end_pos)
                            .multiply(cur_pos,cur_pos,cur_pos);

                    Vec3 cur_packet_pos = start_pos.subtract(packet_pos).subtract(0.5f,0.5f,0.5f);

                    sl.sendParticles(
                            new NetworkParticleType(np.getColor()),
                            cur_packet_pos.x,cur_packet_pos.y,cur_packet_pos.z,
                            3,
                            0,0,0,
                            0);
                }
            }
        }

        if(level.getGameTime()%TICKS_PER_NETTICK == (TICKS_PER_NETTICK-1)){
            port_egress = queued_egress;
            queued_egress = new NetworkPacket[this.getPortCount()];
        }

        if(level.getGameTime()%TICKS_PER_NETTICK != 0) return;

        networkTick();
    }

    protected long getCurNetworkTick(@NotNull Level l){
        return l.getGameTime() / TICKS_PER_NETTICK;
    }

    protected void networkTick(){
        for (int i = 0; i < this.getPortCount(); i++) {
            NetworkPacket np = port_egress[i];
            if(np != null){
                NetworkLink nl = port_connections[i];
                if(nl != null){
                    if(nl.left == this){
                        nl.right.onPacketInput(nl.right_index,np);
                    }else{
                        nl.left.onPacketInput(nl.left_index,np);
                    }
                    port_egress[i] = null;
                }else{
                    emitPacketLossParticles(np);
                }
            }
        }
    }

    public int getPortCount(){
        throw new NotImplementedException();
    }

    protected void emitPacketLossParticles(NetworkPacket np){
        if(level instanceof ServerLevel sl){
            np.destroyMe(this.level,this.getBlockPos().getCenter());
            sl.sendParticles(
                    ParticleTypes.CRIT,
                    this.getBlockPos().getX()+.5,this.getBlockPos().getY()+1,this.getBlockPos().getZ()+.5,
                    5,
                    0,0.2,0,
                    0);
        }
    }

    public void queuePacketToSend(int port, NetworkPacket out){
        assert port < this.getPortCount();
        assert out != null;

        if(this instanceof NetworkClientNode) {
            // fuck you i didnt want to override.
            out.ttl--;
            if (out.ttl < 0) {
                emitPacketLossParticles(out);
                return;
            }
        }

        if(this.queued_egress[port] != null){
            emitPacketLossParticles(this.queued_egress[port]);
        }
        this.queued_egress[port] = out;
    }
    protected void onPacketInput(int port, NetworkPacket in){
        // make a particle effect and delete the mf
        emitPacketLossParticles(in);
    }

    public InteractionResult useBlock(BlockState p60503, Level p60504, BlockPos p60505, Player p60506, InteractionHand p60507, BlockHitResult p60508) {
        return InteractionResult.PASS;
    }
}
