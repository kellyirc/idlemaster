package data;

import generators.ItemGenerator;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

import org.jasypt.util.password.BasicPasswordEncryptor;

import data.Item.Type;

import bot.IdleBot;

public class Player extends Playable{
	private TreeSet<UserData> aliases;
	
	private BigInteger curTime = new BigInteger("0");
	public boolean isIgnoring;
	public transient boolean loggedIn = false;
	public UserData lastLogin;
	
	private long money = 0;
	private String password;
	
	private transient int ticks = 100;

	private transient BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

	private transient Random random = new Random();

	private Statistics stats = new Statistics();

	private BigInteger timeLeft = new BigInteger("0");
	
	public Player(String name, String password, String classtype, Alignment align) {
		super(name, classtype, align);
		this.password = passwordEncryptor.encryptPassword(password);
	}

	private void addNewItem() {
		Slot[] e = equipment.keySet().toArray(new Slot[0]);
		
		Slot slot = e[random.nextInt(e.length)];
		
		Item i = ItemGenerator.generateItem(slot);
		if(i.compareTo(equipment.get(slot)) > 0 && canEquip(slot, i)) {
			equip(slot, i);
		} else {
			sell(i);
		}
	}
	
	public BigInteger calcLevelTime(int level) {
		return new BigInteger(""+600).multiply(new BigInteger(""+Math.round(Math.pow(1.16, level)*100)));
	}

	protected boolean canEquip(Slot s, Item i) {
		if(i.getItemClass() == data.Item.ItemClass.Avatar)
			return true;
		
		Item current = equipment.get(s);
		if(current.getValue() > i.getValue()) return false;
		
		if(i.getValue() > level * ((level/7)+2)*1.5) return false;
		
		return true;
	}
	
	protected void equip(Slot s, Item i) {
		IdleBot.botref.messageChannel(this.getName() + " found "+i+" <<"+i.getValue()+">>");
		
		sell(equipment.get(s));
		
		equipment.put(s, i);
		
		if(i.getAlignment() != alignment && i.getAlignment() != null) {
			IdleBot.botref.messageChannel(this.getName() + " changed alignment to "+i.getAlignment()+"!");
			alignment = i.getAlignment();
		}
	}
	
	public void fromSerialize() {
		if(passwordEncryptor == null) passwordEncryptor = new BasicPasswordEncryptor();
		if(stats.timeSpent == null) stats.timeSpent = new BigInteger("0");
		if(equipment == null) { equipment = new HashMap<>(); generateNewEquipment(); System.out.println(this.getClass());}
		if(random == null) random = new Random();
		if(aliases == null) aliases  = new TreeSet<UserData>();
		
	}

	protected void generateNewEquipment() {
		equipment.put(Slot.Body, new Item("Tattered Shirt", 0, Type.Emotional, Item.ItemClass.Newbie));
		equipment.put(Slot.Feet, new Item("Cardboard Shoes", 0, Type.Emotional, Item.ItemClass.Newbie));
		equipment.put(Slot.Finger, new Item("Twisted Wire", 0, Type.Emotional, Item.ItemClass.Newbie));
		equipment.put(Slot.Hands, new Item("Pixelated Gloves", 0, Type.Emotional, Item.ItemClass.Newbie));
		equipment.put(Slot.Head, new Item("Miniature Top Hat", 0, Type.Emotional, Item.ItemClass.Newbie));
		equipment.put(Slot.Legs, new Item("A Leaf", 0, Type.Emotional, Item.ItemClass.Newbie));
		equipment.put(Slot.Neck, new Item("Old Brooch", 0, Type.Emotional, Item.ItemClass.Newbie));
		equipment.put(Slot.Shield, new Item("Chunk of Rust", 0, Type.Emotional, Item.ItemClass.Newbie));
		equipment.put(Slot.Weapon, new Item("Empty and Broken Ale Bottle", 0, Type.Emotional, Item.ItemClass.Newbie));
		equipment.put(Slot.Charm, new Item("Old Bracelet", 0, Type.Emotional, Item.ItemClass.Newbie));
	}
	
	public TreeSet<UserData> getAliases() { 
		return aliases;
	}

	/**
	 * @return the money
	 */
	public long getMoney() {
		return money;
	}

	public long getTimeLeft() {
		return timeLeft.subtract(curTime).longValue();
	}

	private void levelUp() {
		timeLeft = timeLeft.add(calcLevelTime(level+1));
		curTime = new BigInteger(""+curTime).subtract(timeLeft);
		IdleBot.botref.signalLevelUp(this);
		level ++;
	}

	public boolean login(String pw) {
		if(equipment.size() == 0) {
			generateNewEquipment();
		}
		return passwordEncryptor.checkPassword(pw, password);
	}

	public void modifyTime(long modification) {
		curTime = curTime.add(new BigInteger(""+modification));
		if(curTime.compareTo(timeLeft) >= 0) {
			levelUp();
		}
	}

	protected void sell(Item i) {
		int gain = 0;
		gain += i.getValue()/7;
		gain *= Math.max(level/10,1);
		if(gain > 0) {
			if(!isIgnoring && !(name.equals("IdleMaster"))) {
				IdleBot.botref.sendNotice(IdleBot.botref.getUserByPlayer(this), "You gained "+gain+" gold from selling "+i+".");
			}
			money += gain;
		} else {
			if(!isIgnoring && !(name.equals("IdleMaster"))) {
				IdleBot.botref.sendNotice(IdleBot.botref.getUserByPlayer(this), "You threw away "+i+".");
			}
		}
		
	}

	public void takeTurn() {
		modifyTime(10);
		
		stats.timeSpent = stats.timeSpent.add(new BigInteger("10"));
		
		ticks++;
		if(ticks % 1000 == 0) {
			addNewItem();
		} else if(ticks % 30 == 0){
			move();
			if(alignment == Alignment.Evil || alignment == Alignment.Neutral) move();
			if(alignment == Alignment.Evil) move();
		}
	}

	public void toNextLevel() {
		curTime = timeLeft;
	}
	
	public String toString() {
		return name+", the "+(alignment != null ? alignment+" " : "")+"level "+level + " "+classType;
	}
}
