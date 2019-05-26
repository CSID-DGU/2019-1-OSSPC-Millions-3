package com.tetris.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;	// millions
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.ImageIcon;		// millions
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;		//millions
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.tetris.classes.Block;
import com.tetris.classes.TetrisBlock;
import com.tetris.controller.TetrisController;

import com.tetris.main.Music;	// millions
import com.tetris.main.TetrisMain;
import com.tetris.network.DataShip;
import com.tetris.network.GameClient;
import com.tetris.network.GameServer;
import com.tetris.shape.CenterUp;
import com.tetris.shape.LeftTwoUp;
import com.tetris.shape.LeftUp;
import com.tetris.shape.Line;
import com.tetris.shape.Nemo;
import com.tetris.shape.RightTwoUp;
import com.tetris.shape.RightUp;
import com.tetris.window.Button;

public class TetrisBoard extends JPanel implements Runnable, KeyListener, MouseListener, ActionListener {
	private static final long serialVersionUID = 1L;

	private Tetris tetris;
	private GameClient client;

	public static final int BLOCK_SIZE = 20;
	public static final int BOARD_X = 140;
	public static final int BOARD_Y = 50;
	private static int minX = 1, minY = 0, maxX = 10, maxY = 21, down = 50, up = 0;
	private static final int MESSAGE_WIDTH = BLOCK_SIZE * 7;
	private static final int MESSAGE_HEIGHT = BLOCK_SIZE * 6;
	public static final int PANEL_WIDTH = 2 * ( maxX * BLOCK_SIZE + MESSAGE_WIDTH + BOARD_X) + 20;
	public static final int PANEL_HEIGHT = maxY * BLOCK_SIZE + MESSAGE_HEIGHT + BOARD_Y;

	private SystemMessageArea systemMsg = new SystemMessageArea(BLOCK_SIZE * 1, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE * 7,BLOCK_SIZE * 5, BLOCK_SIZE * 12);
	private MessageArea messageArea = new MessageArea(this,0, PANEL_HEIGHT - MESSAGE_HEIGHT, PANEL_WIDTH-BLOCK_SIZE*7, MESSAGE_HEIGHT);
	private JButton btnStart = new JButton("�떆�옉�븯湲�");
	private JButton btnExit = new JButton("�굹媛�湲�");
	private JCheckBox checkGhost = new JCheckBox("怨좎뒪�듃紐⑤뱶", true);
	private JCheckBox checkGrid = new JCheckBox("寃⑹옄 �몴�떆", true);
	
	
	private JCheckBox checkEffect = new JCheckBox("�슚怨쇱쓬", true);					// �슚怨쇱쓬 checkbox (millions)
	private JCheckBox checkBGM = new JCheckBox("諛곌꼍�쓬�븙", true);						// 諛곌꼍�쓬�븙 checkbox (millions)
	
	
	private Integer[] lv = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
	private JComboBox<Integer> comboSpeed = new JComboBox<Integer>(lv);

	private String ip;
	private int port;
	private String nickName;
	private Thread th;
	private ArrayList<Block> blockList;
	private ArrayList<TetrisBlock> nextBlocks;
	private TetrisBlock shap;
	private TetrisBlock ghost;
	private TetrisBlock hold;
	private Block[][] map;
	private TetrisController controller;
	private TetrisController controllerGhost;

	private boolean isPlay = false;
	private boolean isHold = false;
	private boolean usingGhost = true;
	private boolean usingGrid = true;
	
	
	private boolean usingEffect = true;							// �슚怨쇱쓬 (millions)
	private boolean usingBGM = true;							// 諛곌꼍�쓬�븙 (millions)
	
	
	private int removeLineCount = 0;
	private int removeLineCombo = 0;
	
	//Music 媛앹껜 millions
	public Music GameMusic; // 寃뚯엫�븯�뒗 �룄以묒쓽 �쓬�븙
	public Music GameEndSound;// 寃뚯엫 醫낅즺 �떆 �슚怨쇱쓬


	public TetrisBoard(Tetris tetris, GameClient client) {
		this.tetris = tetris;
		this.client = client;
		this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));// 湲곕낯�겕湲�
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.setLayout(null);
		this.setFocusable(true);

		btnStart.setBounds(PANEL_WIDTH - BLOCK_SIZE * 7, PANEL_HEIGHT - messageArea.getHeight(), BLOCK_SIZE * 7, messageArea.getHeight() / 2);
		btnStart.setFocusable(false);
		btnStart.setEnabled(false);
		btnStart.addActionListener(this);
		
		btnStart.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				Music MousePressedSound = new Music("Start.mp3", false);
				MousePressedSound.start();
			}
			public void mouseEntered(MouseEvent e) {
				Music MousePressedSound = new Music("BlockMoveSound.mp3", false);
				MousePressedSound.start();
			}
		
			
			
		}); // 踰꾪듉 �슚怨쇱쓬 millions
		
		btnExit.setBounds(PANEL_WIDTH - BLOCK_SIZE * 7, PANEL_HEIGHT - messageArea.getHeight() / 2, BLOCK_SIZE * 7,
				messageArea.getHeight() / 2);
		btnExit.setFocusable(false);
		btnExit.addActionListener(this);

		btnExit.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				Music MousePressedSound = new Music("Exit.mp3", false);
				MousePressedSound.start();
			}
			
			public void mouseEntered(MouseEvent e) {
				Music MousePressedSound = new Music("BlockMoveSound.mp3", false);
				MousePressedSound.start();
			}
			
		});
		
		// 踰꾪듉 �슚怨쇱쓬 millions
		
		checkGhost.setBounds(PANEL_WIDTH - BLOCK_SIZE * 12 + 35, 5, 95, 20);		//怨좎뒪�듃紐⑤뱶 checkbox �쇊履쎌쑝濡� �씠�룞.(millions)
		checkGhost.setBackground(new Color(255, 255, 255));
		checkGhost.setForeground(Color.GRAY);
		checkGhost.setFont(new Font("援대┝", Font.BOLD, 13));
		checkGhost.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				usingGhost = checkGhost.isSelected();
				TetrisBoard.this.setRequestFocusEnabled(true);
				TetrisBoard.this.repaint();
			}
		});
		checkGrid.setBounds(PANEL_WIDTH - BLOCK_SIZE * 12 + 35, 25, 95, 20);		//寃⑹옄紐⑤뱶 checkbox �쇊履쎌쑝濡� �씠�룞.(millions)
		checkGrid.setBackground(new Color(255, 255, 255));
		checkGrid.setForeground(Color.GRAY);
		checkGrid.setFont(new Font("援대┝", Font.BOLD, 13));
		checkGrid.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				usingGrid = checkGrid.isSelected();
				TetrisBoard.this.setRequestFocusEnabled(true);
				TetrisBoard.this.repaint();
			}
		});

<<<<<<< HEAD
<<<<<<< HEAD
		checkEffect.setBounds(PANEL_WIDTH - BLOCK_SIZE * 7 + 35, 5, 95, 20);		//ȿ���� checkbox ��ġ �� ������(millions)
		checkEffect.setBackground(new Color(255, 255, 255));
		checkEffect.setForeground(Color.GRAY);
		checkEffect.setFont(new Font("����", Font.BOLD, 13));
=======
		checkEffect.setBounds(PANEL_WIDTH - BLOCK_SIZE * 7 + 35, 5, 95, 20);		//�슚怨쇱쓬 checkbox �쐞移� 諛� �뵒�옄�씤(millions)
		checkEffect.setBackground(new Color(255, 255, 255));
		checkEffect.setForeground(Color.GRAY);
		checkEffect.setFont(new Font("援대┝", Font.BOLD, 13));
>>>>>>> 3748fd147596c04e6ef70dfe4e2546ba96015336
=======
		checkEffect.setBounds(PANEL_WIDTH - BLOCK_SIZE * 7 + 35, 5, 95, 20);		//�슚怨쇱쓬 checkbox �쐞移� 諛� �뵒�옄�씤(millions)
		checkEffect.setBackground(new Color(255, 255, 255));
		checkEffect.setForeground(Color.GRAY);
		checkEffect.setFont(new Font("援대┝", Font.BOLD, 13));
>>>>>>> origin
		checkEffect.setRequestFocusEnabled(false);
		checkEffect.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (checkEffect.isSelected() == true) {
					usingEffect = true;
				} else {
					usingEffect = false;
				}		
				TetrisBoard.this.setRequestFocusEnabled(true);
				TetrisBoard.this.repaint();
			}
		});
		
		checkBGM.setBounds(PANEL_WIDTH - BLOCK_SIZE * 7 + 35, 25, 95, 20);			//諛곌꼍�쓬�븙 checkbox �쐞移� 諛� �뵒�옄�씤(millions)
		checkBGM.setBackground(new Color(255, 255, 255));
		checkBGM.setForeground(Color.GRAY);
<<<<<<< HEAD
<<<<<<< HEAD
		checkBGM.setFont(new Font("����", Font.BOLD, 13));
=======
		checkBGM.setFont(new Font("援대┝", Font.BOLD, 13));
>>>>>>> 3748fd147596c04e6ef70dfe4e2546ba96015336
=======
		checkBGM.setFont(new Font("援대┝", Font.BOLD, 13));
>>>>>>> origin
		checkBGM.setRequestFocusEnabled(false);
		checkBGM.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (checkBGM.isSelected() == true) {
					usingBGM = true;
<<<<<<< HEAD
<<<<<<< HEAD
				} else {
					usingBGM = false;
=======
=======
>>>>>>> origin
					if(GameMusic.isAlive() && GameMusic != null) {
						GameMusic.close();
						GameMusic = new Music("GameMusic.mp3", true );
						GameMusic.start();
					}else {
						GameMusic = new Music("GameMusic.mp3", true );
						GameMusic.start();
					}
				} else {
					usingBGM = false;
					if(GameMusic != null && GameMusic.isAlive()) {
						GameMusic.close();
					}
					
<<<<<<< HEAD
>>>>>>> 3748fd147596c04e6ef70dfe4e2546ba96015336
=======
>>>>>>> origin
				}	
				TetrisBoard.this.setRequestFocusEnabled(true);
				TetrisBoard.this.repaint();
			}
		});
		
<<<<<<< HEAD
<<<<<<< HEAD
		comboSpeed.setBounds(PANEL_WIDTH - BLOCK_SIZE * 13, 5, 45, 20); 			// �ӵ� ���� ǥ�� �������� �̵�.(millions)
=======
		comboSpeed.setBounds(PANEL_WIDTH - BLOCK_SIZE * 13, 5, 45, 20); 			// �냽�룄 �닽�옄 �몴�떆 �쇊履쎌쑝濡� �씠�룞.(millions)
>>>>>>> 3748fd147596c04e6ef70dfe4e2546ba96015336
=======
		comboSpeed.setBounds(PANEL_WIDTH - BLOCK_SIZE * 13, 5, 45, 20); 			// �냽�룄 �닽�옄 �몴�떆 �쇊履쎌쑝濡� �씠�룞.(millions)
>>>>>>> origin
		this.add(comboSpeed);

		this.add(systemMsg);
		this.add(messageArea);
		this.add(btnStart);
		this.add(btnExit);
		this.add(checkGhost);
		this.add(checkGrid);
		
		this.add(checkEffect);														//�슚怨쇱쓬(millions)
		this.add(checkBGM);															//諛곌꼍�쓬�븙(millions)
	}

	public void startNetworking(String ip, int port, String nickName) {
		this.ip = ip;
		this.port = port;
		this.nickName = nickName;
		this.repaint();
	}

	/**
	 * TODO : 寃뚯엫�떆�옉 寃뚯엫�쓣 �떆�옉�븳�떎.
	 */

	public void gameStart(int speed) {
		comboSpeed.setSelectedItem(new Integer(speed));
		// �룎怨� �엳�쓣 �뒪�젅�뱶瑜� �젙吏��떆�궓�떎.
		if (th != null) {
			try {
				isPlay = false;
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// 留듭뀑�똿
		map = new Block[maxY][maxX];
		blockList = new ArrayList<Block>();
		nextBlocks = new ArrayList<TetrisBlock>();

		// �룄�삎�뀑�똿
		shap = getRandomTetrisBlock();
		ghost = getBlockClone(shap, true);
		hold = null;
		isHold = false;
		controller = new TetrisController(shap, maxX - 1, maxY - 1, map);
		controllerGhost = new TetrisController(ghost, maxX - 1, maxY - 1, map);
		this.showGhost();
		for (int i = 0; i < 5; i++) {
			nextBlocks.add(getRandomTetrisBlock());
		}

		// �뒪�젅�뱶 �뀑�똿
		isPlay = true;
		th = new Thread(this);
		th.start();
	}

	// TODO : paint
	@Override
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, this.getWidth(), this.getHeight() + 1);

		g.setColor(new Color(255,255,255));
		g.fillRect(0, 0, 2*(2*BOARD_X+maxX*BLOCK_SIZE), BOARD_Y);

		g.setColor(new Color(255, 255, 255));
		g.fillRect(0, BOARD_Y, 2*(2*BOARD_X+maxX*BLOCK_SIZE), maxY*BLOCK_SIZE);
		g.setColor(Color.GRAY);

		// IP 異쒕젰
		g.drawString("ip : " + ip + "     port : " + port, 20, 20);

		// NickName 異쒕젰
		g.drawString("�땳�꽕�엫 : " + nickName, 20, 40);

		// �냽�룄
		Font font= g.getFont();
		g.setFont(new Font("굴림", Font.BOLD,13));
		g.drawString("속도", PANEL_WIDTH - BLOCK_SIZE*15, 20);
		g.setFont(font);

		g.setColor(Color.BLACK);
		g.fillRect(BOARD_X + BLOCK_SIZE*minX, BOARD_Y, maxX*BLOCK_SIZE+1, maxY*BLOCK_SIZE+1);
		g.fillRect(BLOCK_SIZE*minX ,BOARD_Y + BLOCK_SIZE, BLOCK_SIZE*5,BLOCK_SIZE*5);
		g.fillRect(BOARD_X + BLOCK_SIZE*minX + (maxX+1)*BLOCK_SIZE+1,BOARD_Y + BLOCK_SIZE, BLOCK_SIZE*5,BLOCK_SIZE*5);
		g.fillRect(BOARD_X + BLOCK_SIZE*minX + (maxX+1)*BLOCK_SIZE+1,BOARD_Y + BLOCK_SIZE + BLOCK_SIZE*7, BLOCK_SIZE*5,BLOCK_SIZE*12);
		
		//HOLD  NEXT 출력
		g.setFont(new Font(font.getFontName(),font.getStyle(),20));
		g.drawString("H O L D", BLOCK_SIZE + 12, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE*5 + 20);
		g.drawString("N E X T", BOARD_X + BLOCK_SIZE + (maxX+1)*BLOCK_SIZE+1 + 12, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE*5 + 20);
		g.setFont(font);
		
		//그리드 표시
		if(usingGrid){
			g.setColor(Color.darkGray);
			for(int i=1;i<maxY;i++) g.drawLine(BOARD_X + BLOCK_SIZE*minX, BOARD_Y+BLOCK_SIZE*(i+minY), BOARD_X + (maxX+minX)*BLOCK_SIZE, BOARD_Y+BLOCK_SIZE*(i+minY));
			for(int i=1;i<maxX;i++) g.drawLine(BOARD_X + BLOCK_SIZE*(i+minX), BOARD_Y+BLOCK_SIZE*minY, BOARD_X + BLOCK_SIZE*(i+minX), BOARD_Y+BLOCK_SIZE*(minY+maxY));
			for(int i=1;i<5;i++) g.drawLine(BLOCK_SIZE*minX ,BOARD_Y + BLOCK_SIZE*(i+1), BLOCK_SIZE*(minX+5)-1,BOARD_Y + BLOCK_SIZE*(i+1));
			for(int i=1;i<5;i++) g.drawLine(BLOCK_SIZE*(minY+i+1) ,BOARD_Y + BLOCK_SIZE, BLOCK_SIZE*(minY+i+1),BOARD_Y + BLOCK_SIZE*(minY+6)-1);
			for(int i=1;i<5;i++) g.drawLine(BOARD_X + BLOCK_SIZE*minX + (maxX+1)*BLOCK_SIZE+1, BOARD_Y + BLOCK_SIZE*(i+1), BOARD_X + BLOCK_SIZE*minX + (maxX+1)*BLOCK_SIZE+BLOCK_SIZE*5,BOARD_Y + BLOCK_SIZE*(i+1));
			for(int i=1;i<5;i++) g.drawLine(BOARD_X + BLOCK_SIZE*minX + (maxX+1+i)*BLOCK_SIZE+1, BOARD_Y + BLOCK_SIZE, BOARD_X + BLOCK_SIZE*minX + BLOCK_SIZE+BLOCK_SIZE*(10+i)+1,BOARD_Y + BLOCK_SIZE*6-1);	
		}
		g.drawLine(this.getWidth()/2, BOARD_Y, this.getWidth()/2, BOARD_Y+maxY*BLOCK_SIZE);
		
		// <<2p �솕硫�>>
				// 源뚮쭔 諛곌꼍 遺�遺�
				g.setColor(Color.BLACK);
				// 媛��슫�뜲
				g.fillRect(3*BOARD_X+maxX*BLOCK_SIZE, BOARD_Y, maxX*BLOCK_SIZE, maxY*BLOCK_SIZE);
				// �쇊履�
				g.fillRect(2*BOARD_X+maxX*BLOCK_SIZE+BLOCK_SIZE*minX,BOARD_Y + BLOCK_SIZE, BLOCK_SIZE*5,BLOCK_SIZE*5);
				// �삤瑜몄そ �쐞
				g.fillRect(3*BOARD_X + 2*maxX*BLOCK_SIZE+BLOCK_SIZE*minX, BOARD_Y + BLOCK_SIZE, BLOCK_SIZE*5,BLOCK_SIZE*5);
				// �삤瑜몄そ �븘�옒
				g.fillRect(3*BOARD_X + 2*maxX*BLOCK_SIZE+BLOCK_SIZE*minX, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE*7, BLOCK_SIZE*5,BLOCK_SIZE*12);
				
				//HOLD  NEXT 異쒕젰
				g.setFont(new Font(font.getFontName(),font.getStyle(),20));
				g.setColor(Color.BLACK);
				g.drawString("H O L D", 2*BOARD_X+maxX*BLOCK_SIZE+BLOCK_SIZE*minX + 15, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE*5 + 20);
				g.drawString("N E X T", 3*BOARD_X + 2*maxX*BLOCK_SIZE+ BLOCK_SIZE*minX + 15, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE*5 + 20);
				g.setFont(font);
				
		
		//洹몃━�뱶 �몴�떆
		if(usingGrid){
			g.setColor(Color.darkGray);
			// 媛��슫�뜲 媛�濡쒖쨪
			for(int i=1;i<maxY;i++) 
				g.drawLine(3*BOARD_X+maxX*BLOCK_SIZE, BOARD_Y+BLOCK_SIZE*i, 3*BOARD_X + 2*maxX*BLOCK_SIZE, BOARD_Y + BLOCK_SIZE*i);
			// 媛��슫�뜲 �꽭濡쒖쨪
			for(int i=1;i<maxX;i++) 
				g.drawLine(3*BOARD_X+maxX*BLOCK_SIZE + BLOCK_SIZE*i, BOARD_Y, 3*BOARD_X+maxX*BLOCK_SIZE + BLOCK_SIZE*i, BOARD_Y + maxY*BLOCK_SIZE);
			// �쇊履� 媛�濡쒖쨪
			for(int i=1;i<5;i++) 
				g.drawLine(2*BOARD_X+maxX*BLOCK_SIZE+BLOCK_SIZE*minX, BOARD_Y + BLOCK_SIZE*(i+minX), 2*BOARD_X+maxX*BLOCK_SIZE+BLOCK_SIZE*(minX+5),BOARD_Y + BLOCK_SIZE*(i+minX));
			// �쇊履� �꽭濡쒖쨪
			for(int i=1;i<5;i++) 
				g.drawLine(2*BOARD_X+maxX*BLOCK_SIZE+BLOCK_SIZE*(i+minX),BOARD_Y + BLOCK_SIZE*minX, 2*BOARD_X+maxX*BLOCK_SIZE+BLOCK_SIZE*(i+minX),BOARD_Y + BLOCK_SIZE*(minX+5));
			// �삤瑜몄そ �쐞 媛�濡쒖쨪
			for(int i=1;i<5;i++) 
				g.drawLine(3*BOARD_X + 2*maxX*BLOCK_SIZE+BLOCK_SIZE*minX, BOARD_Y + BLOCK_SIZE*(i+minX), 
						3*BOARD_X + 2*maxX*BLOCK_SIZE+BLOCK_SIZE*(minX+5), BOARD_Y + BLOCK_SIZE*(i+minX));
			// �삤瑜몄そ �쐞 �꽭濡쒖쨪
			for(int i=1;i<5;i++) 
				g.drawLine(3*BOARD_X + 2*maxX*BLOCK_SIZE+BLOCK_SIZE*(i+minX), BOARD_Y + BLOCK_SIZE*minX, 
						3*BOARD_X + 2*maxX*BLOCK_SIZE+BLOCK_SIZE*(i+minX), BOARD_Y + BLOCK_SIZE*(minX+5));	
		}

		int x = 0, y = 0, newY = 0;
		if (hold != null) {
			x = 0;
			y = 0;
			newY = 3;
			x = hold.getPosX();
			y = hold.getPosY();
			hold.setPosX(-4 + minX);
			hold.setPosY(newY + minY);
			hold.drawBlock(g);
			hold.setPosX(x);
			hold.setPosY(y);
		}
		
		if (nextBlocks != null) {
			x = 0;
			y = 0;
			newY = 3;
			for (int i = 0; i < nextBlocks.size(); i++) {
				TetrisBlock block = nextBlocks.get(i);
				x = block.getPosX();
				y = block.getPosY();
				block.setPosX(13 + minX);
				block.setPosY(newY + minY);
				if (newY == 3)
					newY = 6;
				block.drawBlock(g);
				block.setPosX(x);
				block.setPosY(y);
				newY += 3;
			}
		}

		if (blockList != null) {
			x = 0;
			y = 0;
			for (int i = 0; i < blockList.size(); i++) {
				Block block = blockList.get(i);
				x = block.getPosGridX();
				y = block.getPosGridY();
				block.setPosGridX(x + minX);
				block.setPosGridY(y + minY);
				block.drawColorBlock(g);
				block.setPosGridX(x);
				block.setPosGridY(y);
			}
		}

		if (ghost != null) {

			if (usingGhost) {
				x = 0;
				y = 0;
				x = ghost.getPosX();
				y = ghost.getPosY();
				ghost.setPosX(x + minX);
				ghost.setPosY(y + minY);
				ghost.drawBlock(g);
				ghost.setPosX(x);
				ghost.setPosY(y);
			}
		}

		if (shap != null) {
			x = 0;
			y = 0;
			x = shap.getPosX();
			y = shap.getPosY();
			shap.setPosX(x + minX);
			shap.setPosY(y + minY);
			shap.drawBlock(g);
			shap.setPosX(x);
			shap.setPosY(y);
		}
		
		
	}

	@Override
	public void run() {
		int countMove = (21 - (int) comboSpeed.getSelectedItem()) * 5;
		int countDown = 0;
		int countUp = up;

		while (isPlay) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (countDown != 0) {
				countDown--;
				if (countDown == 0) {

					if (controller != null && !controller.moveDown())
						this.fixingTetrisBlock();
				}
				this.repaint();
				continue;
			}

			countMove--;
			if (countMove == 0) {
				countMove = (21 - (int) comboSpeed.getSelectedItem()) * 5;
				if (controller != null && !controller.moveDown())
					countDown = down;
				else
					this.showGhost();
			}

			if (countUp != 0) {
				countUp--;
				if (countUp == 0) {
					countUp = up;
					addBlockLine(1);
				}
			}

			this.repaint();
		} // while()
	}// run()

	/**
	 * 留�(蹂댁씠湲�, �끉由�)�쓣 �긽�븯濡� �씠�룞�븳�떎.
	 * 
	 * @param lineNumber
	 * @param num        -1 or 1
	 */
	public void dropBoard(int lineNumber, int num) {

		// 留듭쓣 �뼥�뼱�듃由곕떎.
		this.dropMap(lineNumber, num);

		// 醫뚰몴諛붽퓭二쇨린(1留뚰겮利앷�)
		this.changeTetrisBlockLine(lineNumber, num);

		// �떎�떆 泥댄겕�븯湲�
		this.checkMap();

		// 怨좎뒪�듃 �떎�떆 肉뚮━湲�
		this.showGhost();
	}

	/**
	 * lineNumber�쓽 �쐞履� �씪�씤�뱾�쓣 紐⑤몢 num移몄뵫 �궡由곕떎.
	 * 
	 * @param lineNumber
	 * @param num        移몄닔 -1,1
	 */
	private void dropMap(int lineNumber, int num) {
		if (num == 1) {
			// �븳以꾩뵫 �궡由ш린
			for (int i = lineNumber; i > 0; i--) {
				for (int j = 0; j < map[i].length; j++) {
					map[i][j] = map[i - 1][j];
				}
			}

			// 留� �쐵以꾩� null濡� 留뚮뱾湲�
			for (int j = 0; j < map[0].length; j++) {
				map[0][j] = null;
			}
		} else if (num == -1) {
			// �븳以꾩뵫 �삱由ш린
			for (int i = 1; i <= lineNumber; i++) {
				for (int j = 0; j < map[i].length; j++) {
					map[i - 1][j] = map[i][j];
				}
			}

			// removeLine�� null濡� 留뚮뱾湲�
			for (int j = 0; j < map[0].length; j++) {
				map[lineNumber][j] = null;
			}
		}
	}

	/**
	 * lineNumber�쓽 �쐞履� �씪�씤�뱾�쓣 紐⑤몢 num留뚰겮 �씠�룞�떆�궓�떎.
	 * 
	 * @param lineNumber
	 * @param num        �씠�룞�븷 �씪�씤
	 */
	private void changeTetrisBlockLine(int lineNumber, int num) {
		int y = 0, posY = 0;
		for (int i = 0; i < blockList.size(); i++) {
			y = blockList.get(i).getY();
			posY = blockList.get(i).getPosGridY();
			if (y <= lineNumber)
				blockList.get(i).setPosGridY(posY + num);
		}
	}

	/**
	 * �뀒�듃由ъ뒪 釉붾윮�쓣 怨좎젙�떆�궓�떎.
	 */
	private void fixingTetrisBlock() {
		synchronized (this) {
			if (stop) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		boolean isCombo = false;
		removeLineCount = 0;

		// drawList 異붽�
		for (Block block : shap.getBlock()) {
			blockList.add(block);
		}

		// check
		isCombo = checkMap();

		if (isCombo)
			removeLineCombo++;
		else
			removeLineCombo = 0;

		// 肄쒕갚硫붿냼�뱶
		this.getFixBlockCallBack(blockList, removeLineCombo, removeLineCount);

		// �떎�쓬 �뀒�듃由ъ뒪 釉붾윮�쓣 媛��졇�삩�떎.
		this.nextTetrisBlock();

		// ���뱶媛��뒫�긽�깭濡� 留뚮뱾�뼱以��떎.
		isHold = false;
	}// fixingTetrisBlock()

	/**
	 * 
	 * @return true-吏��슦湲곗꽦怨�, false-吏��슦湲곗떎�뙣
	 */
	private boolean checkMap() {
		boolean isCombo = false;
		int count = 0;
		Block mainBlock;

		for (int i = 0; i < blockList.size(); i++) {
			mainBlock = blockList.get(i);

			// map�뿉 異붽�
			if (mainBlock.getY() < 0 || mainBlock.getY() >= maxY)
				continue;

			if (mainBlock.getY() < maxY && mainBlock.getX() < maxX)
				map[mainBlock.getY()][mainBlock.getX()] = mainBlock;

			// 以꾩씠 苑� 李쇱쓣 寃쎌슦. 寃뚯엫�쓣 醫낅즺�븳�떎.
			if (mainBlock.getY() == 1 && mainBlock.getX() > 2 && mainBlock.getX() < 7) {
				this.gameEndCallBack();
				GameMusic.close(); // 寃뚯엫�쓬�븙 醫낅즺
				GameEndPopUp(); // 寃뚯엫 醫낅즺 �떆 �뙘�뾽 �씠踰ㅽ듃 諛쒖깮 , millions
				break;
			}

			// 1以꾧컻�닔 泥댄겕
			count = 0;
			for (int j = 0; j < maxX; j++) {
				if (map[mainBlock.getY()][j] != null)
					count++;

			}

			// block�쓽 �빐�떦 line�쓣 吏��슫�떎.
			if (count == maxX) {
				removeLineCount++;
				this.removeBlockLine(mainBlock.getY());
				isCombo = true;
			}
		}
		return isCombo;
	}

	/**
	 * �뀒�듃由ъ뒪 釉붾윮 由ъ뒪�듃�뿉�꽌 �뀒�듃由ъ뒪 釉붾윮�쓣 諛쏆븘�삩�떎.
	 */
	public void nextTetrisBlock() {
		shap = nextBlocks.get(0);
		this.initController();
		nextBlocks.remove(0);
		nextBlocks.add(getRandomTetrisBlock());
	}

	private void initController() {
		controller.setBlock(shap);
		ghost = getBlockClone(shap, true);
		controllerGhost.setBlock(ghost);
	}

	/**
	 * lineNumber �씪�씤�쓣 �궘�젣�븯怨�, drawlist�뿉�꽌 �젣嫄고븯怨�, map�쓣 �븘�옒濡� �궡由곕떎.
	 * 
	 * @param lineNumber �궘�젣�씪�씤
	 */
	private void removeBlockLine(int lineNumber) {
		new Music("Clear.mp3", false).start();
		// 1以꾩쓣 吏��썙以�
		for (int j = 0; j < maxX ; j++) {
			for (int s = 0; s < blockList.size(); s++) {
				Block b = blockList.get(s);
				if (b == map[lineNumber][j])
					blockList.remove(s);
			}
			map[lineNumber][j] = null;
		}// for(j)

		this.dropBoard(lineNumber,1);
	}

	/**
	 * TODO : 寃뚯엫醫낅즺肄쒕깹 寃뚯엫�씠 醫낅즺�릺硫� �떎�뻾�릺�뒗 硫붿냼�뱶
	 */
	public void gameEndCallBack() {
		client.gameover();
		this.isPlay = false;
	}

	/**
	 * 怨좎뒪�듃釉붾윮�쓣 蹂댁뿬以��떎.
	 */
	private void showGhost() {
		ghost = getBlockClone(shap, true);
		controllerGhost.setBlock(ghost);
		controllerGhost.moveQuickDown(shap.getPosY(), true);
	}

	/**
	 * �옖�뜡�쑝濡� �뀒�듃由ъ뒪 釉붾윮�쓣 �깮�꽦�븯怨� 諛섑솚�븳�떎.
	 * 
	 * @return �뀒�듃由ъ뒪 釉붾윮
	 */
	public TetrisBlock getRandomTetrisBlock() {
		switch ((int) (Math.random() * 7)) {
		case TetrisBlock.TYPE_CENTERUP:
			return new CenterUp(4, 1);
		case TetrisBlock.TYPE_LEFTTWOUP:
			return new LeftTwoUp(4, 1);
		case TetrisBlock.TYPE_LEFTUP:
			return new LeftUp(4, 1);
		case TetrisBlock.TYPE_RIGHTTWOUP:
			return new RightTwoUp(4, 1);
		case TetrisBlock.TYPE_RIGHTUP:
			return new RightUp(4, 1);
		case TetrisBlock.TYPE_LINE:
			return new Line(4, 1);
		case TetrisBlock.TYPE_NEMO:
			return new Nemo(4, 1);
		}
		return null;
	}

	/**
	 * tetrisBlock怨� 媛숈� 紐⑥뼇�쑝濡� 怨좎뒪�듃�쓽 釉붾윮紐⑥뼇�쓣 諛섑솚�븳�떎.
	 * 
	 * @param tetrisBlock 怨좎뒪�듃�쓽 釉붾윮紐⑥뼇�쓣 寃곗젙�븷 釉붾윮
	 * @return 怨좎뒪�듃�쓽 釉붾윮紐⑥뼇�쓣 諛섑솚
	 */
	public TetrisBlock getBlockClone(TetrisBlock tetrisBlock, boolean isGhost) {
		TetrisBlock blocks = null;
		switch (tetrisBlock.getType()) {
		case TetrisBlock.TYPE_CENTERUP:
			blocks = new CenterUp(4, 1);
			break;
		case TetrisBlock.TYPE_LEFTTWOUP:
			blocks = new LeftTwoUp(4, 1);
			break;
		case TetrisBlock.TYPE_LEFTUP:
			blocks = new LeftUp(4, 1);
			break;
		case TetrisBlock.TYPE_RIGHTTWOUP:
			blocks = new RightTwoUp(4, 1);
			break;
		case TetrisBlock.TYPE_RIGHTUP:
			blocks = new RightUp(4, 1);
			break;
		case TetrisBlock.TYPE_LINE:
			blocks = new Line(4, 1);
			break;
		case TetrisBlock.TYPE_NEMO:
			blocks = new Nemo(4, 1);
			break;
		}
		if (blocks != null && isGhost) {
			blocks.setGhostView(isGhost);
			blocks.setPosX(tetrisBlock.getPosX());
			blocks.setPosY(tetrisBlock.getPosY());
			blocks.rotation(tetrisBlock.getRotationIndex());
		}
		return blocks;
	}

	/**
	 * TODO : 肄쒕갚硫붿냼�뱶 �뀒�듃由ъ뒪 釉붾윮�씠 怨좎젙�맆 �븣 �옄�룞 �샇異� �맂�떎.
	 * 
	 * @param removeCombo   �쁽�옱 肄ㅻ낫 �닔
	 * @param removeMaxLine �븳踰덉뿉 吏��슫 以꾩닔
	 */
	public void getFixBlockCallBack(ArrayList<Block> blockList, int removeCombo, int removeMaxLine) {
		if (removeCombo < 3) {
			if (removeMaxLine == 3)
				client.addBlock(1);
			else if (removeMaxLine == 4)
				client.addBlock(3);
		} else if (removeCombo < 10) {
			if (removeMaxLine == 3)
				client.addBlock(2);
			else if (removeMaxLine == 4)
				client.addBlock(4);
			else
				client.addBlock(1);
		} else {
			if (removeMaxLine == 3)
				client.addBlock(3);
			else if (removeMaxLine == 4)
				client.addBlock(5);
			else
				client.addBlock(2);
		}
	}

	/**
	 * 釉붾윮�쓣 ���뱶�떆�궓�떎.
	 */
	public void playBlockHold() {
		if(isHold) return;
		
		if(hold==null){
			new Music("Save.mp3", false).start();
			hold = getBlockClone(shap,false);
			this.nextTetrisBlock();
		}else{
			new Music("Save.mp3", false).start();
			TetrisBlock tmp = getBlockClone(shap,false);
			shap = getBlockClone(hold,false);
			hold = getBlockClone(tmp,false);
			this.initController();
		}
		
		isHold = true;
	}

	/**
	 * 媛��옣 諛묒뿉 以꾩뿉 釉붾윮�쓣 �깮�꽦�븳�떎.
	 * 
	 * @param numOfLine
	 */
	boolean stop = false;

	public void addBlockLine(int numOfLine) {
		stop = true;
		// �궡由ш린媛� �엳�쓣 �븣源뚯� ��湲고븳�떎.
		// �궡由ш린瑜� 紐⑤몢 �떎�뻾�븳 �썑 �떎�떆 �떆�옉�븳�떎.
		Block block;
		int rand = (int) (Math.random() * maxX);
		for (int i = 0; i < numOfLine; i++) {
			this.dropBoard(maxY - 1, -1);
			for (int col = 0; col < maxX; col++) {
				if (col != rand) {
					block = new Block(0, 0, Color.GRAY, Color.GRAY);
					block.setPosGridXY(col, maxY - 1);
					blockList.add(block);
					map[maxY - 1][col] = block;
				}
			}
			// 留뚯빟 �궡�젮�삤�뒗 釉붾윮怨� 寃뱀튂硫� 釉붾윮�쓣 �쐞濡� �삱由곕떎.
			boolean up = false;
			for (int j = 0; j < shap.getBlock().length; j++) {
				Block sBlock = shap.getBlock(j);
				if (map[sBlock.getY()][sBlock.getX()] != null) {
					up = true;
					break;
				}
			}
			if (up) {
				controller.moveDown(-1);
			}
		}

		this.showGhost();
		this.repaint();
		synchronized (this) {
			stop = false;
			this.notify();
		}
	}
	// popup �씠踰ㅽ듃 �븿�닔 , millions
	public void GameEndPopUp() {  
		GameEndSound = new Music("GameOver.mp3", false); 
		GameEndSound.start();	
		ImageIcon popupicon = new ImageIcon(TetrisMain.class.getResource("../../../Images/GAMEOVER.PNG"));
		JOptionPane.showMessageDialog(null, null, "The End", JOptionPane.ERROR_MESSAGE, popupicon);
	}


	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		Button button = new Button();
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			messageArea.requestFocus();
		}
		if (!isPlay)
			return;
		if (e.getKeyCode() == button.getLeft_key()) {
			if (usingEffect)
				new Music("Left.mp3", false).start(); // millions
			controller.moveLeft();
			controllerGhost.moveLeft();
		} else if (e.getKeyCode() == button.right_key) {
			if (usingEffect)
				new Music("Right.mp3", false).start(); // millions
			controller.moveRight();
			controllerGhost.moveRight();
		} else if (e.getKeyCode() == button.down_key) {
			if (usingEffect)
				new Music("Down.mp3", false).start();	// millions
			controller.moveDown();
		} else if (e.getKeyCode() == button.up_key) {
			if (usingEffect)
				new Music("Rotation.mp3", false).start();	// millions
			controller.nextRotationLeft();
			controllerGhost.nextRotationLeft();
		} else if (e.getKeyCode() == button.space_key) {
			controller.moveQuickDown(shap.getPosY(), true);
			this.fixingTetrisBlock();
			
			if (usingEffect)
				new Music("Space.mp3", false).start();	// millions
			
<<<<<<< HEAD
<<<<<<< HEAD
		} else if (e.getKeyCode() == Button.shift_key) {
=======
		} else if (e.getKeyCode() == Button.shift_key) {	// millions
>>>>>>> 3748fd147596c04e6ef70dfe4e2546ba96015336
=======
		} else if (e.getKeyCode() == Button.shift_key) {	// millions
>>>>>>> origin
			playBlockHold();
		}
		this.showGhost();
		this.repaint();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		this.requestFocus();
	}

	public void mouseReleased(MouseEvent e) {
	}
	
	// 寃뚯엫 裕ㅼ쭅 �궎怨� �걚湲� millions
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnStart) {
			
			if(GameMusic != null && GameMusic.isAlive() && usingBGM) {
				GameMusic.close();
				if(usingBGM) {
					GameMusic = new Music("GameMusic.mp3", true );
					GameMusic.start();
					
					}
			}else {
				if(usingBGM) {
					GameMusic = new Music("GameMusic.mp3", true );
					GameMusic.start();
					
				}
			}
				
			if(client!=null){
				client.gameStart((int)comboSpeed.getSelectedItem());
			}else{
				this.gameStart((int)comboSpeed.getSelectedItem());
			}
			
		}else if(e.getSource() == btnExit){
			if(GameMusic != null && GameMusic.isAlive()) {
				GameMusic.close();
			}
			if (client != null) {
				GameMusic.close();	// millions
				if (tetris.isNetwork()) {
					client.closeNetwork(tetris.isServer());
				}
			} else {
				System.exit(0);
			}

		}
	}
	

	public boolean isPlay() {
		return isPlay;
	}

	public void setPlay(boolean isPlay) {
		this.isPlay = isPlay;
	}

	public JButton getBtnStart() {
		return btnStart;
	}

	public JButton getBtnExit() {
		return btnExit;
	}

	public void setClient(GameClient client) {
		this.client = client;
	}

	public void printSystemMessage(String msg) {
		systemMsg.printMessage(msg);
	}

	public void printMessage(String msg) {
		messageArea.printMessage(msg);
	}

	public GameClient getClient() {
		return client;
	}

	public void changeSpeed(Integer speed) {
		comboSpeed.setSelectedItem(speed);
	}

	public void clearMessage() {
		messageArea.clearMessage();
		systemMsg.clearMessage();
	}

}
