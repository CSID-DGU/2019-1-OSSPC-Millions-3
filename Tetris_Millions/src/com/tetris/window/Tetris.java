package com.tetris.window;

import static com.tetris.window.Sound.GameMusic;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.tetris.network.GameClient;
import com.tetris.network.GameServer;

public class Tetris extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private GameServer server;
	private GameClient client;
	private TetrisBoard board = new TetrisBoard(this,client);
	private JMenuItem itemServerStart = new JMenuItem("������ �����ϱ�");
	private JMenuItem itemClientStart = new JMenuItem("Ŭ���̾�Ʈ�� �����ϱ�");
	
	private JMenuItem itemKeySet = new JMenuItem("�����ϱ�");	//"Ű ����" ���� menu�� "�����ϱ�" ����.(millions)
	
	private boolean isNetwork;
	private boolean isServer;

	

	public Tetris() {
		JMenuBar mnBar = new JMenuBar();
		JMenu mnGame = new JMenu("�����ϱ�");
		JMenu stGame = new JMenu("Ű ����");	 //��ܿ� "Ű ����" menu ����. (millions)
		
		mnGame.add(itemServerStart);
		mnGame.add(itemClientStart);
		
		stGame.add(itemKeySet);				//millions
		
		mnBar.add(mnGame);
		mnBar.add(stGame);					//millions
		
		this.setJMenuBar(mnBar);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.getContentPane().add(board);
		
		this.setResizable(false);
		this.pack();
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((size.width-this.getWidth())/2,(size.height-this.getHeight())/2);
		this.setVisible(true);
		
		itemServerStart.addActionListener(this);
		itemClientStart.addActionListener(this);
		
		itemKeySet.addActionListener(this);				//millions
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if(client!=null ){
					
					if(isNetwork){
						client.closeNetwork(isServer);
					}
				}else{
					System.exit(0);
				}
				
			}
			
		});
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String ip=null;
		int port=0;
		String nickName=null;
		if(e.getSource() == itemServerStart){
			
			String sp = JOptionPane.showInputDialog("port��ȣ�� �Է����ּ���","9500");
			if(sp!=null && !sp.equals(""))port = Integer.parseInt(sp);
			nickName = JOptionPane.showInputDialog("�г����� �Է����ּ���","�̸�����");
			
			if(port!=0){
				if(server == null) server = new GameServer(port);
				server.startServer();
				try {ip = InetAddress.getLocalHost().getHostAddress();
				} catch (UnknownHostException e1) {e1.printStackTrace();}
				if(ip!=null){
					client = new GameClient(this,ip,port,nickName);
					if(client.start()){
						itemServerStart.setEnabled(false);
						itemClientStart.setEnabled(false);
						board.setClient(client);
						board.getBtnStart().setEnabled(true);
						board.startNetworking(ip, port, nickName);
						isNetwork = true;
						isServer = true;
					}
				}
			}
		}else if(e.getSource() == itemClientStart){
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			
			ip = JOptionPane.showInputDialog("IP�� �Է����ּ���.",ip);
			String sp = JOptionPane.showInputDialog("port��ȣ�� �Է����ּ���","9500");
			if(sp!=null && !sp.equals(""))port = Integer.parseInt(sp);
			nickName = JOptionPane.showInputDialog("�г����� �Է����ּ���","�̸�����");

		
			if(ip!=null){														// "�����ϱ�"�� ������ ����Ǵ� program(millions)
				client = new GameClient(this,ip,port,nickName);
				if(client.start()){
					itemServerStart.setEnabled(false);
					itemClientStart.setEnabled(false);
					board.setClient(client);
					board.startNetworking(ip, port, nickName);
					isNetwork = true;
				}
			}		}else if(e.getSource() == itemKeySet) {
				  Button start = new Button();
				  start.FrameShow();
		}
	}

	public void closeNetwork(){
		if(GameMusic != null && GameMusic.isAlive()) {
			GameMusic.close();
		isNetwork = false;
		client = null;
		itemServerStart.setEnabled(true);
		itemClientStart.setEnabled(true);
		board.setPlay(false);
		board.setClient(null);
		}else {
			isNetwork = false;
			client = null;
			itemServerStart.setEnabled(true);
			itemClientStart.setEnabled(true);
			board.setPlay(false);
			board.setClient(null);
		}
	}

	public JMenuItem getItemServerStart() {return itemServerStart;}
	public JMenuItem getItemClientStart() {return itemClientStart;}
	public TetrisBoard getBoard(){return board;}
	public void gameStart(int speed){board.gameStart(speed);}
	public boolean isNetwork() {return isNetwork;}
	public void setNetwork(boolean isNetwork) {this.isNetwork = isNetwork;}
	public void printSystemMessage(String msg){board.printSystemMessage(msg);}
	public void printMessage(String msg){board.printMessage(msg);}
	public boolean isServer() {return isServer;}
	public void setServer(boolean isServer) {this.isServer = isServer;}

	public void changeSpeed(Integer speed) {board.changeSpeed(speed);}
}
