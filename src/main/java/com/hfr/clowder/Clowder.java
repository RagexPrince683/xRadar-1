package com.hfr.clowder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hfr.data.ClowderData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

//it's like a faction
//but with cats!
public class Clowder {

	public String name;
	public String motd;
	public ClowderFlag flag;
	public int color;
	
	public int homeX;
	public int homeY;
	public int homeZ;
	
	public String leader;
	public List<String> members = new ArrayList();
	public HashMap<String, Long> applications = new HashMap();
	
	public static List<Clowder> clowders = new ArrayList();
	public static HashMap<String, Clowder> inverseMap = new HashMap();
	
	public boolean addMember(World world, String name) {
		
		if(world.getPlayerEntityByName(name) == null)
			return false;
		
		if(inverseMap.containsKey(name) || members.contains(name))
			return false;
		
		members.add(name);
		inverseMap.put(name, this);
		
		ClowderData.getData(world).markDirty();
		
		return true;
	}
	
	public boolean removeMember(World world, String name) {

		if(!inverseMap.containsKey(name) && !members.contains(name))
			return false;
		
		members.remove(name);
		inverseMap.remove(name);

		ClowderData.getData(world).markDirty();
		
		return true;
	}
	
	public boolean transferOwnership(EntityPlayer player) {
		
		String key = player.getDisplayName();

		if(!members.contains(key))
			return false;
		
		leader = key;
		ClowderData.getData(player.worldObj).markDirty();
		
		return true;
	}
	
	public void setHome(double x, double y, double z, EntityPlayer player) {
		
		this.homeX = (int)x;
		this.homeY = (int)y;
		this.homeZ = (int)z;
		
		ClowderData.getData(player.worldObj).markDirty();
	}
	
	public boolean isOwner(EntityPlayer player) {
		
		String key = player.getDisplayName();
		
		return this.leader == key;
	}
	
	public boolean disbandClowder(EntityPlayer player) {
		
		String key = player.getDisplayName();
		
		if(this.leader != key)
			return false;
		
		clowders.remove(this);
		recalculateIMap();
		this.leader = "";
		
		ClowderData.getData(player.worldObj).markDirty();
		
		return true;
		
	}
	
	public boolean valid() {
		return this.leader != "";
	}
	
	public void saveClowder(int i, NBTTagCompound nbt) {
		nbt.setString(i + "_name", this.name);
		nbt.setString(i + "_motd", this.motd);
		nbt.setInteger(i + "_flag", this.flag.ordinal());
		nbt.setInteger(i + "_color", this.color);
		nbt.setInteger(i + "_homeX", this.homeX);
		nbt.setInteger(i + "_homeY", this.homeY);
		nbt.setInteger(i + "_homeZ", this.homeZ);

		nbt.setString(i + "_leader", this.leader);
		nbt.setInteger(i + "_members", this.members.size());
		
		for(int j = 0; j < this.members.size(); j++) {

			nbt.setString(i + "_" + j, this.members.get(j));
		}
	}
	
	public static Clowder loadClowder(int i, NBTTagCompound nbt) {
		
		Clowder c = new Clowder();
		c.name = nbt.getString(i + "_name");
		c.motd = nbt.getString(i + "_motd");
		c.flag = ClowderFlag.values()[nbt.getInteger(i + "_flag")];
		c.color = nbt.getInteger(i + "_color");
		c.homeX = nbt.getInteger(i + "_homeX");
		c.homeY = nbt.getInteger(i + "_homeY");
		c.homeZ = nbt.getInteger(i + "_homeZ");

		c.leader = nbt.getString(i + "_leader");
		int count = nbt.getInteger(i + "_members");
		
		for(int j = 0; j < count; j++) {
			
			c.members.add(nbt.getString(i + "_" + j));
		}
		
		return c;
	}
	
	public void notifyLeader(World world, ChatComponentText message) {

		notifyPlayer(world, this.leader, message);
	}
	
	public void notifyAll(World world, ChatComponentText message) {

		for(String player : this.members) {
			notifyPlayer(world, player, message);
		}
	}
	
	public void notifyPlayer(World world, String player, ChatComponentText message) {

		EntityPlayer notif = world.getPlayerEntityByName(player);
		
		if(notif != null) {
			notif.addChatMessage(message);
		}
	}
	
	
	/// GLOBAL METHODS ///
	public static void recalculateIMap() {
		
		inverseMap.clear();
		
		for(Clowder clowder : clowders) {
			for(String member : clowder.members) {
				inverseMap.put(member, clowder);
			}
		}
	}
	
	public static void readFromNBT(NBTTagCompound nbt) {
		
		int count = nbt.getInteger("clowderCount");
		
		for(int i = 0; i < count; i++)
			clowders.add(loadClowder(i, nbt));
		
		recalculateIMap();
	}
	
	public static void writeToNBT(NBTTagCompound nbt) {
		
		nbt.setInteger("clowderCount", clowders.size());

		for(int i = 0; i < clowders.size(); i++)
			clowders.get(i).saveClowder(i, nbt);
	}
	
	public static Clowder getClowderFromPlayer(EntityPlayer player) {
		
		String key = player.getDisplayName();
		
		return inverseMap.get(key);
	}
	
	public static Clowder getClowderFromName(String name) {

		for(Clowder clowder : clowders) {
			if(clowder.name.equals(name))
				return clowder;
		}
		
		return null;
	}
	
	public static void createClowder(EntityPlayer player, String name) {

		String leader = player.getDisplayName();
		
		Clowder c = new Clowder();
		c.name = name;
		c.leader = leader;
		c.members.add(leader);
		c.color = player.getRNG().nextInt(0x1000000);
		c.setHome(player.posX, player.posY, player.posZ, player);
		
		c.motd = "Message of the day!";
		c.flag = ClowderFlag.TRICOLOR;
		
		clowders.add(c);
		inverseMap.put(leader, c);
		
		ClowderData.getData(player.worldObj).markDirty();
	}
}