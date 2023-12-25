package de.raidandfa.networker.network.utils;

import net.minecraft.core.BlockPos;

public class AddressUtils {

    public static byte[] addressFromBlockPos(BlockPos bp, byte dev_id){

        int xpos = bp.getX() + (2<<23);
        int zpos = bp.getZ() + (2<<23);

        int chunkpos_x = xpos % 16; // 4 bits
        int chunkpos_z = zpos % 16; // 4 bits
        int y_pos = bp.getY() + (2<<7); // 32 bits (usually -64 to 319?)

        int chunk_x = (xpos / 16); // 28 bits (32 - 4)
        int chunk_z = (zpos / 16); // 28 bits (32 - 4)

        // address format [ 16x8bit ]:

        // dim_id [ 8 bits ]
        // chunk big id [ top byte of x and y ]
        // chunk x [ 2x8 bits ]
        // chunk z [ 2x8 bits ]
        // cpos y [ 2x8 bits ]
        // cpos x [ 4 bits ]
        // cpos z [ 4 bits ]
        // 1 [8 bits]
        return new byte[]{
                0x26, 0x03,
                0x10, 0x00, // microsoft.. LOL
                0x00, 0x00, // nothing for now, maybe something cool later?
                (byte)(chunk_x>>16), (byte)(chunk_z>>16), // chunk big number, usually 0
                (byte)(chunk_x>>8), (byte)(chunk_z>>8), // chunk middle number
                (byte)(chunk_x), (byte)(chunk_z), // exact chunk
                (byte)(y_pos>>8), (byte)(y_pos), // y position in chunk
                (byte)((chunkpos_x<<4) + chunkpos_z), dev_id // xz in chunk, and device id (always 01 for clients)
        }; // addresses are 128 bit, "ipv6" (not really)
    }

    public static String addressToString(byte[] address){
        return String.format("%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x",
                address[0],address[1],address[2],address[3],
                address[4],address[5],address[6],address[7],
                address[8],address[9],address[10],address[11],
                address[12],address[13],address[14],address[15]);
    }

    public static byte[] getMulticastAddress(byte devid) {
        return new byte[]{
                (byte) 0xff,0x02, 0x00,0x00,
                0x00,0x00, 0x00,0x00,
                0x00,0x00, 0x00,0x01,
                (byte) 0xff,0x00, 0x00, devid
        };
    }
}
