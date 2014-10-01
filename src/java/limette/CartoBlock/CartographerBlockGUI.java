package limette.CartoBlock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import openmods.gui.component.GuiComponentCraftingGrid;

import org.lwjgl.opengl.GL11;

public class CartographerBlockGUI extends GuiContainer {

	public static final ResourceLocation textureResourceLoc = new ResourceLocation("cartoblock:textures/gui/crafting_table.png"); 
	private CartographerBlockTileEntity cartoBlock;

	public CartographerBlockGUI (InventoryPlayer inventoryPlayer,
			CartographerBlockTileEntity tileEntity) {
		//the container is instantiated and passed to the superclass for handling
		super(new CartographerBlockContainer(inventoryPlayer, tileEntity));
		cartoBlock = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		//draw text and stuff here
		//the parameters for drawString are: string, x, y, color
		fontRenderer.drawString("Tiny", 8, 6, 4210752);
		//draws "Inventory" or your regional equivalent
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		//draw your Gui here, only thing you need to change is the path

		this.mc.renderEngine.bindTexture(textureResourceLoc);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;

		this.drawTexturedModalRect(x,  y,  0, 0, xSize, ySize);
		
		System.out.println(xSize + ", " + ySize);
		
		int pX = 63 - cartoBlock.getCookProgressScaled(63);
		System.out.println(pX);
		drawTexturedModalRect(x, y, xSize+1, 0, pX, ySize + 60);

	}

}
