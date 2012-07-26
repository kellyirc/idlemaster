package data;

import generators.ItemGenerator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import org.jasypt.util.password.BasicPasswordEncryptor;

import data.Item.Type;

import bot.IdleBot;

public class Player extends Playable {
	/**
	 * @return the items
	 */
	public ArrayList<Usable> getItems() {
		return items;
	}

	/**
	 * @param money
	 *            the money to set
	 */
	public void setMoney(long money) {
		this.money = money;
	}

	private LinkedList<UserData> aliases;

	private BigInteger curTime = new BigInteger("0");
	public boolean isIgnoring;
	public transient boolean loggedIn = false;
	public UserData lastLogin;
	private ArrayList<Usable> items = new ArrayList<>();

	private long money = 0;
	private String password;

	private transient int ticks = 100;

	private transient BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

	public Statistics stats = new Statistics();

	private BigInteger timeLeft = new BigInteger("0");

	public Player(String name, String password, String classtype,
			Alignment align) {
		super(name, classtype, align);
		this.password = passwordEncryptor.encryptPassword(password);
	}

	private void addNewItem() {
		Slot[] e = equipment.keySet().toArray(new Slot[0]);

		Slot slot = e[(int) (Math.random() * e.length)];

		Item i = ItemGenerator.generateItem(slot);
		if (i.compareTo(equipment.get(slot)) > 0 && canEquip(slot, i)) {
			equip(slot, i);
		} else {
			sell(i);
		}
	}

	public boolean tryEquip(Item i, Slot slot) {
		if (i.compareTo(equipment.get(slot)) > 0 && canEquip(slot, i)) {
			equip(slot, i);
			return true;
		} else {
			sell(i);
		}
		return false;
	}

	public BigInteger calcLevelTime() {
		return calcLevelTime(this.level);
	}

	public static BigInteger calcLevelTime(int level) {
		return BigInteger.valueOf(600).multiply(getModifierTime(level).multiply(BigInteger.valueOf(111)));
	}
	
	public static BigInteger getModifierTime(int level) {
		return BigInteger.valueOf((long) Math.pow(TIME_MULTIPLIER,level));
	}

	@Override
	public boolean canEquip(Slot s, Item i) {
		if (i.getItemClass() == data.Item.ItemClass.Avatar)
			return true;

		Item current = equipment.get(s);
		if (current.getValue() > i.getValue())
			return false;

		if (i.getValue() > (level * ((level / 7) + 2) * 1.5))
			return false;

		return true;
	}

	protected void equip(Slot s, Item i) {
		IdleBot.botref.messageChannel(this.getName() + " found " + i + " <<"
				+ i.getValue() + ">>");

		sell(equipment.get(s));

		equipment.put(s, i);

		if (i.getAlignment() != alignment && i.getAlignment() != null) {
			IdleBot.botref.messageChannel(this.getName()
					+ " changed alignment to " + i.getAlignment() + "!");
			alignment = i.getAlignment();
		}
	}

	public void fromSerialize() {
		if (passwordEncryptor == null)
			passwordEncryptor = new BasicPasswordEncryptor();
		if (stats.timeSpent == null)
			stats.timeSpent = new BigInteger("0");
		if (equipment == null) {
			equipment = new ConcurrentHashMap<>();
			generateNewEquipment();
		}
		if (aliases == null)
			aliases = new LinkedList<UserData>();
		if (items == null)
			items = new ArrayList<Usable>();
	}

	protected void generateNewEquipment() {
		equipment.put(Slot.Body, new Item("Tattered Shirt", 0, Type.Emotional,
				Item.ItemClass.Newbie));
		equipment.put(Slot.Feet, new Item("Cardboard Shoes", 0, Type.Emotional,
				Item.ItemClass.Newbie));
		equipment.put(Slot.Finger, new Item("Twisted Wire", 0, Type.Emotional,
				Item.ItemClass.Newbie));
		equipment.put(Slot.Hands, new Item("Pixelated Gloves", 0,
				Type.Emotional, Item.ItemClass.Newbie));
		equipment.put(Slot.Head, new Item("Miniature Top Hat", 0,
				Type.Emotional, Item.ItemClass.Newbie));
		equipment.put(Slot.Legs, new Item("A Leaf", 0, Type.Emotional,
				Item.ItemClass.Newbie));
		equipment.put(Slot.Neck, new Item("Old Brooch", 0, Type.Emotional,
				Item.ItemClass.Newbie));
		equipment.put(Slot.Shield, new Item("Chunk of Rust", 0, Type.Emotional,
				Item.ItemClass.Newbie));
		equipment.put(Slot.Weapon, new Item("Empty and Broken Ale Bottle", 0,
				Type.Emotional, Item.ItemClass.Newbie));
		equipment.put(Slot.Charm, new Item("Old Bracelet", 0, Type.Emotional,
				Item.ItemClass.Newbie));
	}

	public LinkedList<UserData> getAliases() {
		return aliases;
	}
	
	public void addAlias(UserData newData) {
		for(UserData u : aliases) {
			if(u.equals(newData)) return;
		}
		aliases.add(newData);
	}

	/**
	 * @return the money
	 */
	public long getMoney() {
		return money;
	}

	public BigInteger getTimeLeft() {
		return timeLeft.subtract(curTime);
	}

	public void levelUp() {
		curTime = new BigInteger("0");
		timeLeft = timeLeft.add(calcLevelTime(level + 1));
		IdleBot.botref.signalLevelUp(this);
		level++;
	}

	public boolean login(String pw) {
		if (equipment.size() == 0) {
			generateNewEquipment();
		}
		return passwordEncryptor.checkPassword(pw, password);
	}

	public void modifyTime(long modification) {
		modifyTime(new BigInteger(String.valueOf(modification)));
	}
	
	public void modifyTime(BigInteger modification) {
		curTime = curTime.add(modification);
		if (curTime.compareTo(timeLeft) >= 0) {
			levelUp();
		}
	}

	protected void sell(Item i) {
		int gain = 0;
		gain += i.getValue() / 6;
		gain *= Math.max(level / 5, 1);
		gain *= (this.stats.hasPhilStone ? 2 : 1);
		if (gain > 0) {
			if (!isIgnoring && !(name.equals("IdleMaster"))) {
				IdleBot.botref.sendNotice(IdleBot.botref.getUserByPlayer(this),
						"You gained " + gain + " gold from selling " + i + ".");
			}
			money += gain;
		} else {
			if (!isIgnoring && !(name.equals("IdleMaster"))) {
				IdleBot.botref.sendNotice(IdleBot.botref.getUserByPlayer(this),
						"You threw away " + i + ".");
			}
		}

	}

	public void takeTurn() {
		modifyTime(10);

		stats.timeSpent = stats.timeSpent.add(BigInteger.valueOf(10));

		ticks++;
		if (ticks % 20000 == 0) {
			addNewItem();
		} else if(ticks%(stats.hasWingShoes ? 250 : 85) == 0) {
			move();
			if (alignment == Alignment.Evil || alignment == Alignment.Neutral)
				move();
			if (alignment == Alignment.Evil)
				move();
		}
	}

	public void toNextLevel() {
		curTime = timeLeft;
	}

	public String toString() {
		return name + ", the " + (alignment != null ? alignment + " " : "")
				+ "level " + level + " " + classType;
	}

	public void addItem(Usable usable) {
		if (items.contains(usable)) {
			Usable old = items.get(items.indexOf(usable));
			old.addTo();
		} else
			items.add(usable);
	}

	
}
