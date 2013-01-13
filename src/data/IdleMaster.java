package data;

import data.Item.Type;

public class IdleMaster extends Player {

	private transient int ticks = 100;
	
	public IdleMaster() {
		super("IdleMaster", "saltycanoebags", "SentientBeing", Alignment.Neutral);
		generateNewEquipment();
	}
	
	@Override
	protected void generateNewEquipment() {
		equipment.put(Slot.Body, new Item("Avaracious Armor of the Idling Gods", 5000, Type.Physical, Item.ItemClass.Idle));
		equipment.put(Slot.Feet, new Item("Fastidious Feet of the Idling Gods", 5000, Type.Physical, Item.ItemClass.Idle));
		equipment.put(Slot.Finger, new Item("Rebellious Ring of the Idling Gods", 5000, Type.Magical, Item.ItemClass.Idle));
		equipment.put(Slot.Hands, new Item("Glowing Gauntlets of the Idling Gods", 5000, Type.Physical, Item.ItemClass.Idle));
		equipment.put(Slot.Head, new Item("Feathered Fez of the Idling Gods",5000, Type.Physical, Item.ItemClass.Idle));
		equipment.put(Slot.Legs, new Item("Glowing Greaves of the Idling Gods", 5000, Type.Physical, Item.ItemClass.Idle));
		equipment.put(Slot.Neck, new Item("Transcendent Torc of the Idling Gods", 5000, Type.Magical, Item.ItemClass.Idle));
		equipment.put(Slot.Shield, new Item("Supersonic Shield of the Idling Gods", 5000, Type.Physical, Item.ItemClass.Idle));
		equipment.put(Slot.Weapon, new Item("Wingsaber, the Whirling Blade", 5000, Type.Emotional, Item.ItemClass.Idle));
		equipment.put(Slot.Charm, new Item("Cosmic Charm of the Idling Gods", 5000, Type.Spiritual, Item.ItemClass.Idle));
	}

	@Override
	public boolean canEquip(Slot s, Item i) {
		return false;
	}

	@Override
	protected void equip(Slot s, Item i) {
		return;
	}

	public void takeTurn() {
		modifyTime(10);
		if(ticks++ > 100) {
			ticks = 0;
			move();
		}
	}
	
}
