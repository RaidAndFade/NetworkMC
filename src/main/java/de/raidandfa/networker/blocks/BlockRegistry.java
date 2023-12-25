package de.raidandfa.networker.blocks;

import de.raidandfa.networker.NetworkerMod;
import de.raidandfa.networker.network.NetworkClientNode;
import de.raidandfa.networker.network.NetworkHubNode;
import de.raidandfa.networker.network.NetworkRelayNode;
import de.raidandfa.networker.network.inventory.NetworkInventoryControlNode;
import de.raidandfa.networker.network.inventory.NetworkInventoryInterfaceNode;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {

    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NetworkerMod.MODID);
    public static final DeferredRegister<Item> ITEMBLOCKS = DeferredRegister.create(ForgeRegistries.ITEMS, NetworkerMod.MODID);

    public static final VoxelShape CLIENT_SHAPE = Block.box(2,0,2,14,13,12);
    public static final RegistryObject<Block> CLIENT_BLOCK = BLOCKS.register("network_client_block", () -> new NetworkNodeBlock<>(NetworkClientNode.class,CLIENT_SHAPE));

    public static final VoxelShape INVENTORY_INTERFACE_SHAPE = Block.box(6,6,6,10,10,10);
    public static final RegistryObject<Block> INVENTORY_INTERFACE_BLOCK = BLOCKS.register("network_inventory_interface_block", () -> new NetworkNodeBlock<>(NetworkInventoryInterfaceNode.class,INVENTORY_INTERFACE_SHAPE));
    public static final RegistryObject<Block> INVENTORY_CONTROL_BLOCK = BLOCKS.register("network_inventory_control_block", () -> new NetworkNodeBlock<>(NetworkInventoryControlNode.class));
    public static final VoxelShape RELAY_SHAPE = Block.box(4,0,4,11,3,12);
    public static final RegistryObject<Block> RELAY_BLOCK = BLOCKS.register("network_relay_block", () -> new NetworkNodeBlock<>(NetworkRelayNode.class,RELAY_SHAPE));

    public static final RegistryObject<Block> HUB_BLOCK = BLOCKS.register("network_hub_block", () -> new NetworkNodeBlock<>(NetworkHubNode.class,RELAY_SHAPE));

    static {
        for(RegistryObject<Block> bro : BLOCKS.getEntries()){
            ITEMBLOCKS.register(bro.getId().getPath(),() -> new BlockItem(bro.get(), new Item.Properties()));
        }
    }
}
