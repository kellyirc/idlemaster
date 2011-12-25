package data;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public abstract class Playable {
	
	public enum Alignment { Evil, Good, Neutral };
	public enum Slot { Neck, Head, Charm, Hands, Feet, Legs, Finger, Shield, Body, Weapon };

	protected Alignment alignment;
	
	protected String classType;

	protected HashMap<Slot, Item> equipment = new HashMap<>();

	protected short level = 0;

	protected String name;

	public Playable(String name2, String classtype2, Alignment align) {
		this.name = name2;
		this.classType = classtype2;
		this.alignment = align;
	}
	/**
	 * @return the alignment
	 */
	public Alignment getAlignment() {
		return alignment;
	}
	/**
	 * @return the classType
	 */
	public String getClassType() {
		return classType;
	}
	/**
	 * @return the level
	 */
	public short getLevel() {
		return level;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param alignment the alignment to set
	 */
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	/**
	 * @return the equipment
	 */
	public Set<Entry<Slot, Item>> getEquipment() {
		return equipment.entrySet();
	}
	/**
	 * @param classType the classType to set
	 */
	public void setClassType(String classType) {
		this.classType = classType;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(short level) {
		this.level = level;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public int calcTotal(data.Item.Type type) {
		int rev = 0;
		for(Item i : equipment.values()) {
			if(type!=null && i.getType() == type || type == null) rev += i.getValue();
		}
		return rev;
	}
	
	public abstract void takeTurn();
	
}