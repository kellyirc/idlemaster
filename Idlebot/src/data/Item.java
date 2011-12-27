package data;

import org.pircbotx.Colors;

import data.Playable.Alignment;

public class Item implements Comparable<Item>{
	
	public enum Type { Magical, Physical, Spiritual, Emotional };
	public enum ItemClass { Newbie, Normal, Animal, Saint, Spiritual, Avatar, Special, Retro, Idle};
	
	private Type type;
	
	private int value;
	
	private String name;
	
	private String color;
	
	private Alignment alignment;
	
	private ItemClass iClass;
	
	public ItemClass getItemClass() {
		return iClass;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}
	
	public String toString() {
		return color + name + Colors.NORMAL;
	}

	public Item(String n, int v, Type t, ItemClass c) {
		this(n,v,t,c,null);
	}
	
	public Item(String name, int value, Type type, ItemClass cl, Alignment align) {
		this.value = value;
		this.type = type;
		this.name = name;
		this.alignment = align;
		this.iClass = cl;
		this.color = determineColor(cl);
	}
	
	public void changeClass(ItemClass cl) {
		this.color = determineColor(cl);
	}

	private String determineColor(ItemClass color2) {
		switch(color2) {
		case Normal: case Newbie: return Colors.NORMAL;
		case Animal: return Colors.BOLD + Colors.BROWN;
		case Spiritual: return Colors.BOLD + Colors.BLUE;
		case Saint: return Colors.BOLD + Colors.DARK_BLUE;
		case Avatar: return Colors.BOLD + Colors.DARK_GREEN;
		case Special: return Colors.BOLD + Colors.PURPLE;
		case Retro: return Colors.BOLD  + Colors.BLACK;
		case Idle: return Colors.BOLD  + Colors.MAGENTA;
		}
		return Colors.MAGENTA;
	}

	/**
	 * @return the alignment
	 */
	public data.Playable.Alignment getAlignment() {
		return alignment;
	}

	/**
	 * @param alignment the alignment to set
	 */
	public void setAlignment(data.Playable.Alignment alignment) {
		this.alignment = alignment;
	}

	@Override
	public int compareTo(Item o) {
		return value-o.value;
	}
}
