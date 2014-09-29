package limette.CartoBlock;

import java.util.Random;
import java.util.Set;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.OpenBlocks.Items;
import openblocks.common.MapDataBuilder;
import openblocks.common.MapDataBuilder.ChunkJob;
import openblocks.common.entity.EntityCartographer;
import openblocks.common.entity.EntityItemProjectile;
import openblocks.common.item.ItemCartographer;
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.item.ItemHeightMap;
import openblocks.common.MapDataBuilder;
import openblocks.common.MapDataManager;
import openmods.utils.BitSet;

public class CartographerBlock extends BlockContainer {
	public CartographerBlock( int id, Material mat ){
		super( id, mat );
		
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHardness(5.6f);
		this.setResistance(56.34f);
		
	}
	
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, 
									int par6, float par7, float par8, float par9)
									{
		
		
		
		
		if (!world.isRemote)
		{
			FMLNetworkHandler.openGui(player, CartographerBlockMod.instance,
										0, world, x, y, z);
			
			player.openGui(CartographerBlockMod.instance, 0, world, x, y, z);
			/*
			EntityCartographer carto = new EntityCartographer(world);
			carto.setPosition(x,  y,  z);
			
			int offsetX[] = {0, 0, 4 * 16, 4 * 16};
			int offsetZ[] = {0, 4 * 16, 0, 4 * 16};
			
			for (int i = 0; i < 4; i++)
			{
				int ox = offsetX[i];
				int oz = offsetZ[i];
				
				int useX = x + ox;
				int useZ = z + oz;
				
				int newMapId = MapDataManager.createNewMap(world, (byte) 0);
				carto.jobs.startMapping(world, newMapId, useX, useZ);
			
				MapDataBuilder builder = new MapDataBuilder(newMapId);
				builder.resetMap(world, useX, useZ);
			
				BitSet bits = new BitSet();
				builder.resizeIfNeeded(bits);
			
				Set<MapDataBuilder.ChunkJob> chunkJobs = builder.createJobs(bits);
			
				while (true){
					ChunkJob job = builder.doNextChunk(world, useX, useZ, chunkJobs);
					if (job == null) break;
				
					System.out.println("Chunk.");
					chunkJobs.remove(job);
					bits.setBit(job.bitNum);
				}
			
				ItemStack hmap = new ItemStack(Items.heightMap, 1, newMapId);
				EntityItem hmapItem = new EntityItemProjectile(world, x, y+1, z, hmap);
				//world.spawnEntityInWorld(hmapItem);
			}
			*/
		}
		
		return true;
	}

	
	@Override
    public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
            dropItems(world, x, y, z);
            super.breakBlock(world, x, y, z, par5, par6);
    }

	private void dropItems(World world, int x, int y, int z){
		Random rand = new Random();

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory)) {
			return;
		}
		IInventory inventory = (IInventory) tileEntity;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i);

			if (item != null && item.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world,
						x + rx, y + ry, z + rz,
						new ItemStack(item.itemID, item.stackSize, item.getItemDamage()));

				if (item.hasTagCompound()) {
					entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
				}

				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
				item.stackSize = 0;
			}
		}
	}
	

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new CartographerBlockTileEntity();
	}
	
	@Override
	public int getRenderType() {
		return -1;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	public void registerIcons(IconRegister icon){
		this.blockIcon = icon.registerIcon("cartoblock:textures/items/CartographerBlockIcon.png");

}
	
	
}
