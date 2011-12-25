package generators;

import generators.Utilities.Data;

import data.Item.ItemClass;
import data.Item.Type;
import data.Playable.Alignment;
import data.Playable.Slot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import data.Item;

public class ItemGenerator {

	private static final Data[] DUMMYARRAY = new Data[0];

	/*
	public static void main(String[] args) {
		new ItemGenerator();
	}*/

	Random random = new Random();

	Data[] colors;

	Data[] downvalue;
	Data[] upvalue;

	Data[] evils;
	Data[] goods;
	Data[] neutrals;

	Data[] prefixes;

	HashMap<data.Playable.Slot, Data[]> equipment = new HashMap<>();
	HashMap<data.Item.ItemClass, Data[]> types = new HashMap<>();

	public ItemGenerator() {
		loadEquipment();
		loadTypes();
		loadOthers();
	}

	private data.Playable.Slot determineSlot(String name) {

		name = name.substring(0, name.indexOf('.'));

		for (data.Playable.Slot s : data.Playable.Slot.values()) {
			if (s.toString().toLowerCase().equals(name))
				return s;
		}

		return null;
	}

	private data.Item.ItemClass determineType(String name) {

		name = name.substring(0, name.indexOf('.'));

		for (data.Item.ItemClass s : data.Item.ItemClass.values()) {
			if (s.toString().equals(name))
				return s;
		}
		return null;
	}

	private void loadEquipment() {
		File eq = new File("data/equipment/");
		for (File f : eq.listFiles()) {

			data.Playable.Slot curslot = determineSlot(f.getName());
			if (curslot == null) {
				System.err.println("Slot error: " + f.getName());
				continue;
			}

			ArrayList<Data> temp = Utilities.loadFile(this, f);

			equipment.put(curslot, temp.toArray(DUMMYARRAY));
		}
	}

	private void loadOthers() {
		colors = Utilities.loadFile(this,
				new File("data/descriptors/Colors.txt")).toArray(DUMMYARRAY);
		downvalue = Utilities.loadFile(this,
				new File("data/descriptors/Devalued.txt")).toArray(DUMMYARRAY);
		upvalue = Utilities.loadFile(this,
				new File("data/descriptors/Valuable.txt")).toArray(DUMMYARRAY);
		goods = Utilities
				.loadFile(this, new File("data/descriptors/Goods.txt"))
				.toArray(DUMMYARRAY);
		evils = Utilities
				.loadFile(this, new File("data/descriptors/Evils.txt"))
				.toArray(DUMMYARRAY);
		neutrals = Utilities.loadFile(this,
				new File("data/descriptors/Neutrals.txt")).toArray(DUMMYARRAY);
		prefixes = Utilities.loadFile(this,
				new File("data/descriptors/Prefixes.txt")).toArray(DUMMYARRAY);
	}

	private void loadTypes() {
		File eq = new File("data/descriptors/");
		for (File f : eq.listFiles()) {

			data.Item.ItemClass curtype = determineType(f.getName());
			if (curtype == null) {
				// System.err.println("Type error: "+f.getName());
				continue;
			}

			ArrayList<Data> temp = Utilities.loadFile(this, f);

			types.put(curtype, temp.toArray(DUMMYARRAY));
		}
	}

	public Item generateItem(Slot slot) {
		return generateItem(slot, null, null);
	}
	
	public Item generateItem(ItemClass cl) {
		return generateItem(null, cl, null);
	}
	
	public Item generateItem(Type type) {
		return generateItem(null, null, type);
	}
	
	public Item generateItem() {
		return generateItem(null, null, null);
	}
	
	public Item generateItem(Slot slot, ItemClass cl, Type type) {

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
				|| random.nextInt(1000) <= 3) {
			itemClass = ItemClass.Avatar;
			choice = types.get(ItemClass.Avatar)[random.nextInt(types
					.get(ItemClass.Avatar).length)];
			itemName.append(choice + " ");
			itemValue += choice.getValue();
		}

		// try to add retro
		if (cl == ItemClass.Idle || cl == ItemClass.Retro
				|| random.nextInt(1000) > (itemClass == ItemClass.Avatar ? 990
						: 999)) {

			choice = types.get(ItemClass.Retro)[random.nextInt(types.get(ItemClass.Retro).length)];
			itemValue += choice.getValue();
			itemName.append(choice + " ");
			if(itemClass == null) itemClass = ItemClass.Retro;
		}

		// try to add descriptor (25% normal, 55% if avatar)
		// value descriptors need to be taken care of
		if (cl == ItemClass.Idle
				|| random.nextInt(1000) > (itemClass == ItemClass.Avatar ? 450
						: 750)) {
			Data[] array;
			int i = random.nextInt(100);
			if (i < 6) {
				array = evils;
				align = Alignment.Evil;
			} else if (i < 12) {
				array = goods;
				align = Alignment.Good;
			} else if (i < 18) {
				array = neutrals;
				align = Alignment.Neutral;
			} else if (i < 33) {
				array = downvalue;
				modPercent = 0.5;
				// TODO make individualized mod percents
			} else if (i < 58) {
				array = upvalue;
				modPercent = 1.5;
			} else {
				array = prefixes;
			}
			choice = array[random.nextInt(array.length)];
			itemValue += choice.getValue();
			itemName.append(choice + " ");
		}
		// try to add color (75% avatar, 25% normal)
		if (cl == ItemClass.Idle
				|| random.nextInt(1000) > (itemClass == ItemClass.Avatar ? 250
						: 750)) {
			choice = colors[random.nextInt(colors.length)];
			itemValue += choice.getValue();
			itemName.append(choice + " ");
		}

		if (slot == null) {
			Slot[] e = equipment.keySet().toArray(new Slot[0]);
			slot = e[random.nextInt(e.length)];
		}

		// pick a base item, if slot, seed with slot
		// if base item is a proper named item
		// if no current class, or class is avatar, make class special
		choice = equipment.get(slot)[random
				.nextInt(equipment.get(slot).length)];
		itemValue += choice.getValue();
		itemName.append(choice);
		if (choice.toString().charAt(0) < 91 && itemClass == null)
			itemClass = ItemClass.Special;

		// try to become spirit class (5% avatar, 0.3% normal)
		if (cl == ItemClass.Idle || cl == ItemClass.Spiritual
				|| random.nextInt(1000) > (itemClass == ItemClass.Avatar ? 950
						: 997)) {
			choice = types.get(ItemClass.Saint)[random.nextInt(types.get(ItemClass.Saint).length)];
			itemValue += choice.getValue();
			itemName.append(" of "+choice + ", ");

			choice = types.get(ItemClass.Animal)[random.nextInt(types.get(ItemClass.Animal).length)];
			itemValue += choice.getValue();
			itemName.append("the "+choice);
			if(itemClass == null) itemClass = ItemClass.Spiritual;

			// if not, try to become animal class (59% avatar, 39% normal)
		} else if(cl == ItemClass.Idle || cl == ItemClass.Animal
				|| random.nextInt(1000) > (itemClass == ItemClass.Avatar ? 410
						: 610)) {
			choice = types.get(ItemClass.Animal)[random.nextInt(types.get(ItemClass.Animal).length)];
			itemValue += choice.getValue();
			itemName.append(" of the "+choice);
			if(itemClass == null) itemClass = ItemClass.Animal;

			// if not, try to become saint class (17% avatar, 1% normal)
		} else if(cl == ItemClass.Idle || cl == ItemClass.Saint
				|| random.nextInt(1000) > (itemClass == ItemClass.Avatar ? 830
						: 990)) {
			choice = types.get(ItemClass.Saint)[random.nextInt(types.get(ItemClass.Saint).length)];
			itemValue += choice.getValue();
			itemName.append(" of "+choice);
			if(itemClass == null) itemClass = ItemClass.Saint;
		}

		// take care of any remaining value descriptors that modify value by a factor (1.5, .5, etc)
		// determine type
		
		if(itemType == null) itemType = determineType(slot);

		if (itemClass == null)
			itemClass = ItemClass.Normal;

		if (modPercent > 0)
			itemValue *= modPercent;

		return new Item(itemName.toString(), itemValue, determineType(slot),
				itemClass, align);
	}

	private Type determineType(Slot slot) {
		switch(slot) {
		case Neck: case Charm: case Finger: return Type.Magical;
		case Head: case Hands: case Feet: case Legs: case Shield: case Body: case Weapon: return Type.Physical;
		default: return Type.Emotional;
		}
	}

}
