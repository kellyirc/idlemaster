package generators;

import generators.Utilities.Data;

import data.Item.ItemClass;
import data.Item.Type;
import data.Playable.Alignment;
import data.Playable.Slot;
import data.Player;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import bot.IdleBot;

import data.Item;

public class ItemGenerator {

	static HashMap<data.Playable.Slot, Data[]> equipment = new HashMap<>();
	static HashMap<data.Item.ItemClass, Data[]> types = new HashMap<>();

	static{
		initialize();
	}

	public static void initialize() {
		try {
			loadEquipment();
			loadTypes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static data.Playable.Slot determineSlot(String name) {

		name = name.substring(0, name.indexOf('.'));

		for (data.Playable.Slot s : data.Playable.Slot.values()) {
			if (s.toString().toLowerCase().equals(name))
				return s;
		}

		return null;
	}

	private static data.Item.ItemClass determineType(String name) {

		name = name.substring(0, name.indexOf('.'));

		for (data.Item.ItemClass s : data.Item.ItemClass.values()) {
			if (s.toString().equals(name))
				return s;
		}
		return null;
	}

	private static void loadEquipment() throws IOException {
		Document doc = Jsoup
				.connect(
						"https://idlemaster.googlecode.com/svn/trunk/Idlebot/data/equipment/")
				.get();
		Elements nodes = doc.select("li a");
		for (Element e : nodes) {
			if (!e.ownText().contains(".txt"))
				continue;

			data.Playable.Slot curslot = determineSlot(e.ownText());
			if (curslot == null) {
				System.err.println("Slot error: " + e.ownText());
				continue;
			}

			ArrayList<Data> temp = Utilities.loadFile( new URL(
					"https://idlemaster.googlecode.com/svn/trunk/Idlebot/data/equipment/"
							+ e.ownText()));

			equipment.put(curslot, temp.toArray(GeneratorData.DUMMYARRAY));
		}
	}

	private static void loadTypes() throws IOException {
		Document doc = Jsoup
				.connect(
						"https://idlemaster.googlecode.com/svn/trunk/Idlebot/data/descriptors/")
				.get();
		Elements nodes = doc.select("li a");
		for (Element e : nodes) {
			if (!e.ownText().contains(".txt"))
				continue;

			data.Item.ItemClass curtype = determineType(e.ownText());
			if (curtype == null) {
				continue;
			}

			ArrayList<Data> temp = Utilities.loadFile(new URL("https://idlemaster.googlecode.com/svn/trunk/Idlebot/data/descriptors/"+e.ownText()));

			types.put(curtype, temp.toArray(GeneratorData.DUMMYARRAY));
		}
		
		types.put(ItemClass.Animal, Utilities.loadFile(new URL("https://idlemaster.googlecode.com/svn/trunk/Idlebot/data/monsters/Monsters.txt")).toArray(GeneratorData.DUMMYARRAY));

	}

	public static Item generateItem(Slot slot) {
		return generateItem(slot, null, null);
	}

	public static Item generateItem(ItemClass cl) {
		return generateItem(null, cl, null);
	}

	public static Item generateItem(Type type) {
		return generateItem(null, null, type);
	}

	public static Item generateItem() {
		return generateItem(null, null, null);
	}

	public static Item generateItem(Slot slot, ItemClass cl, Type type) {

		StringBuffer itemName = new StringBuffer();
		double modPercent = 0;
		int itemValue = 0;
		ItemClass itemClass = cl;
		Type itemType = type;
		Alignment align = null;
		// all trys become successes if type = Idle
		Data choice;

		// try to make avatar (1/10000)
		if (cl == ItemClass.Idle || cl == ItemClass.Avatar
				|| Math.random() * 10000 > 9999) {
			if(itemClass == null) itemClass = ItemClass.Avatar;
			//choice = types.get(ItemClass.Avatar)[GeneratorData.random.nextInt(types.get(ItemClass.Avatar).length)];
			Player pChoice = IdleBot.botref.getRandomPlayer();
			if(pChoice == null)
				for(Player p : IdleBot.botref.getPlayers()) {
					if(p.getName().equals("IdleMaster")) pChoice = p;
				}
			if(pChoice != null) {
				itemName.append(pChoice.getName()+"'s ");
				Item rItem = pChoice.getEquipmentItems().toArray(new Item[0])[(int) (Math.random() * pChoice.getEquipment().size())];
				itemValue += rItem.getValue();
			}
		}

		// try to add retro
		if (cl == ItemClass.Idle
				|| cl == ItemClass.Retro
				|| Math.random() * 1000 > (itemClass == ItemClass.Avatar ? 900
						: 955)) {

			choice = types.get(ItemClass.Retro)[(int) (Math.random() * (types
					.get(ItemClass.Retro).length-1))];
			itemValue += choice.getValue();
			itemName.append(choice + " ");
			if (itemClass == null)
				itemClass = ItemClass.Retro;
			if(itemType == null) 
				itemType = Type.Emotional;
		}

		// try to add descriptor (25% normal, 55% if avatar)
		// value descriptors need to be taken care of
		if (cl == ItemClass.Idle
				|| Math.random() * 1000 > (itemClass == ItemClass.Avatar ? 150
						: 550)) {
			Data[] array;
			int i = (int) (Math.random() * 100);
			if (i < 6) {
				array = GeneratorData.evils;
				align = Alignment.Evil;
			} else if (i < 12) {
				array = GeneratorData.goods;
				align = Alignment.Good;
			} else if (i < 18) {
				array = GeneratorData.neutrals;
				align = Alignment.Neutral;
			} else if (i < 33) {
				array = GeneratorData.downvalue;
				modPercent = 0.5;
			} else if (i < 58) {
				array = GeneratorData.upvalue;
				modPercent = 1.5;
			} else {
				array = GeneratorData.prefixes;
			}
			choice = array[(int) (Math.random() * (array.length-1))];
			itemValue += choice.getValue();
			itemName.append(choice + " ");
		}
		// try to add color (75% avatar, 25% normal)
		if (cl == ItemClass.Idle
				|| Math.random() * 1000 > (itemClass == ItemClass.Avatar ? 250
						: 750)) {
			choice = GeneratorData.colors[(int) (Math.random() * (GeneratorData.colors.length-1))];
			itemValue += choice.getValue();
			itemName.append(choice + " ");
		}

		if (slot == null) {
			Slot[] e = equipment.keySet().toArray(new Slot[0]);
			slot = e[(int) (Math.random() * (e.length-1))];
		}

		// pick a base item, if slot, seed with slot
		// if base item is a proper named item
		// if no current class, or class is avatar, make class special
		choice = equipment.get(slot)[(int) (Math.random() * (equipment.get(slot).length-1))];
		itemValue += choice.getValue();
		itemName.append(choice);
		if (choice.toString().charAt(0) < 91 && itemClass == null)
			itemClass = ItemClass.Special;

		// try to become spirit class (5% avatar, 0.7% normal)
		if (cl == ItemClass.Idle
				|| cl == ItemClass.Spiritual
				|| Math.random() * 1000 > (itemClass == ItemClass.Avatar ? 950
						: 993)) {
			choice = types.get(ItemClass.Saint)[(int) (Math.random() * (types
					.get(ItemClass.Saint).length-1))];
			itemValue += choice.getValue();
			itemName.append(" of " + choice + ", ");

			choice = types.get(ItemClass.Animal)[(int) (Math.random() * (types
					.get(ItemClass.Animal).length-1))];
			itemValue += choice.getValue();
			itemName.append("the " + choice);
			if (itemClass == null)
				itemClass = ItemClass.Spiritual;

			if(itemType == null) 
				itemType = Type.Spiritual;

			// if not, try to become animal class (63% avatar, 42% normal)
		} else if (cl == ItemClass.Animal
				|| Math.random() * 100 > (itemClass == ItemClass.Avatar ? 37
						: 56)) {
			choice = types.get(ItemClass.Animal)[(int) (Math.random() * (types
					.get(ItemClass.Animal).length-1))];
			itemValue += choice.getValue();
			itemName.append(" of the " + choice);
			if (itemClass == null)
				itemClass = ItemClass.Animal;

			// if not, try to become saint class (17% avatar, 1% normal)
		} else if (cl == ItemClass.Saint
				|| Math.random() * 1000 > (itemClass == ItemClass.Avatar ? 830
						: 950)) {
			choice = types.get(ItemClass.Saint)[(int) (Math.random() * (types
					.get(ItemClass.Saint).length-1))];
			itemValue += choice.getValue();
			itemName.append(" of " + choice);
			if (itemClass == null)
				itemClass = ItemClass.Saint;

			if(itemType == null) 
				itemType = Type.Spiritual;
		}

		// take care of any remaining value descriptors that modify value by a factor (1.5, .5, etc)
		// determine type

		if (itemClass == null)
			itemClass = ItemClass.Normal;

		if (modPercent > 0)
			itemValue *= modPercent;
		
		if(itemType == null) {
			itemType = determineType(slot);
		}

		return new Item(itemName.toString(), itemValue, itemType,
				itemClass, align);
	}

	private static Type determineType(Slot slot) {
		switch (slot) {
		case Neck:
		case Charm:
		case Finger:
			return Type.Magical;
		case Head:
		case Hands:
		case Feet:
		case Legs:
		case Shield:
		case Body:
		case Weapon:
			return Type.Physical;
		default:
			return Type.Emotional;
		}
	}

}
