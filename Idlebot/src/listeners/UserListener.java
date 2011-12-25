package listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;

import data.Player;
import data.UserData;

import bot.IdleBot;

public class UserListener extends org.pircbotx.hooks.ListenerAdapter<PircBotX> {

	/*
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onJoin(org.pircbotx.hooks.events.JoinEvent)
	 */
	@Override
	public void onJoin(JoinEvent<PircBotX> event) throws Exception {
		super.onJoin(event);
		Thread.sleep(6000);
		if (event.getUser().equals(event.getBot().getUserBot())) {
			for (User u : event.getChannel().getUsers()) {
				if(!u.equals(event.getBot().getUserBot())) tryToMatch(u);
			}
		} else {
			tryToMatch(event.getUser());
		}
	}

	private void tryToMatch(User u) {
		for (Player p : IdleBot.botref.getPlayers()) {
			for (UserData alias : p.getAliases()) {
				if ((alias.getLogin().equals(u.getLogin()) && alias.getRealName().equals(u.getRealName()) || alias
								.getHostMask().equals(u.getHostmask()))) {
					IdleBot.botref.handleLogin(u, p);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onKick(org.pircbotx.hooks.events.KickEvent)
	 */
	@Override
	public void onKick(KickEvent<PircBotX> event) throws Exception {
		super.onKick(event);
		IdleBot.botref.handleLogout(event.getRecipient());
	}

	/*
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onPart(org.pircbotx.hooks.events.PartEvent)
	 */
	@Override
	public void onPart(PartEvent<PircBotX> event) throws Exception {
		super.onPart(event);
		IdleBot.botref.handleLogout(event.getUser());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.pircbotx.hooks.ListenerAdapter#onNickChange(org.pircbotx.hooks.events.NickChangeEvent)
	 */
	@Override
	public void onNickChange(NickChangeEvent<PircBotX> event) throws Exception {

		super.onNickChange(event);

		IdleBot.botref.movePlayerToNewNick(event.getOldNick(),
				event.getNewNick());
	}

	/*
	 * (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onQuit(org.pircbotx.hooks.events.QuitEvent)
	 */
	@Override
	public void onQuit(QuitEvent<PircBotX> event) throws Exception {
		super.onQuit(event);
		IdleBot.botref.handleLogout(event.getUser());
	}

}
