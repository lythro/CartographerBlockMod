package limette.CartoBlock;

import java.util.List;

import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.SimpleComponent;
import appeng.api.IItemList;
import appeng.api.me.util.IMEInventory;
import appeng.api.me.util.IMEInventoryHandler;
import appeng.me.tile.TileInterfaceBase;
import appeng.proxy.helpers.TileLPInterface;
import appeng.util.ItemList;

import com.sun.org.apache.xml.internal.resolver.readers.XCatalogReader;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")
public class MEMapInterfaceBlock extends Block implements ITileEntityProvider{

	public MEMapInterfaceBlock(int par1, Material par2Material) {
		super(par1, par2Material);
		
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHardness(5.6f);
		this.setResistance(56.34f);
		this.setUnlocalizedName("cartographerblock.memapinterface.block");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new MEMapInterfaceTileEntity();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, 
									int par6, float par7, float par8, float par9)
									{
		return false;
	}
	
}
