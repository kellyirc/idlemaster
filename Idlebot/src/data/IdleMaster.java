package data;

import data.Item.Type;

public class IdleMaster extends Player {

	public IdleMaster() {
		super("IdleMaster", "saltycanoebags", "SentientBeing", Alignment.Neutral);
		generateNewEquipment();
	}
	
	@Override
	protected void generateNewEquipment() {
		equipment.put(Slot.Body, new Item("Avaracious Armor of the Idling Gods", 1000, Type.Physical, Item.ItemClass.Special));
		equipment.put(Slot.Feet, new Item("Fastidious Boots of the Idling Gods", 1000, Type.Physical, Item.ItemClass.Special));
		equipment.put(Slot.Finger, new Item("Rebellious Ring of the Idling Gods", 1000, Type.Magical, Item.ItemClass.Special));
		equipment.put(Slot.Hands, new Item("Glowing Gauntlets of the Idling Gods", 1000, Type.Physical, Item.ItemClass.Special));
		equipment.put(Slot.Head, new Item("Feathered Fez of the Idling Gods", 1000, Type.Physical, Item.ItemClass.Special));
		equipment.put(Slot.Legs, new Item("Glowing Greaves of the Idling Gods", 1000, Type.Physical, Item.ItemClass.Special));
		equipment.put(Slot.Neck, new Item("Transcendent Torc of the Idling Gods", 1000, Type.Magical, Item.ItemClass.Special));
		equipment.put(Slot.Shield, new Item("Supersonic Shield of the Idling Gods", 1000, Type.Physical, Item.ItemClass.Special));
		equipment.put(Slot.Weapon, new Item("Wingsaber, the Whirling Blade", 500, Type.Emotional, Item.ItemClass.Special));
		equipment.put(Slot.Charm, new Item("Cosmic Charm of the Idling Gods", 50, Type.Spiritual, Item.ItemClass.Special));
	}

	@Override
	protected boolean canEquip(Slot s, Item i) {
		return false;
	}

}
