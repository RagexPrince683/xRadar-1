package com.hfr.inventory;

import org.lwjgl.opengl.GL11;

import com.hfr.lib.RefStrings;
import com.hfr.tileentity.TileEntityHydro;
import com.hfr.tileentity.TileEntityMachineNet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GUIMachineNet extends GuiContainer {

	public static ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/gui/gui_net.png");
	private TileEntityMachineNet diFurnace;
	
	public GUIMachineNet(InventoryPlayer invPlayer, TileEntityMachineNet tedf) {
		super(new ContainerMachineNet(invPlayer, tedf));
		diFurnace = tedf;

		this.xSize = 176;
		this.ySize = 168;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = this.diFurnace.hasCustomInventoryName() ? this.diFurnace.getInventoryName() : I18n.format(this.diFurnace.getInventoryName());
		
		this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if(!diFurnace.operational() || !diFurnace.hasSpace())
			drawTexturedModalRect(guiLeft + 99, guiTop + 38, 176, 0, 10, 10);
		
		if(diFurnace.slots[4] != null)
			drawTexturedModalRect(guiLeft + 121, guiTop + 38, 176, 0, 10, 10);
	}
}
