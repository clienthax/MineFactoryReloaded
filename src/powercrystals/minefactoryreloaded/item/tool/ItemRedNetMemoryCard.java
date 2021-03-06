package powercrystals.minefactoryreloaded.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.item.base.ItemFactory;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetLogic;

public class ItemRedNetMemoryCard extends ItemFactory {

	@Override
	public void addInfo(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips) {
		super.addInfo(stack, player, infoList, advancedTooltips);
		if (stack.getTagCompound() != null) {
			infoList.add("Programmed, " + stack.getTagCompound().getTagList("circuits", 10).tagCount() + " circuits");
			// TODO: localize ^
			infoList.add(MFRUtil.localize("tip.info.mfr.memorycard.wipe", true));
		}
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side,
			float xOffset, float yOffset, float zOffset) {
		if (world.isRemote) {
			return true;
		}

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityRedNetLogic) {
			if (itemstack.getTagCompound() == null) {
				NBTTagCompound tag = new NBTTagCompound();
				te.writeToNBT(tag);
				itemstack.setTagCompound(tag);
				player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.memorycard.uploaded"));
			} else {
				int circuitCount = itemstack.getTagCompound().getTagList("circuits", 10).tagCount();
				if (circuitCount > ((TileEntityRedNetLogic)te).getCircuitCount()) {
					player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.memorycard.error"));
				} else {
					((TileEntityRedNetLogic)te).readCircuitsOnly(itemstack.getTagCompound());
					player.addChatMessage(new ChatComponentTranslation("chat.info.mfr.rednet.memorycard.downloaded"));
				}
			}

			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return stack.getTagCompound() != null;
	}

}
