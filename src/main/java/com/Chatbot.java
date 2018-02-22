package com;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.History;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.utils.IOUtils;

public class Chatbot {
	private static final boolean TRACE_MODE = false;
	static String botName = "super";





	public static void main(String[] args) {


		try {

			ServerSocket server = new ServerSocket(3345);
			Socket client = server.accept();
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			DataInputStream in = new DataInputStream(client.getInputStream());


			String resourcesPath = getResourcesPath();
			System.out.println(resourcesPath);
			MagicBooleans.trace_mode = TRACE_MODE;
			Bot bot = new Bot("super", resourcesPath);
			Chat chatSession = new Chat(bot);
			bot.brain.nodeStats();

			while (!client.isClosed()) {
				try {

				System.out.print("Human : ");

				String entry = in.readUTF();
				System.out.println("Получено от клиента сообщение - " + entry);

				if ((entry == null) || (entry.length() < 1))
					entry = MagicStrings.null_input;
				if (entry.equals("q")) {
					System.exit(0);
				} else if (entry.equals("wq")) {
					bot.writeQuit();
					System.exit(0);
				} else {
					String request = entry;
					if (MagicBooleans.trace_mode)
						System.out.println("STATE=" + request + ":THAT=" + ((History) chatSession.thatHistory.get(0)).get(0) + ":TOPIC=" + chatSession.predicates.get("topic"));
					String response = chatSession.multisentenceRespond(request);
					while (response.contains("&lt;"))
						response = response.replace("&lt;", "<");
					while (response.contains("&gt;"))
						response = response.replace("&gt;", ">");

					out.writeUTF( response);
					out.flush();

					System.out.println("Robot : " + response);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

			in.close();
			out.close();
			client.close();
			System.exit(1);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getResourcesPath() {
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		path = path.substring(0, path.length() - 2);
		System.out.println(path);
		String resourcesPath = path + File.separator + "src" + File.separator + "main" + File.separator + "resources";
		return resourcesPath;
	}

}
