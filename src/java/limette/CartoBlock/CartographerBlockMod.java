package limette.CartoBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = CartographerBlockMod.MODID, name=CartographerBlockMod.NAME, version = CartographerBlockMod.VERSION)
public class CartographerBlockMod
{
    public static final String MODID = "lim_carto_block";
    public static final String VERSION = "1.0";
    public static final String NAME = "CartographerBlockMod";
    
    @Instance( MODID )
    public static CartographerBlockMod instance; 
    
    public static Block cartoBlock;
    
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	cartoBlock = (new CartographerBlock( 500, Material.rock ));
    	LanguageRegistry.addName(cartoBlock, "Cartographer Block");
    	MinecraftForge.setBlockHarvestLevel(cartoBlock, "pickaxe", 1);
    	
    	GameRegistry.registerBlock(cartoBlock, "cartoBlock");
    	GameRegistry.registerTileEntity(CartographerBlockTileEntity.class, "CartographerBlockContainer");
    	
    	NetworkRegistry.instance().registerGuiHandler(this, new CartographerGUIHandler());
    }

    
}
