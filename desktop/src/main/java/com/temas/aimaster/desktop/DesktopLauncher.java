package com.temas.aimaster.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.temas.aimaster.MainGame;

public class DesktopLauncher {
	public static void main (String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Aimaster";
		//cfg.useGL30 = true;
		if (args.length > 1) { // use -PappArgs=[697,1080] as gradle script parameter
			cfg.width = Integer.valueOf(args[0]);
			cfg.height = Integer.valueOf(args[1]);
		} else {        // 0.6453
			cfg.width = 839;
			cfg.height = 1300;
		}
		cfg.x = -1; // center
		cfg.y = -1;

		Integer playerId = args.length > 2 ? Integer.valueOf(args[2]) : null;

		new LwjglApplication(new MainGame(playerId), cfg);
	}
}
