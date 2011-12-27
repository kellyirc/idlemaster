package generators;

import generators.Utilities.Data;

public class MonsterGenerator {
	
	private ItemGenerator itemGen = new ItemGenerator();
	
	Data[] normal;
	Data[] midboss;
	Data[] bosses;
	BossStrings[] strings;
	
	private class BossStrings {
		
		public String intro, death, kill;
		int bossPos;
		
		public BossStrings(int bossPos, String i, String d, String k) {
			this.bossPos = bossPos;
			this.intro = i;
			this.death = d;
			this.kill = k;
		}
	}
	
	public MonsterGenerator() {
		
	}
	
	public static void main(String[] args) {
		new MonsterGenerator();
	}
	
	
	
}
