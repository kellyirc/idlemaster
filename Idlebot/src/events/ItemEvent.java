package events;

import java.net.MalformedURLException;
import java.net.URL;

import org.pircbotx.Colors;

import bot.IdleBot;

import data.Item;
import data.Playable.Slot;
import data.Player;

import generators.Utilities;

public class ItemEvent {
	
	static String[] goodEvents;
	static String[] badEvents;
	
	static {
		initialize();
	}

	public static void initialize() {
		try {
			goodEvents = Utilities.loadFileNoArray(new URL("http://idlemaster.googlecode.com/svn/trunk/Idlebot/data/events/item_bless.txt")).toArray(new String[0]);
			badEvents = Utilities.loadFileNoArray(new URL("http://idlemaster.googlecode.com/svn/trunk/Idlebot/data/events/item_forsake.txt")).toArray(new String[0]);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public ItemEvent(Player p) {
		this(p, null);
	}
	
	public ItemEvent(Player p, Boolean b) {
		Item i = getRandomItem(p);
		if(i == null) return;
		if((Math.random() > 0.3 || b != null && b) && b!= null && b!=false) {
			IdleBot.botref.messageChannel(modifyMessage(goodEvents[(int) (Math.random() * (goodEvents.length-1))], p, i, true));
			p.stats.itemBless++;
			i.setValue((int) (i.getValue()*1.1));
		} else if(b == null || !b){
			IdleBot.botref.messageChannel(modifyMessage(badEvents[(int) (Math.random() * (badEvents.length-1))], p, i, false));
			p.stats.itemForsake++;
			i.setValue((int) (i.getValue()*0.9));
		}
	}
	
	private String modifyMessage(String string, Player p, Item i, boolean isGood) {
		String message = (isGood ? Colors.DARK_GREEN : Colors.RED) + string.replaceAll("%player", p.getName()).replaceAll("%item", i.getName()).replaceAll("%hisher", getGenderName(p));
		message += " ["+i.getValue()+"->"+Math.round(i.getValue()*(isGood ? 1.1 : 0.9))+"]";
		return message;
	}

	public static Item getRandomItem(Player p) {
		if(p == null) return null;
		return p.getEquipmentRaw().get(Slot.values()[(int) (Math.random() * (Slot.values().length-1))]);
	}
}
