package limette.CartoBlock;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class CartographerBlockContainer extends Container {

	protected CartographerBlockTileEntity tileEntity;

	public CartographerBlockContainer (InventoryPlayer inventoryPlayer, CartographerBlockTileEntity te){
		tileEntity = te;
		
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
        
		//commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}


	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
						8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}
	
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		Slot slot = getSlot(i);

		if(slot != null && slot.getHasStack()) {
			ItemStack itemstack = slot.getStack();
			ItemStack result = itemstack.copy();

			if(i >= 36) {
				if(!mergeItemStack(itemstack, 0, 36, false)) {
					return null;
				}
			} else if(!mergeItemStack(itemstack, 36, 36 + tileEntity.getSizeInventory(), false)) {
				return null;
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