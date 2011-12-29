package data;

import bot.IdleBot;
import data.Item.ItemClass;
import data.Item.Type;
import generators.ItemGenerator;
import generators.MonsterGenerator;
import generators.MonsterGenerator.BossStrings;

public class Monster extends Playable {

	private transient int ticks = 0;
	public BossStrings strings;
	private int bonus = 0;
	
	public Monster(String name2, Alignment align) {
		this(name2, align, null);
	}
	
	public Monster(String name, Alignment align, BossStrings strings) {
		super(name, "Monster", align);
		this.strings = strings;
		if(strings!=null) {
			IdleBot.botref.messageChannel(strings.intro);
		}
		generateEquipment();
		warp();
	}
	
	private void generateEquipment() {
		for(Slot s : Slot.values()) {
			Item newItem = ItemGenerator.generateItem(s);
			if(s == Slot.Weapon) {
				if(this.canEquip(s,  newItem) || this.strings != null) {
					equipment.put(s, newItem);
				} else {
					equipment.put(s, new Item("fist", 0, Type.Physical, ItemClass.Newbie));
				}
			} else if(this.canEquip(s, newItem) || this.strings != null)
				equipment.put(s,  newItem);
		}
	}

	@Override
	public void takeTurn() {
		if(ticks++ > 10000) {
			ticks = 0;
			move();
		}
	}
	
	public void addToBonus(int i) {
		bonus += i;
		this.level = (short) (bonus/45);
	}
	
	public int getBonus() {
		return bonus;
	}
	
	public void die(Playable second) {
		if(second == null) return;
		IdleBot.botref.getPlayersRaw().add(MonsterGenerator.generateMonster(null, -1));
		IdleBot.botref.getPlayersRaw().remove(this);
	}
	
	public String toString() { 
		return this.name;
	}
}
