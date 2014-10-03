package limette.CartoBlock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.xml.internal.stream.Entity;

import scala.tools.nsc.doc.base.comment.UnorderedList;
import appeng.proxy.helpers.TileLPInterface;
import cpw.mods.fml.common.Optional;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import openblocks.common.item.ItemHeightMap;

public class MEMapInterfaceTileEntity extends TileEntity implements SimpleComponent {

	@Override
	public String getComponentName() {
		return "me_map_interface";
	}
	
	public TileLPInterface findMEInterface() {
		TileLPInterface meinter = null;

		int dx[] = {-1, 1, 0, 0, 0, 0};
		int dy[] = {0, 0, -1, 1, 0, 0};
		int dz[] = {0, 0, 0, 0, -1, 1};

		for (int i = 0; i < dx.length; i++) {
			TileEntity te = worldObj.getBlockTileEntity(xCoord+dx[i], yCoord+dy[i], zCoord+dz[i]);
			if (te != null) {
				if (te instanceof TileLPInterface) {
					meinter = (TileLPInterface) te;
					break;
				}
			}
		}
		
		return meinter;
	}
	
	public TileEntityChest findChest() {
		TileEntityChest chest = null;
		
		int dx[] = {-1, 1, 0, 0, 0, 0};
		int dy[] = {0, 0, -1, 1, 0, 0};
		int dz[] = {0, 0, 0, 0, -1, 1};
		
		for (int i = 0; i < dx.length; i++) {
			TileEntity te = worldObj.getBlockTileEntity(xCoord+dx[i], yCoord+dy[i], zCoord+dz[i]);
			if (te != null) {
				if (te instanceof TileEntityChest) {
					chest = (TileEntityChest) te;
					break;
				}
			}
		}
		
		return chest;
	}
	
	public Map<String, List<ItemStack> > getMaps(TileLPInterface me) {
		Map<String, List<ItemStack> > maps = new java.util.HashMap<String, List<ItemStack> >();

		if (me != null) {			
			List<ItemStack> items = me.apiGetNetworkContents();

			for (int it = 0; it < items.size(); it++) {

				if (items.get(it).getItem() instanceof ItemHeightMap) {
					String name = items.get(it).getDisplayName();
					int s = name.indexOf("_");
					int e = name.lastIndexOf("_");
					String trname = name.substring(s+1, e);

					if (maps.get(trname) == null)
						maps.put(trname, new LinkedList<ItemStack>());
					
					maps.get(trname).add(items.get(it));
				}
			}
		}
		
		return maps;
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] getMapNames(Context context, Arguments args) {
		
		TileLPInterface me = findMEInterface();
		Map<String, List<ItemStack> > maps = getMaps(me);
		
		return maps.keySet().toArray();
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] requestMaps(Context context, Arguments args) {
		
		TileLPInterface me = findMEInterface();
		TileEntityChest chest = findChest();
		
		if (chest == null || me == null) {
			return new Object[]{false, "either chest or ME Interface not found!"};
		}
		
		Map<String, List<ItemStack> > maps = getMaps(me);
		String requestedMap = args.checkString(0);
		
		// check if chest has enough free slots
		int countFree = 0;
		for (int i = 0; i < chest.getSizeInventory(); i++) {
			if (chest.getStackInSlot(i) == null) countFree++;
		}
		
		if (countFree < 9) return new Object[]{false, "not enough space in chest!"};
		
		// spawn map items in chest
		String[] names = {"NW", "N", "NE", "E", "C", "W", "SW", "S", "SE"};
		for (int i = 0; i < names.length; i++) {
			// search for the item
			ItemStack item = null;
			List<ItemStack> list = maps.get(requestedMap);
			String currentNeed = "Map_" + requestedMap + "_" + names[i];
			
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j).getDisplayName().equals(currentNeed)) {
					item = list.get(j);
					break;
				}
			}
			
			if (item != null) {
				// search for the first free slot in the chest
				for (int j = 0; j < chest.getSizeInventory(); j++) {
					if (chest.getStackInSlot(j) == null) {
						ItemStack nItem = item.copy();
						chest.setInventorySlotContents(j, nItem);
						
						me.apiExtractNetworkItem(item, true);
						
						break;
					}
				}
			}
		}
		
		
		return new Object[]{true};
	}
}
