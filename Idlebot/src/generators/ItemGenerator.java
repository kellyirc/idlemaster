package generators;

import generators.Utilities.Data;

import data.Item.ItemClass;
import data.Item.Type;
import data.Playable.Alignment;
import data.Playable.Slot;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import data.Item;

public class ItemGenerator {

	static HashMap<data.Playable.Slot, Data[]> equipment = new HashMap<>();
	static HashMap<data.Item.ItemClass, Data[]> types = new HashMap<>();

	static{
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

		// try to make avatar (3/1000)
		if (cl == ItemClass.Idle || cl == ItemClass.Avatar
				|| GeneratorData.random.nextInt(1000) > 997) {
			itemClass = ItemClass.Avatar;
			choice = types.get(ItemClass.Avatar)[GeneratorData.random.nextInt(types
					.get(ItemClass.Avatar).length)];
			itemName.append(choice + " ");
			itemValue += choice.getValue();
			if(type == null) 
				type = Type.Spiritual;
		}

		// try to add retro
		if (cl == ItemClass.Idle
				|| cl == ItemClass.Retro
				|| GeneratorData.random.nextInt(1000) > (itemClass == ItemClass.Avatar ? 960
						: 980)) {

			choice = types.get(ItemClass.Retro)[GeneratorData.random.nextInt(types
					.get(ItemClass.Retro).length)];
			itemValue += choice.getValue();
			itemName.append(choice + " ");
			if (itemClass == null)
				itemClass = ItemClass.Retro;
			if(type == null) 
				type = Type.Emotional;
		}

		// try to add descriptor (25% normal, 55% if avatar)
		// value descriptors need to be taken care of
		if (cl == ItemClass.Idle
				|| GeneratorData.random.nextInt(1000) > (itemClass == ItemClass.Avatar ? 450
						: 750)) {
			Data[] array;
			int i = GeneratorData.random.nextInt(100);
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
				// TODO make individualized mod percents
			} else if (i < 58) {
				array = GeneratorData.upvalue;
				modPercent = 1.5;
			} else {
				array = GeneratorData.prefixes;
			}
			choice = array[GeneratorData.random.nextInt(array.length)];
			itemValue += choice.getValue();
			itemName.append(choice + " ");
		}
		// try to add color (75% avatar, 25% normal)
		if (cl == ItemClass.Idle
				|| GeneratorData.random.nextInt(1000) > (itemClass == ItemClass.Avatar ? 250
						: 750)) {
			choice = GeneratorData.colors[GeneratorData.random.nextInt(GeneratorData.colors.length)];
			itemValue += choice.getValue();
			itemName.append(choice + " ");
		}

		if (slot == null) {
			Slot[] e = equipment.keySet().toArray(new Slot[0]);
			slot = e[GeneratorData.random.nextInt(e.length)];
		}

		// pick a base item, if slot, seed with slot
		// if base item is a proper named item
		// if no current class, or class is avatar, make class special
		choice = equipment.get(slot)[GeneratorData.random.nextInt(equipment.get(slot).length)];
		itemValue += choice.getValue();
		itemName.append(choice);
		if (choice.toString().charAt(0) < 91 && itemClass == null)
			itemClass = ItemClass.Special;

		// try to become spirit class (5% avatar, 0.3% normal)
		if (cl == ItemClass.Idle
				|| cl == ItemClass.Spiritual
				|| GeneratorData.random.nextInt(1000) > (itemClass == ItemClass.Avatar ? 950
						: 997)) {
			choice = types.get(ItemClass.Saint)[GeneratorData.random.nextInt(types
					.get(ItemClass.Saint).length)];
			itemValue += choice.getValue();
			itemName.append(" of " + choice + ", ");

			choice = types.get(ItemClass.Animal)[GeneratorData.random.nextInt(types
					.get(ItemClass.Animal).length)];
			itemValue += choice.getValue();
			itemName.append("the " + choice);
			if (itemClass == null)
				itemClass = ItemClass.Spiritual;

			// if not, try to become animal class (59% avatar, 39% normal)
		} else if (cl == ItemClass.Animal
				|| GeneratorData.random.nextInt(1000) > (itemClass == ItemClass.Avatar ? 410
						: 610)) {
			choice = types.get(ItemClass.Animal)[GeneratorData.random.nextInt(types
					.get(ItemClass.Animal).length)];
			itemValue += choice.getValue();
			itemName.append(" of the " + choice);
			if (itemClass == null)
				itemClass = ItemClass.Animal;

			// if not, try to become saint class (17% avatar, 1% normal)
		} else if (cl == ItemClass.Saint
				|| GeneratorData.random.nextInt(1000) > (itemClass == ItemClass.Avatar ? 830
						: 990)) {
			choice = types.get(ItemClass.Saint)[GeneratorData.random.nextInt(types
					.get(ItemClass.Saint).length)];
			itemValue += choice.getValue();
			itemName.append(" of " + choice);
			if (itemClass == null)
				itemClass = ItemClass.Saint;
		}

		// take care of any remaining value descriptors that modify value by a factor (1.5, .5, etc)
		// determine type

		if (itemType == null)
			itemType = determineType(slot);

		if (itemClass == null)
			itemClass = ItemClass.Normal;

		if (modPercent > 0)
			itemValue *= modPercent;
		
		if(type == null) {
			type = determineType(slot);
		}

		return new Item(itemName.toString(), itemValue, type,
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
