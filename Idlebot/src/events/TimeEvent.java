package events;

import data.Playable.Alignment;
import data.Player;

public class TimeEvent {

	public enum Type {
		Blessing, Fatehand, Forsaken
	};

	public TimeEvent(Player target, Type type) {

	}

	public float getModifier(Alignment align, Type type) {
		switch (type) {
		case Blessing:
			switch (align) {
			case Good:
			}
			break;
		case Fatehand:
			break;
		case Forsaken:
			break;
		default:
			return 1;
		}
	}
}
