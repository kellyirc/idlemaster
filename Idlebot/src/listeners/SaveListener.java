package listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;

import data.IdleMaster;
import data.Player;

import bot.IdleBot;

public class SaveListener extends org.pircbotx.hooks.ListenerAdapter<PircBotX> {

	/*
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onConnect(org.pircbotx.hooks.events.ConnectEvent)
	 */
	@Override
	public void onConnect(ConnectEvent<PircBotX> event) throws Exception {
		IdleBot.botref.loadPlayers();
		if (IdleBot.botref.findPlayer("IdleMaster") == null) {

			IdleMaster i = new IdleMaster();

			IdleBot.botref.getPlayersRaw().add(i);

			IdleBot.botref.savePlayers(false);
			
			i.loggedIn = true;

			i.fromSerialize();
		} else {
			Player p = IdleBot.botref.findPlayer("IdleMaster");
			IdleBot.botref.handleLogin(event.getBot().getUserBot(), p);
		}
		super.onConnect(event);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onDisconnect(org.pircbotx.hooks.events.DisconnectEvent)
	 */
	@Override
	public void onDisconnect(DisconnectEvent<PircBotX> event) throws Exception {
		IdleBot.botref.savePlayers(false);
		super.onDisconnect(event);
		System.err.println("I died.");
	}

}
