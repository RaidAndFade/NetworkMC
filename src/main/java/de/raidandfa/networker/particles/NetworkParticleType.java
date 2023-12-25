package de.raidandfa.networker.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.RedStoneWireBlock;
import org.joml.Vector3f;

import java.util.stream.Stream;

public class NetworkParticleType extends ParticleType<NetworkParticleType> implements ParticleOptions {
    public int color;
    private static final ParticleOptions.Deserializer<NetworkParticleType> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        public NetworkParticleType fromCommand(ParticleType<NetworkParticleType> p_123846_, StringReader p_123847_) {
            return (NetworkParticleType)p_123846_;
        }

        public NetworkParticleType fromNetwork(ParticleType<NetworkParticleType> p_123849_, FriendlyByteBuf p_123850_) {
            NetworkParticleType npt = (NetworkParticleType)p_123849_;
            npt.color = p_123850_.readInt();
            return npt;
        }
    };

    public NetworkParticleType(int color) {
        super(false, DESERIALIZER);
        this.color = color;
    }
    public NetworkParticleType() {
        super(false, DESERIALIZER);
    }

    @Override
    public ParticleType<NetworkParticleType> getType() {

        return ParticleRegistry.NETWORK_PACKET_PARTICLE_TYPE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(this.color);
    }

    @Override
    public String writeToString() {
        return null;
    }

    @Override
    public Codec<NetworkParticleType> codec() {
        return RecordCodecBuilder.create(
                (p_253370_) -> p_253370_.group(
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("color").forGetter((p_253371_) -> p_253371_.color))
                .apply(p_253370_, NetworkParticleType::new)
        );
    }
}
