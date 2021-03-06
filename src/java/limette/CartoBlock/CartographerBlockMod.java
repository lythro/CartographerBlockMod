package limette.CartoBlock;

import appeng.api.Blocks;
import appeng.me.block.BlockInterface;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import openblocks.OpenBlocks.Items;
import openblocks.common.item.ItemCartographer;
import openblocks.common.item.ItemEmptyMap;
import openmods.config.RegisterBlock;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = CartographerBlockMod.MODID, name=CartographerBlockMod.NAME, version = CartographerBlockMod.VERSION, dependencies = "required-after:AppliedEnergistics;required-after:OpenBlocks;required-after:OpenComputers")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels={"cartoBlockMod"},  packetHandler=CartographerPacketHandler.class)
public class CartographerBlockMod
{
    public static final String MODID = "lim_carto_block";
    public static final String VERSION = "1.1";
    public static final String NAME = "CartographerBlockMod";
    
    public static final int MEMAPINTERFACE_ID = 2588;
    public static final int CARTOBLOCK_ID = 2589;
    
    
    @Instance( MODID )
    public static CartographerBlockMod instance; 
    
    public static Block cartoBlock;
    public static Block meMapInterfaceBlock;
    
    @SidedProxy(clientSide="limette.CartoBlock.CartographerBlockClientProxy", serverSide="limette.CartoBlock.CartographerBlockServerProxy")
    public static CommonProxy proxy;
    
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	cartoBlock = (new CartographerBlock( CARTOBLOCK_ID, Material.rock ));
    	meMapInterfaceBlock = new MEMapInterfaceBlock(MEMAPINTERFACE_ID, Material.rock);
    	
    	GameRegistry.registerBlock(meMapInterfaceBlock, "meMapInterfaceBlock");
    	LanguageRegistry.addName(meMapInterfaceBlock, "ME-OC Map Interface");
    	MinecraftForge.setBlockHarvestLevel(cartoBlock, "pickaxe", 1);
    	
    	GameRegistry.registerBlock(cartoBlock, "cartoBlock");
    	LanguageRegistry.addName(cartoBlock, "Cartographer Block");
    	MinecraftForge.setBlockHarvestLevel(cartoBlock, "pickaxe", 1);
    	
    	GameRegistry.registerTileEntity(MEMapInterfaceTileEntity.class, "MEOCMapInterfaceTE");
    	GameRegistry.registerTileEntity(CartographerBlockTileEntity.class, "CartographerBlockTE");//Container");
    	
    	NetworkRegistry.instance().registerGuiHandler(this, new CartographerGUIHandler());
    	
//    	Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("cartoblock:textures/blocks/CartographerBlock.png"));
    	proxy.registerRenderers();
    
    	// 'R', BlockReferences.EnumBlocks.rubyBlock
    	// 'P', ComputerCraft.Blocks.computer
    	// 'R', Block.blockRedstone
    	
    	GameRegistry.addRecipe(new ItemStack(cartoBlock, 1), "CIC", "IMI", "CIC",
    			'I', Block.blockIron, 'C', Items.cartographer, 'M', Item.emptyMap);
    	
    	GameRegistry.addRecipe(new ItemStack(meMapInterfaceBlock, 1), "CMC", "MIM", "CMC",
    			'C', Items.cartographer, 'M', Items.emptyMap, 'I', Blocks.blkInterface ); 
    	
    }

    
}
