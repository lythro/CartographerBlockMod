package limette.CartoBlock;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CartographerBlock extends BlockContainer {	
	public CartographerBlock( int id, Material mat ){
		super( id, mat );
		
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHardness(5.6f);
		this.setResistance(56.34f);
		this.setTextureName("cartoblock:CartographerBlockModelTexture");
		
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
	

	
//	@Override
//	@SideOnly(Side.CLIENT)
//	public boolean addBlockDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer){
//		return true;
//	}
//	
//	@Override
//	@SideOnly(Side.CLIENT)
//	public boolean addBlockHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer){
//		return true;
//	}
	
//	@SideOnly(Side.CLIENT)
//	public Icon getIcon(){
//		return this.blockIcon;
//	}
//	
//	@Override
//	@SideOnly(Side.CLIENT)
//	public void registerIcons(IconRegister register){
//		this.blockIcon = register.registerIcon("cartoblock:CartographerBlock");
//	}
	
	
}
