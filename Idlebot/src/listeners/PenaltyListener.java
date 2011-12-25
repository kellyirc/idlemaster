package listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;

import bot.IdleBot;

public class PenaltyListener extends org.pircbotx.hooks.ListenerAdapter<PircBotX>{

	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onAction(org.pircbotx.hooks.events.ActionEvent)
	 */
	@Override
	public void onAction(ActionEvent<PircBotX> event) throws Exception {
		IdleBot.botref.penalize(event.getUser(), event.getMessage().length());
		super.onAction(event);
	}

	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onKick(org.pircbotx.hooks.events.KickEvent)
	 */
	@Override
	public void onKick(KickEvent<PircBotX> event) throws Exception {
		IdleBot.botref.penalize(event.getRecipient(), 450);
		super.onKick(event);
	}

	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onMessage(org.pircbotx.hooks.events.MessageEvent)
	 */
	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception {
		if(event.getUser().equals(event.getBot().getUserBot())) return;
		IdleBot.botref.penalize(event.getUser(), event.getMessage().length());
		super.onMessage(event);
	}

	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onNickChange(org.pircbotx.hooks.events.NickChangeEvent)
	 */
	@Override
	public void onNickChange(NickChangeEvent<PircBotX> event) throws Exception {
		//TODO make this work
		IdleBot.botref.penalize(event.getNewNick(), 40);
		super.onNickChange(event);
	}

	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onNotice(org.pircbotx.hooks.events.NoticeEvent)
	 */
	@Override
	public void onNotice(NoticeEvent<PircBotX> event) throws Exception {
		if(event.getUser().equals(event.getBot().getUserBot())) return;
		IdleBot.botref.penalize(event.getUser(), event.getMessage().length());
		super.onNotice(event);
	}

	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onPart(org.pircbotx.hooks.events.PartEvent)
	 */
	@Override
	public void onPart(PartEvent<PircBotX> event) throws Exception {
		IdleBot.botref.penalize(event.getUser(), 250);
		super.onPart(event);
	}

	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onQuit(org.pircbotx.hooks.events.QuitEvent)
	 */
	@Override
	public void onQuit(QuitEvent<PircBotX> event) throws Exception {
		IdleBot.botref.penalize(event.getUser(), 15);
		super.onQuit(event);
	}

}
