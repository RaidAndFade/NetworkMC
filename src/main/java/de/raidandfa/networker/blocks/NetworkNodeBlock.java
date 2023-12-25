package de.raidandfa.networker.blocks;

import com.google.common.graph.Network;
import de.raidandfa.networker.network.NetworkClientNode;
import de.raidandfa.networker.network.BlockEntityRegistry;
import de.raidandfa.networker.network.NetworkNode;
import de.raidandfa.networker.network.NetworkRelayNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public class NetworkNodeBlock<Q extends NetworkNode> extends Block implements EntityBlock {

    Class<Q> clazz;
    VoxelShape shape;
    public NetworkNodeBlock(Class<Q> clazz, VoxelShape shape) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
        this.clazz = clazz;
        this.shape = shape;
    }

    public NetworkNodeBlock(Class<Q> clazz) {
        this(clazz, Shapes.block());
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return shape;
    }

    @Override
    public void onRemove(BlockState p_60515_, Level p_60516_, BlockPos p_60517_, BlockState p_60518_, boolean p_60519_) {
        super.onRemove(p_60515_, p_60516_, p_60517_, p_60518_, p_60519_);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public Q newBlockEntity(BlockPos blockPos, BlockState blockState) {
        try {
            return clazz.getConstructor(BlockPos.class, BlockState.class).newInstance(blockPos,blockState);
        }catch(Exception ignored){}
        return null;
    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {

        if(p_60504_.getBlockEntity(p_60505_) instanceof NetworkNode nn){
            return nn.useBlock(p_60503_,p_60504_,p_60505_,p_60506_,p_60507_,p_60508_);
        }
        return super.use(p_60503_, p_60504_, p_60505_, p_60506_, p_60507_, p_60508_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        for(RegistryObject<BlockEntityType<?>> ro : BlockEntityRegistry.NETWORK_NODE_BENTITIES.getEntries()){
            if(type == ro.get()) return NetworkNode::tick;
        }
        return null;
    }
}
