package limette.CartoBlock;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraftforge.client.MinecraftForgeClient;

public class CartographerBlockClientProxy extends CommonProxy{
	
	@Override
	public void registerRenderers(){
		ClientRegistry.bindTileEntitySpecialRenderer(CartographerBlockTileEntity.class, new CartographerBlockRenderer());
	}

}
