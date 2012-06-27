package generators;

import java.util.Random;

import org.pircbotx.Colors;

import events.Battle;

public class SpellGenerator {
	
	Random r = new Random();
	
	static String[] names = { "wave", "sphere", "orb", "fist", "slash", "storm",
		"fireball", "ray", "beam", "shooter", "arrow", "soul", "knife",
		"disc", "ring", "geyser", "wall", "star", "rain", "missile",
		"pool", "pillar", "amulet", "aura", "blast", "rain", "cone", "cube", "phantom sword"
		};
	
	public class Spell {
		
		private String name;
		private int damage;
		
		public Spell(String name, int damage) {
			this.setName(name);
			this.setDamage(damage);
		}
		
		public String toString() {
			return this.name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the damage
		 */
		public int getDamage() {
			return damage;
		}

		/**
		 * @param damage the damage to set
		 */
		public void setDamage(int damage) {
			this.damage = damage;
		}
	}

	public SpellGenerator() {
		
	}
	//breath of anubis
	//judgment of the angels
	
	public Spell generateGoodSpell(int maxDamage) {
		
		if(Battle.prob(5)) {
			return new Spell(Colors.BOLD + "Judgment of the Angels" + Colors.NORMAL, maxDamage);
		}
		
		String[] prefixes = { "holy", "electric", "starry", "winding",
				"fantasmic", "piercing", "purifying", "smiting", "pure",
				"thunder", "stormy", "cloudy", "gigantic", "heavenly",
				"spiritual", "earthen", "white", "light", "magic", "sonic" };

		String[] suffixes = { "of holiness", "of electricity", "of smiting",
				"of the stars", "of the typhoon", "of the deities",
				"of angels", "of angelicity", "of divinity", "of purification",
				"of perfection", "of eternity" };

		return new Spell(genSpellName(prefixes, names, suffixes), Math.max(r.nextInt(maxDamage)+1,1));
	}
	
	public Spell generateEvilSpell(int maxDamage) {
		
		if(Battle.prob(5)) {
			return new Spell(Colors.BOLD + "Breath of Anubis" + Colors.NORMAL, maxDamage);
		}
		
		String[] prefixes = { "unholy", "flaming", "demonic", "dark", "evil",
				"unjust", "enshadowed", "envenomed", "ensorcelled", "illusory",
				"vector", "blazing", "freezing", "destroying", "obliterating",
				"menacing", "grim", "gross", "poisonous", "mean", "cruel",
				"unfair", "rude"};

		String[] suffixes = { "of the demon", "of fire", "of darkness",
				"of shadows", "of binding", "of cruelty", "of the netherworld",
				"of the deity", "of the demons" };

		return new Spell(genSpellName(prefixes, names, suffixes), r.nextInt(maxDamage)+1);
	}

	private String genSpellName(String[] prefixes, String[] names,
			String[] suffixes) {
		StringBuilder sb = new StringBuilder();

		if (Battle.prob(25)) {
			sb.append(prefixes[r.nextInt(prefixes.length)] + " ");
			sb.append(names[r.nextInt(names.length)]);
			if (Battle.prob(25)) {
				sb.append(" " + suffixes[r.nextInt(suffixes.length)]);
			}
		} else {
			sb.append(names[r.nextInt(names.length)]);
			sb.append(" " + suffixes[r.nextInt(suffixes.length)]);
		}
		return sb.toString();
	}
	
}
