//음악 재생 클래스 millions

package com.tetris.main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class Music extends Thread {

	private Player player;
	private boolean isloop;
	private File file;
	private FileInputStream fis;
	private BufferedInputStream bis;
	
	public Music(String name , boolean isloop) {
		try {
			this.isloop = isloop;
			file = new File(TetrisMain.class.getResource("../../../Music/" + name).toURI());
		fis = new FileInputStream(file);
		bis = new BufferedInputStream(fis);
		player = new Player(bis);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}return;
	}
	public void close() {
		this.isloop = false;
		this.player.close();
		this.interrupt();
	}
	
	
	@Override
	public void run() {
		try {
		 { do {
				player.play();
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				player = new Player(bis);

			}while(isloop);
		 }
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	

	
}
