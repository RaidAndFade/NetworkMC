package de.raidandfa.networker.link;

import de.raidandfa.networker.network.NetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class NetworkLink {
    public NetworkNode left;
    public NetworkNode right;
    public int left_index;
    public int right_index;

    public static CompoundTag toNBT(NetworkLink nl) {
        CompoundTag nbt = new CompoundTag();

        ListTag left_node_data = new ListTag();
        left_node_data.add(IntTag.valueOf(nl.left.getBlockPos().getX()));
        left_node_data.add(IntTag.valueOf(nl.left.getBlockPos().getY()));
        left_node_data.add(IntTag.valueOf(nl.left.getBlockPos().getZ()));
        left_node_data.add(IntTag.valueOf(nl.left_index));

        ListTag right_node_data = new ListTag();
        right_node_data.add(IntTag.valueOf(nl.right.getBlockPos().getX()));
        right_node_data.add(IntTag.valueOf(nl.right.getBlockPos().getY()));
        right_node_data.add(IntTag.valueOf(nl.right.getBlockPos().getZ()));
        right_node_data.add(IntTag.valueOf(nl.right_index));

        nbt.put("left_node_data", left_node_data);
        nbt.put("right_node_data", right_node_data);
        return nbt;
    }

    public static BlockPos getBlockPosFromList(ListTag lt) {
        return new BlockPos(lt.getInt(0), lt.getInt(1), lt.getInt(2));
    }

    public static NetworkLink getExistingNetworkLinkAtBlockPosIndex(Level l, BlockPos p, int i) {
        if ((l.getBlockEntity(p) instanceof NetworkNode nn)) {
            try {
                return nn.port_connections[i];
            }catch(Exception e){}
        }
        return null;
    }

    public static NetworkLink fromNBT(Level l, CompoundTag nbt) {
        if (!(nbt.get("left_node_data") instanceof ListTag left_node_data))
            return null;

        if (!(nbt.get("right_node_data") instanceof ListTag right_node_data))
            return null;

        int left_index = left_node_data.getInt(3), right_index = right_node_data.getInt(3);

        // find the existing ones, just in case.
        BlockPos left_blockpos = getBlockPosFromList(left_node_data);
        NetworkLink left_nl = getExistingNetworkLinkAtBlockPosIndex(l, left_blockpos, left_index);

        if (left_nl != null) return left_nl;

        BlockPos right_blockpos = getBlockPosFromList(right_node_data);
        NetworkLink right_nl = getExistingNetworkLinkAtBlockPosIndex(l, right_blockpos, right_index);

        if (right_nl != null) return right_nl;

        // both were null, we make one now and assign to both.
        if (l.getBlockEntity(left_blockpos) instanceof NetworkNode left_nn && left_nn.getPortCount() > left_index) {
            if (l.getBlockEntity(right_blockpos) instanceof NetworkNode right_nn && right_nn.getPortCount() > right_index) {
                NetworkLink new_nl = new NetworkLink();
                new_nl.left = left_nn;
                new_nl.right = right_nn;
                new_nl.left_index = left_index;
                new_nl.right_index = right_index;
                left_nn.port_connections[new_nl.left_index] = new_nl;
                right_nn.port_connections[new_nl.right_index] = new_nl;
                return new_nl;
            }
        }
        return null;
    }

    public void delete() {
        // get rid of connection data.
        try{left.port_connections[left_index] = null;}catch(Exception e){}
        try{right.port_connections[right_index] = null;}catch(Exception e){}
        left = null;
        right = null;
    }

    public Vec3 getStartPoint(){
        return this.left.getBlockPos().getCenter().add(this.left.getBlockState().getShape(this.left.getLevel(),this.left.getBlockPos()).bounds().getCenter());
    }

    public Vec3 getEndPoint(){
        return this.right.getBlockPos().getCenter().add(this.right.getBlockState().getShape(this.right.getLevel(),this.right.getBlockPos()).bounds().getCenter());
    }
}
