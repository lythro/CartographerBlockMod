package limette.CartoBlock;


import openblocks.common.item.ItemEmptyMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class CartographerBlockContainer extends Container {

	protected CartographerBlockTileEntity tileEntity;

	public CartographerBlockContainer (InventoryPlayer inventoryPlayer, CartographerBlockTileEntity te){
		tileEntity = te;
		
		//commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer);
		
        int l;
        int i1;

        for (l = 0; l < 3; ++l)
        {
            for (i1 = 0; i1 < 3; ++i1)
            {
                this.addSlotToContainer(new Slot(tileEntity, i1 + l * 3, 30 + i1 * 18, 17 + l * 18));
            }
        }
        
        this.addSlotToContainer(new Slot(tileEntity, 9, 124, 35));
        
		
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}


	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
						8 + j * 18, 84 + i * 18));
			}
		}
	}
	
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		Slot slot = getSlot(i);

		if(slot != null && slot.getHasStack()) {
			ItemStack itemstack = slot.getStack();
			ItemStack result = itemstack.copy();
			
			if (i >= 36) {
				// Block -> Player
				if(!mergeItemStack(itemstack, 0, 36, false)) {
					return null;
				}
			} else {
				// Player -> Block
				// only allow empty maps!
				if (!(itemstack.getItem() instanceof ItemEmptyMap)){
					return null;
				}
				
				// allow only stack no. 9!
				if (!mergeItemStack(itemstack, 	36 + tileEntity.getSizeInventory()-1, 
												36 + tileEntity.getSizeInventory(), false)) {
					return null;
				}
			}

			if(itemstack.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}
			slot.onPickupFromSlot(player, itemstack); 
			return result;
		}
		return null;
	}
}