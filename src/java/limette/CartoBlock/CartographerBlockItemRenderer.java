package limette.CartoBlock;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

public class CartographerBlockItemRenderer implements IItemRenderer {

	private CartographerBlockModel model;
	
	public CartographerBlockItemRenderer(CartographerBlockModel model){
		this.model = model;
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		// TODO Auto-generated method stub
		GL11.glPushMatrix();
		GL11.glTranslatef((float)0, (float) 1F, (float) 0);
		GL11.glScalef(-1F,  -1F,  1F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("cartoblock:textures/blocks/CartographerBlock.png"));
		
		model.render(null, 0, 0, 0, 0, 0, 0.0625F);
		
		GL11.glPopMatrix();
		
		
	}

}
