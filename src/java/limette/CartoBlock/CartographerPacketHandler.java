package limette.CartoBlock;

import java.io.DataInputStream;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class CartographerPacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		
		System.out.println("Got packet");
		
		DataInputStream in = new DataInputStream( new ByteInputStream( packet.data, packet.length ) );
		
		int x = 0;
		int y = 0;
		int z = 0;
		String newName = "";
		boolean good = true;
		try {
			x = in.readInt();
			y = in.readInt();
			z = in.readInt();
			newName = in.readUTF();
			
			in.close();
			
		} catch (Exception e) {
			good = false;
		}
		
		if (!good) return;
		

		EntityPlayerMP playerMP = (EntityPlayerMP)player;

		TileEntity te = playerMP.worldObj.getBlockTileEntity(x, y, z);
		if(te != null){
			if(te instanceof CartographerBlockTileEntity){
				CartographerBlockTileEntity tet = (CartographerBlockTileEntity)te;
				tet.setDimName( newName );
				playerMP.worldObj.markBlockForUpdate(x, y, z);//this could also be the code to make a custom packet to send to all players
			}
		}

	}

}
