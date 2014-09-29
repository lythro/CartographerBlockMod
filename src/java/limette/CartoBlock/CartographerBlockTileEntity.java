package limette.CartoBlock;

import java.util.Set;

import cpw.mods.fml.common.network.NetworkModHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import openblocks.OpenBlocks.Items;
import openblocks.common.MapDataBuilder;
import openblocks.common.MapDataBuilder.ChunkJob;
import openblocks.common.MapDataManager;
import openblocks.common.entity.EntityCartographer;
import openblocks.common.entity.EntityItemProjectile;
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.tileentity.TileEntityAutoAnvil;
import openmods.utils.BitSet;
import sun.net.NetworkClient;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.WorldManager;
import net.minecraftforge.common.DimensionManager;


public class CartographerBlockTileEntity extends TileEntity implements IInventory {

	private ItemStack[] inv;
	
	static int counter = 0;

	public CartographerBlockTileEntity() {
		inv = new ItemStack[10];
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inv[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}               
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
		
		if (!worldObj.isRemote)
			checkForEmptyMaps();
	}
	
	/// check if 9 empty maps are in the slot no.9
	public void checkForEmptyMaps() {
		ItemStack stack = getStackInSlot(9);
		
		System.out.println("CHECK");

		if (stack == null) return;
		if (stack.getItem() instanceof ItemEmptyMap) {
			if (stack.stackSize >= 9) {
				System.out.println( "STACK SIZE: " + stack.stackSize );
				
				for (int i = 0; i < 9; i++)
				{
					if (getStackInSlot(i) != null) return;
				}
				
				stack = stack.splitStack( stack.stackSize - 9 );
				if (stack != null && stack.stackSize == 0) stack = null;
				setInventorySlotContents(9, stack);
											
				// create the maps!
				EntityCartographer carto = new EntityCartographer(worldObj);
				carto.setPosition(xCoord,  yCoord,  zCoord);
				
				int offsetX[] = {-64, 0, 64, -64, 0, 64, -64, 0, 64};
				int offsetZ[] = {-64, -64, -64, 0, 0, 0, 64, 64, 64};
				String names[] = {"nw", "n", "ne", "w", "c", "e", "sw", "s", "se"};
				
				for (int i = 0; i < 9; i++)
				{
					int ox = offsetX[i];
					int oz = offsetZ[i];
					
					int useX = xCoord + ox;
					int useZ = zCoord + oz;
					
					int newMapId = MapDataManager.createNewMap(worldObj, (byte) 0);
					carto.jobs.startMapping(worldObj, newMapId, useX, useZ);
				
					MapDataBuilder builder = new MapDataBuilder(newMapId);
					builder.resetMap(worldObj, useX, useZ);
				
					BitSet bits = new BitSet();
					builder.resizeIfNeeded(bits);
				
					Set<MapDataBuilder.ChunkJob> chunkJobs = builder.createJobs(bits);
				
					while (true){
						ChunkJob job = builder.doNextChunk(worldObj, useX, useZ, chunkJobs);
						if (job == null) break;
					
						System.out.println("Chunk.");
						chunkJobs.remove(job);
						bits.setBit(job.bitNum);
					}
					ItemStack hmap = new ItemStack(Items.heightMap, 1, newMapId);
					hmap.setItemName("Dim_" + "_" + names[i]);
					
					setInventorySlotContents(i, hmap);
					
					//EntityItem hmapItem = new EntityItemProjectile(worldObj, xCoord, yCoord+1, zCoord, hmap);
					//worldObj.spawnEntityInWorld(hmapItem);
				}
				
			}
			
		}
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this &&
				player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);

		NBTTagList tagList = tagCompound.getTagList("Inventory");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inv.length) {
				inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < inv.length; i++) {
			ItemStack stack = inv[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		tagCompound.setTag("Inventory", itemList);
	}

	@Override
	public String getInvName() {
		return "cartoblockInv";
	}

	@Override
	public boolean isInvNameLocalized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateEntity()
	{
		//System.out.println( counter++ );
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		return false;
	}
}
