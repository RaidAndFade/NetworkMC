package de.raidandfa.networker.particles;

import de.raidandfa.networker.NetworkerMod;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = NetworkerMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLETYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, NetworkerMod.MODID);

    public static final RegistryObject<ParticleType<NetworkParticleType>> NETWORK_PACKET_PARTICLE_TYPE = PARTICLETYPES.register(
            "network_packet_particle", NetworkParticleType::new);

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event)
    {
        event.registerSpriteSet(NETWORK_PACKET_PARTICLE_TYPE.get(), NetworkPacketParticle.NetworkPacketParticleProvider::new);
    }
}
