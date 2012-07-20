package bot;

import generators.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import listeners.*;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;

import com.google.gson.Gson;

import data.IdleMaster;
import data.Monster;
import data.Playable;
import data.Player;
import data.Playable.Alignment;
import data.UserData;
import events.Event;
import events.ItemEvent;
import events.MoneyEvent;
import events.TimeEvent;

public class IdleBot extends PircBotX implements Globals {

	private class EventThread extends Thread {

		int ticks = 0;

		@Override
		public void run() {
			try{
			while (true) {

				for (Playable p : players) {
					if (p instanceof Player && !((Player) p).loggedIn)
						continue;
					p.takeTurn();
				}
				
				if(ticks++%Event.EVENT_TIME == 0) {
					if(getPlayers().size() == 0) continue;
					new Event();
				}
				
				if(ticks%(Event.EVENT_TIME*20) == 0) {
					Player pl = IdleBot.botref.getRandomPlayer();
					Playable m;
					if(Math.random() > 0.9)
						m = IdleBot.botref.getMonsterInRangeOf(pl);
					else {
						do{
							m = IdleBot.botref.getRandomPlayer();
						} while(m.equals(pl));
					}
					if(m != null) {
						pl.engage(m);
					}
				}

				try {
					sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (ticks%10000 == 0) {
					if(ticks > 100000) {
						ticks = 0;
						savePlayers(true);
					} else {
						savePlayers(false);
					}
				}
			}}catch(Exception e) {
				e.printStackTrace();
				IdleBot.botref.messageChannel("I derped: "+ e.getMessage());
			}
		}

	}

	public static IdleBot botref;
	public static final int MAX_MONSTERS = 300;
	
	public static void main(String[] args) {

		new IdleBot(Globals.Server);

	}

	private Gson gson = new Gson();

	private HashMap<String, Player> loggedIn = new HashMap<>();

	private ConcurrentLinkedQueue<Playable> players = new ConcurrentLinkedQueue<>();

	public IdleBot(String server) {
		this(server, 6667);
	}

	public IdleBot(String server, int port) {

		botref = this;

		addListeners();

		//this.setVerbose(true);

		try {

			this.setLogin("EllyBot");
			this.setName("IdleMaster");
			this.setVersion("Eclipse 3.8");
			this.setAutoNickChange(true);
			this.connect(server, port);
			this.identify("cake");
			this.setMessageDelay(750);

		} catch (NickAlreadyInUseException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		} catch (IrcException e) {
			e.printStackTrace();

		}

		this.joinChannel(Globals.Channel);
		this.setModerated(getGlobalChannel());

		try {
			Thread.sleep(9001);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (!this.getChannel(Globals.Channel).isOp(this.getUserBot())
				&& this.isConnected()) {
			messageChannel("I can't play without being an op!");
			this.quitServer("Sorry!");
			return;
		}

		this.setTopic(getGlobalChannel(), getCustomTopic());
		new Thread(new EventThread(), "Event Thread").start();
	}

	private void addListeners() {
		this.getListenerManager().addListener(new CommandListener());
		this.getListenerManager().addListener(new UserListener());
		this.getListenerManager().addListener(new PenaltyListener());
		this.getListenerManager().addListener(new SaveListener());
	}

	private void backup(File file) {
		File dir = new File("_backup/");
		File output = new File("_backup/players-"+System.currentTimeMillis()+".dat");
		try {
			if(!dir.exists()) {
				dir.mkdir();
			}
			output.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		copy(file, output);
	}

	private void copy(File file, File output) {
		try (FileInputStream from = new FileInputStream(file); FileOutputStream to = new FileOutputStream(output) ){
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createPlayer(User user, String name, String password,
			String cclass) {
		Player p = new Player(name, password, cclass, Alignment.Neutral);

		players.add(p);

		savePlayers(false);

		p.fromSerialize();

		messageChannel(user.getNick() + " has registered " + name + "!");

	}

	public boolean findLoggedInUser(String nick) {
		return loggedIn.containsKey(nick);
	}

	public Playable findPlayableByCoordinates(Playable root) {

		for (Playable p : players) {
			if (p.equals(root))
				continue;
			if (p instanceof Player && !((Player) p).loggedIn)
				continue;
			if (p.getX() == root.getX() && p.getY() == root.getY()) {
				return p;
			}
		}
		return null;

	}

	public Player findPlayer(String name) {
		for (Playable p : players) {
			if (p instanceof Player && p.getName().equals(name)) {
				return (Player) p;
			}
		}
		return null;
	}

	public void generateMonsters() {
		for(int i=0; i<MAX_MONSTERS; i++) {
			players.add(MonsterGenerator.generateMonster(null, -1));
		}
		
	}

	public String getCustomTopic() {
		return "Welcome to Word Soup v" + Globals.Version
				+ ", the best idling game ever! http://seiyria.com/idle.php";
	}

	public org.pircbotx.Channel getGlobalChannel() {
		return this.getChannel(Globals.Channel);
	}

	public LinkedList<Player> getOnlinePlayers() {
		LinkedList<Player> ll = new LinkedList<>();
		for (Playable p : players) {
			if (p instanceof Player && ((Player) p).loggedIn && !p.getName().equals("IdleMaster"))
				ll.add((Player) p);
		}
		return ll;
	}

	public Player getPlayerByUser(String user) {
		return loggedIn.get(user);
	}

	public Player getPlayerByUser(User user) {
		return getPlayerByUser(user.getNick());
	}
	
	public Player getPlayerByName(String name) {
		for(Player p : getPlayers()){
			if(p.getName().toLowerCase().equals(name.toLowerCase())) return p;
		}
		return null;
	}

	public LinkedList<Player> getPlayers() {
		LinkedList<Player> ll = new LinkedList<>();
		for (Playable p : players) {
			if (p instanceof Player)
				ll.add((Player) p);
		}
		return ll;
	}

	public ConcurrentLinkedQueue<Playable> getPlayersRaw() {
		return players;
	}

	public Player getRandomPlayer() {
		if(getOnlinePlayers().size() == 0) return null;
		return getOnlinePlayers().toArray(new Player[0])[(int) (Math.random() *( getOnlinePlayers().size()-1))];
	}

	public String getUserByPlayer(Player player) {
		for (String s : loggedIn.keySet()) {
			if (loggedIn.get(s).equals(player)) {
				return s;
			}
		}
		return null;
	}

	public void handleLogin(User user, Player player) {
		if (player.loggedIn)
			return;
		player.loggedIn = true;
		player.lastLogin = new UserData(user.generateSnapshot());
		loggedIn.put(user.getNick(), player);
		player.getAliases().add(new UserData(user.generateSnapshot()));
		messageChannel(user.getNick() + " has joined Idletopia as " + player
				+ ".");
		this.voice(getGlobalChannel(), user);

	}

	public void handleLogout(Player p) {
		p.loggedIn = false;
		loggedIn.remove(getUserByPlayer(p));
		messageChannel(p.getName() + " has abandoned Idletopia.");
	}
	
	public void handleLogout(User user) {
		if (user == null)
			return;
		Player p = IdleBot.botref.getPlayerByUser(user);
		if (p == null)
			return;
		handleLogout(p);
	}

	public void loadPlayers() {
		File f = new File("players.dat");
		if (!f.exists())
			return;

		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new DataInputStream(
					new FileInputStream("players.dat"))));

			String strLine;

			while ((strLine = br.readLine()) != null) {
				if (strLine.equals(""))
					continue;
				if (strLine.contains("IdleMaster")
						&& strLine
								.contains("\"password\":\"EOyfDm54PVWZAY0jH292yhlzTUYjGYmp\"")) {
					IdleMaster p = gson.fromJson(strLine, IdleMaster.class);
					p.fromSerialize();

					players.add(p);
				} else {
					Player p = gson.fromJson(strLine, Player.class);
					if(p.getLevel() == 0) continue;
					p.fromSerialize();
					if(players.contains(p)) continue;
					
					boolean dupPlayer = false;
					for(Playable x : players) {
						if(x.getName().equals(p.getName())) {
							dupPlayer = true; break;
						}
					}
					if(!dupPlayer)
						players.add(p);
				}

			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void messageChannel(String s) {
		try {
			sendMessage(getGlobalChannel(), Colors.NORMAL+s);
		} catch(Exception e) {
			
		}
	}
	
	public void messageChannel(String s, Player p) {
		try {
			sendMessage(getGlobalChannel(), Event.replaceGender(s,p));
		} catch(Exception e) {
			
		}
	}

	public void movePlayerToNewNick(String oldNick, String newNick) {
		Player p = loggedIn.get(oldNick);
		if (p == null)
			return;
		loggedIn.remove(oldNick);
		loggedIn.put(newNick, p);
	}
	
	public static String ms2dd(BigInteger b) {
		int weeks=0, days=0, hours=0, minutes=0, seconds=0;
		
		while(b.compareTo(BigInteger.valueOf(86400000)) > 0) {
			days++;
			b = b.subtract(BigInteger.valueOf(86400000));
		}
		
		while(days > 7) {
			weeks++;
			days -= 7;
		}
		
		while(b.compareTo(BigInteger.valueOf(3600000)) > 0) {
			hours++;
			b = b.subtract(BigInteger.valueOf(3600000));
		}
		
		while(b.compareTo(BigInteger.valueOf(60000)) > 0) {
			minutes++;
			b = b.subtract(BigInteger.valueOf(60000));
		}
		
		while(b.compareTo(BigInteger.valueOf(1000)) > 0) {
			seconds++;
			b = b.subtract(BigInteger.valueOf(1000));
		}
		
		StringBuffer sb = new StringBuffer();
		if(weeks > 0) sb.append(weeks + "w ");
		if(days > 0) sb.append(days + "d ");
		if(hours > 0) sb.append(hours + "h ");
		if(minutes > 0) sb.append(minutes + "m ");
		if(seconds > 0) sb.append(seconds + "s");
		
		return sb.toString();
	}

	public void penalize(String user, int length) {
		Player p = this.getPlayerByUser(user);
		if (p == null)
			return;
		BigInteger pentime = (new BigInteger(String.valueOf(1000*length)).multiply(Player.getModifierTime(p.getLevel())));
		messageChannel(p.getName() + " was penalized " + ms2dd(pentime));
		p.modifyTime(pentime.negate());
	}

	public void penalize(User user, int length) {
		penalize(user.getNick(), length);
	}

	public void reload() {
		messageChannel("Reloading my generators...");
		try {
			MonsterGenerator.initialize();
			ItemGenerator.initialize();
			ItemEvent.initialize();
			MoneyEvent.initialize();
			TimeEvent.initialize();
		} catch(Exception e) {
			messageChannel("Reload failed: "+e.getMessage());
		}
		messageChannel("Reloaded.");
	}

	public void savePlayers(boolean backup) {
		File file = new File("players.dat");
		
		if (backup && file.exists()) {
			backup(file);
		}

		try {

			File temp = new File("players.dat.swap");
			
			BufferedWriter out = new BufferedWriter(new FileWriter(temp));

	        signalStart();
	        
	        TreeSet<Player> tempList = new TreeSet<>(new Comparator<Player>(){

				@Override
				public int compare(Player o1, Player o2) {
					return o2.calcTotal(null) - o1.calcTotal(null);
				}});
	        
	        tempList.addAll(getPlayers());
	        
			for (Player p : tempList) {
				String gsonstr = gson.toJson(p);
				out.write(gsonstr + "\n\n");
				
				sendData(gsonstr);
			}
			
			signalEnd();
			
			out.close();
			
			file.delete();
			temp.renameTo(file);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void sendData(String gsonstr) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://seiyria.com/update.php");
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();   
		nameValuePairs.add(new BasicNameValuePair("msg", gsonstr+"\n\n"));
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		httpclient.execute(post);
		httpclient.getConnectionManager().shutdown();
	}

	private void signalEnd() throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://seiyria.com/update.php?end=1");
		httpclient.execute(httpget);
		httpclient.getConnectionManager().shutdown();
	}

	public void signalLevelUp(Player player) {

		messageChannel(player.getName() + " is now level "
				+ (player.getLevel() + 1) + "! " + ms2dd(player.getTimeLeft())
				+ " to next level.");
		savePlayers(false);

	}

	private void signalStart() throws IOException, ClientProtocolException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://seiyria.com/update.php?start=1");
		httpclient.execute(httpget);
		httpclient.getConnectionManager().shutdown();
	}
	
	public Monster getRandomMonster() {
		Monster m = null;
		Playable[] p = players.toArray(new Playable[0]);
		int tries = 0;
		
		do {
			
			Playable choice = p[(int) (Math.random() * (p.length-1))];
			
			if( choice instanceof Monster) {
				m = (Monster) choice;
			}
			
		} while (tries++<100 && m == null);
		
		return m;
	}
	
	public Monster getMonsterInRangeOf(Playable p) {
		Monster m = null;
		int tries = 0;
		do {
			m = MonsterGenerator.generateMonster(null, -1);
			if(p.canBattle(m)) return m;
			else m.die(null);
		} while (tries++<100);
		
		return null;
	}
}
