package limette.CartoBlock;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.output.NullWriter;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkModHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import openblocks.OpenBlocks.Items;
import openblocks.common.MapDataBuilder;
import openblocks.common.MapDataBuilder.ChunkJob;
import openblocks.common.MapDataManager;
import openblocks.common.entity.EntityCartographer;
import openblocks.common.entity.EntityItemProjectile;
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.tileentity.TileEntityAutoAnvil;
import openmods.network.PacketHandler;
import openmods.utils.BitSet;
import scala.xml.persistent.SetStorage;
import sun.net.NetworkClient;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.WorldManager;
import net.minecraftforge.common.DimensionManager;


public class CartographerBlockTileEntity extends TileEntity implements IInventory {

	static public final int runTime = 10*20;
	private ItemStack[] inv;

	public boolean isRunning = false;
	public int countdown = runTime;
	public String dimName = "";


	public CartographerBlockTileEntity() {
		inv = new ItemStack[10];
	}

	public void setDimName(String name) {
		dimName = name;
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
	public net.minecraft.network.packet.Packet getDescriptionPacket() {
		NBTTagCompound tags = new NBTTagCompound();
		writeToNBT(tags);
		
		System.out.println("getDescriptionPacket!");
		
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tags);
	}
	
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
		
		if (worldObj.isRemote) System.out.println("CLIENT READ FROM NBT!");

		isRunning = tagCompound.getBoolean("isRunning");
		countdown = tagCompound.getInteger("countdown");
		dimName = tagCompound.getString("dimName");
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

		tagCompound.setBoolean("isRunning", isRunning);
		tagCompound.setInteger("countdown", countdown);
		tagCompound.setString("dimName", dimName);
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
		if (!worldObj.isRemote)
		{
			if (!isRunning)
			{
				// check, if there are maps to consume!
				ItemStack stack = getStackInSlot(9);
				if (stack != null) {
					if (stack.getItem() instanceof ItemEmptyMap && stack.stackSize >= 1) {
						stack.stackSize = stack.stackSize - 1;
						if (stack.stackSize == 0) this.setInventorySlotContents(9, null);
						isRunning = true;

						onInventoryChanged();
					}
				}
			}
			else countdown--;

			if (countdown <= 0)
			{
				System.out.println("countdown reached 0");

				boolean succ = false;
				for (int slot = 0; slot < 9; slot++) {
					if (this.getStackInSlot(slot) == null) {
						// success, found an empty slot to put the map into!
						succ = true;
						EntityCartographer carto = new EntityCartographer(worldObj);
						carto.setPosition(xCoord,  yCoord,  zCoord);

						int offsetX[] = {-64, 0, 64, -64, 0, 64, -64, 0, 64};
						int offsetZ[] = {-64, -64, -64, 0, 0, 0, 64, 64, 64};
						String names[] = {"nw", "n", "ne", "w", "c", "e", "sw", "s", "se"};

						int ox = offsetX[slot];
						int oz = offsetZ[slot];

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
						hmap.setItemName("Dim_" + dimName + "_" + names[slot]);

						setInventorySlotContents(slot, hmap);
						onInventoryChanged();

						countdown = runTime;
						isRunning = false;
						break;
					}
				}

				if (!succ) {
					// --> try to output every tick..
					isRunning = true;
					countdown = 0;
				}
			}
		}
	}



	@SideOnly(Side.CLIENT)
	/**
	 * Returns an integer between 0 and the passed value representing how close the current item is to being completely
	 * cooked
	 */
	public int getCookProgressScaled(int par1)
	{
		return (int)(par1 * ((float) (CartographerBlockTileEntity.runTime - this.countdown) / (float) CartographerBlockTileEntity.runTime));
	}


	@SideOnly(Side.CLIENT)
	/**
	 * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
	 * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
	public int getBurnTimeRemainingScaled(int par1)
	{
		return (int)(par1 * ((float) this.countdown / (float) CartographerBlockTileEntity.runTime)); 
	}



	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		return false;
	}

}
