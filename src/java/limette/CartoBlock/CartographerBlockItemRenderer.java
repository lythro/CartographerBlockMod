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
		GL11.glScalef(-1F,  -1F,  1F);
		
		//correct position of the item according to where it should be rendered
		//(only minor adjustments)
		switch (type) {
		case INVENTORY:
			GL11.glTranslatef(0, -1F, 0);
			break;
		case EQUIPPED:
			GL11.glTranslatef(-0.6F, -1.4F, 0.5F);
			break;
		case ENTITY:
			GL11.glTranslatef(0, -1F, 0);
			break;
		case EQUIPPED_FIRST_PERSON:
			GL11.glTranslatef(-0.3F, -1.7F, 0.5F);
			break;
		case FIRST_PERSON_MAP:
			break;
		default:
			break;
		}
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("cartographerblockmod:textures/blocks/CartographerBlockModelTexture.png"));
		
		model.render(null, 0, 0, 0, 0, 0, 0.0625F);
		
		GL11.glPopMatrix();
		
		
	}

}
