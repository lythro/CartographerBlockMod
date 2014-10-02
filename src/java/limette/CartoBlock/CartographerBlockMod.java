package limette.CartoBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import openblocks.OpenBlocks.Items;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = CartographerBlockMod.MODID, name=CartographerBlockMod.NAME, version = CartographerBlockMod.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels={"cartoBlockMod"},  packetHandler=CartographerPacketHandler.class)
public class CartographerBlockMod
{
    public static final String MODID = "lim_carto_block";
    public static final String VERSION = "1.0";
    public static final String NAME = "CartographerBlockMod";
    public static final int CARTOBLOCK_ID = 2589;
    
    @Instance( MODID )
    public static CartographerBlockMod instance; 
    
    public static Block cartoBlock;
    
    @SidedProxy(clientSide="limette.CartoBlock.CartographerBlockClientProxy", serverSide="limette.CartoBlock.CartographerBlockServerProxy")
    public static CommonProxy proxy;
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	cartoBlock = (new CartographerBlock( CARTOBLOCK_ID, Material.rock ));
    	LanguageRegistry.addName(cartoBlock, "Cartographer Block");
    	MinecraftForge.setBlockHarvestLevel(cartoBlock, "pickaxe", 1);
    	
    	GameRegistry.registerBlock(cartoBlock, "cartoBlock");
    	GameRegistry.registerTileEntity(CartographerBlockTileEntity.class, "CartographerBlockContainer");
    	
    	NetworkRegistry.instance().registerGuiHandler(this, new CartographerGUIHandler());
    	
//    	Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("cartoblock:textures/blocks/CartographerBlock.png"));
    	proxy.registerRenderers();
    
    	// 'R', BlockReferences.EnumBlocks.rubyBlock
    	// 'P', ComputerCraft.Blocks.computer
    	// 'R', Block.blockRedstone
    	
    	GameRegistry.addRecipe(new ItemStack(cartoBlock, 1), "CIC", "IMI", "CIC",
    			'I', Block.blockIron, 'C', Items.cartographer, 'M', Item.emptyMap);
    	
    }

    
}
