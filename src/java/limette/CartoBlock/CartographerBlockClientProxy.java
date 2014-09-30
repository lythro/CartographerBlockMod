package limette.CartoBlock;

import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;

public class CartographerBlockClientProxy extends CommonProxy{
	
	@Override
	public void registerRenderers(){
		CartographerBlockModel model = new CartographerBlockModel();
		ClientRegistry.bindTileEntitySpecialRenderer(CartographerBlockTileEntity.class, new CartographerBlockRenderer(model));
		MinecraftForgeClient.registerItemRenderer(CartographerBlockMod.CARTOBLOCK_ID, new CartographerBlockItemRenderer(model));
	}

}
