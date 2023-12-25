package de.raidandfa.networker.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.raidandfa.networker.link.NetworkLink;
import de.raidandfa.networker.network.NetworkNode;
import de.raidandfa.networker.packet.NetworkPacket;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.pipeline.VertexConsumerWrapper;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class NetworkNodeRenderer implements BlockEntityRenderer<NetworkNode> {

    private final BlockEntityRendererProvider.Context ctx;

    public NetworkNodeRenderer(BlockEntityRendererProvider.Context ctx) {
        this.ctx = ctx;
    }
    public void makeBlockVertexAtPos(float x, float y, float z, VertexConsumer vc){
        int color = 0xb36c3f;
        int light = 1;

        vc.vertex(
                x, y, z, // x y z
                0xb3/255f, 0x6c/255f, 0x3f/255f, 1f, // color (r,g,b) alpha
                0, 1, // texU, texV
                OverlayTexture.NO_OVERLAY, light, // overlay, lightcolor
                1, 0, 0); // normal
    }


    public void renderCable(Vec3 start_pos, Vec3 end_pos, VertexConsumer vc) {
        Vec3 radius = new Vec3(2,2,2);

//        vc.vertex(start_pos.x,start_pos.y,start_pos.z).color(255,255,255,1).normal(1,0,0).endVertex();
//        vc.vertex(end_pos.x,end_pos.y,end_pos.z).color(255,255,255,1).normal(1,0,0).endVertex();
//
        Vec3[] vertices = {start_pos.add(radius), end_pos.add(radius), end_pos.subtract(radius), start_pos.subtract(radius),};
//
        for(int i = 0; i < vertices.length; i++)
            makeBlockVertexAtPos((float) vertices[i].x, (float) vertices[i].y, (float) vertices[i].z, vc);
        for(int i = vertices.length-1; i >= 0; i--)
            makeBlockVertexAtPos((float) vertices[i].x, (float) vertices[i].y, (float) vertices[i].z, vc);
    }

    @Override
    public void render(NetworkNode networkNode, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        VertexConsumer vc_lines = multiBufferSource.getBuffer(RenderType.lines());

        for (int i = 0; i < networkNode.getPortCount(); i++) {
            NetworkLink nl = networkNode.port_connections[i];
            if(nl != null){
                Vec3 start_pos, end_pos;
                if(nl.left == networkNode) {
                    start_pos = nl.getStartPoint();
                    end_pos = nl.getEndPoint();
                }else {
                    end_pos = nl.getStartPoint();
                    start_pos = nl.getEndPoint();
                }
                poseStack.pushPose();
                Matrix4f lp = poseStack.last().pose();
                Matrix3f n = poseStack.last().normal();
                int color = 0x80ffff80;

                start_pos = start_pos.subtract(networkNode.getBlockPos().getCenter());
                end_pos = end_pos.subtract(networkNode.getBlockPos().getCenter());

                vc_lines.vertex(lp, (float) start_pos.x, (float) start_pos.y, (float) start_pos.z).color(color).normal(n,1f,0f,1f).endVertex();
                vc_lines.vertex(lp, (float) end_pos.x, (float) end_pos.y, (float) end_pos.z).color(color).normal(n,1f,0f,1f).endVertex();

//                    BeaconRenderer.renderBeaconBeam(poseStack,multiBufferSource,BeaconRenderer.BEAM_LOCATION,partialTicks,1f, networkNode.getLevel().getGameTime(),
//                            0, 1024, new float[]{1f, 1f, 1f}, 0.2f, 0.25f);

//                    vc.vertex(lp, (float) start_pos.x, (float) start_pos.y, (float) start_pos.z).color(0.25f,1f,0,1f).normal(n,1f,0f,0f).endVertex();
//                    vc.vertex(lp, (float) end_pos.x, (float) end_pos.y, (float) end_pos.z).color(0.25f,1f,0f,1f).normal(n,1f,0f,0f).endVertex();

//                    renderCable(networkNode.getBlockPos().getCenter(), otherNode.getBlockPos().getCenter(), vc);
                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(NetworkNode p_112306_) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return BlockEntityRenderer.super.getViewDistance();
    }

    @Override
    public boolean shouldRender(NetworkNode p_173568_, Vec3 p_173569_) {
        return Vec3.atCenterOf(p_173568_.getBlockPos()).closerThan(p_173569_, (double)this.getViewDistance());
    }
}

