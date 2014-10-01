package limette.CartoBlock;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.block.BlockAnvil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import openmods.gui.component.GuiComponentCraftingGrid;
import openmods.network.PacketHandler;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;

public class CartographerBlockGUI extends GuiContainer {

	public static final ResourceLocation textureResourceLoc = new ResourceLocation("cartoblock:textures/gui/crafting_table_map_progress.png"); 
	private CartographerBlockTileEntity cartoBlock;
	
	private GuiTextField textfield;

	public CartographerBlockGUI (InventoryPlayer inventoryPlayer,
			CartographerBlockTileEntity tileEntity) {
		//the container is instantiated and passed to the superclass for handling
		super(new CartographerBlockContainer(inventoryPlayer, tileEntity));
		cartoBlock = tileEntity;
		
	}

	@Override
	public void initGui() {
		super.initGui();
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		this.textfield = new GuiTextField(fontRenderer, x + 100,  y + 67, 60, 10);
		this.textfield.setMaxStringLength(30);
        this.textfield.setFocused(false);
        this.textfield.setEnabled(true);
        
        this.textfield.setText(cartoBlock.dimName);
        System.out.println("client: dimName: " + cartoBlock.dimName);
	}
	
	@Override
	public void keyTyped(char c, int i) {
		if (textfield.isFocused()) {
			textfield.textboxKeyTyped(c, i);
			sendChangeToServer();
		}
		else
			super.keyTyped(c, i);
	}
	
	
	public void sendChangeToServer(){
	    ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
	    DataOutputStream outputStream = new DataOutputStream(bos);
	    try {
	        outputStream.writeInt(cartoBlock.xCoord);
	        outputStream.writeInt(cartoBlock.yCoord);
	        outputStream.writeInt(cartoBlock.zCoord);
	        //write the relevant information here... 
	        outputStream.writeUTF(textfield.getText());
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	               
	    Packet250CustomPayload packet = new Packet250CustomPayload();
	    packet.channel = "cartoBlockMod";
	    packet.data = bos.toByteArray();
	    packet.length = bos.size();

	    PacketDispatcher.sendPacketToServer(packet);
	    
	    System.out.println("Send packet - " + textfield.getText());
	    
	    //cartoBlock.setDimName(textfield.getText());
	}
	
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		textfield.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		this.textfield.drawTextBox();
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
		
		if (this.cartoBlock.countdown < CartographerBlockTileEntity.runTime) {
			int pX = cartoBlock.getCookProgressScaled(15);
			drawTexturedModalRect(x + 86 + 7 - pX/2, y + 35, xSize + 7 - pX/2, 0, pX, 60);
		}

	}

}
