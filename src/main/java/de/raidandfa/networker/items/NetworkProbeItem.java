package de.raidandfa.networker.items;

import de.raidandfa.networker.network.NetworkClientNode;
import de.raidandfa.networker.network.NetworkNode;
import de.raidandfa.networker.network.inventory.NetworkInventoryControlNode;
import de.raidandfa.networker.network.utils.AddressUtils;
import de.raidandfa.networker.packet.NetworkPacket;
import de.raidandfa.networker.packet.data.NetworkInventoryItemRequest;
import de.raidandfa.networker.packet.data.NetworkInventoryListingRequest;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NetworkProbeItem extends Item {
    public NetworkProbeItem() {
        super(new Item.Properties());
    }


    public void getNodeInfo(NetworkNode nn, Player p){
        StringBuilder sb = new StringBuilder();
        sb.append("Node: "); sb.append(nn.getClass().getSimpleName());
        if(nn instanceof NetworkClientNode ncn){
            sb.append("\nNode Address:"); sb.append(ncn.getAddressString());
        }
        sb.append("\nNode ports: ");
        for(int i=0;i<nn.getPortCount();i++){
            sb.append("\n - ");sb.append(i);sb.append(" : ");
            if(nn.port_connections[i] != null){
                NetworkNode leftnode = nn.port_connections[i].left;
                sb.append(leftnode == nn? "Me":leftnode);
                sb.append(" [");sb.append(nn.port_connections[i].left_index);
                sb.append("] <-> [");
                sb.append(nn.port_connections[i].right_index);sb.append("] ");
                NetworkNode rightnode = nn.port_connections[i].right;
                sb.append(rightnode==nn? "Me":rightnode);
            }else{
                sb.append("Empty");
            }
        }

        PlayerChatMessage chatMessage = PlayerChatMessage.system(sb.toString());
        p.createCommandSourceStack().sendSystemMessage(chatMessage.decoratedContent());
    }

    NetworkNode last_click;

    public void connectNode(NetworkNode nn, Player p){
        if((last_click == null || last_click == nn) && nn.firstEmptyPort()>-1){
            last_click = nn;return;
        }

        if(last_click != null && last_click.connectTo(nn)){
            last_click = null;
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext p_41427_) {
        if(p_41427_.getLevel().isClientSide) return InteractionResult.PASS;

        BlockEntity block_at_pos = p_41427_.getLevel().getBlockEntity(p_41427_.getClickedPos());

        if(block_at_pos instanceof NetworkNode nn){
            Player p = p_41427_.getPlayer();
            if(p != null){
                if(p.isCrouching()){
                    this.connectNode(nn,p);
                }else{
                    this.getNodeInfo(nn,p);
                }

                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(p_41427_);
    }
}
