package data;

public class Monster extends Playable {

	private transient int ticks = 0;
	public String dieSpeech, killSpeech, introSpeech;
	private int bonus;
	
	public Monster(String name2, String classtype2, Alignment align) {
		super(name2, classtype2, align);
	}
//super mega uber goblin
//classes based on scale
//introduction speech on battles
//death speech, kill speech
//entry into world speech
	@Override
	public void takeTurn() {
		if(ticks++ > 100) {
			ticks = 0;
			move();
		}
	}
	
	public void addToBonus(int i) {
		bonus += i;
	}
	
}
