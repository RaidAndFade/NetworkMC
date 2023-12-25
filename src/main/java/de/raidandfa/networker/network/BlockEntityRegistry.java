package de.raidandfa.networker.network;

import de.raidandfa.networker.NetworkerMod;
import de.raidandfa.networker.blocks.BlockRegistry;
import de.raidandfa.networker.blocks.NetworkNodeBlock;
import de.raidandfa.networker.network.inventory.NetworkInventoryControlNode;
import de.raidandfa.networker.network.inventory.NetworkInventoryInterfaceNode;
import de.raidandfa.networker.renderers.NetworkNodeRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> NETWORK_NODE_BENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NetworkerMod.MODID);

    public static final RegistryObject<BlockEntityType<NetworkClientNode>> NETWORK_CLIENT_BENTITY = NETWORK_NODE_BENTITIES.register("network_client_bentity", () -> BlockEntityType.Builder.of(NetworkClientNode::new, BlockRegistry.CLIENT_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<NetworkInventoryInterfaceNode>> NETWORK_INVENTORY_INTERFACE_BENTITY = NETWORK_NODE_BENTITIES.register("network_inventory_interface_bentity", () -> BlockEntityType.Builder.of(NetworkInventoryInterfaceNode::new, BlockRegistry.INVENTORY_INTERFACE_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<NetworkInventoryControlNode>> NETWORK_INVENTORY_CONTROL_BENTITY = NETWORK_NODE_BENTITIES.register("network_inventory_control_bentity", () -> BlockEntityType.Builder.of(NetworkInventoryControlNode::new, BlockRegistry.INVENTORY_CONTROL_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<NetworkRelayNode>> NETWORK_RELAY_BENTITY = NETWORK_NODE_BENTITIES.register("network_relay_bentity", () -> BlockEntityType.Builder.of(NetworkRelayNode::new, BlockRegistry.RELAY_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<NetworkHubNode>> NETWORK_HUB_BENTITY = NETWORK_NODE_BENTITIES.register("network_hub_bentity", () -> BlockEntityType.Builder.of(NetworkHubNode::new, BlockRegistry.HUB_BLOCK.get()).build(null));

    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        for(RegistryObject<BlockEntityType<?>> robet : NETWORK_NODE_BENTITIES.getEntries()){
            event.registerBlockEntityRenderer((BlockEntityType<NetworkNode>)robet.get(), NetworkNodeRenderer::new);
        }
    }
}
