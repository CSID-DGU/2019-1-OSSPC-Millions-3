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
	public static final int PANEL_WIDTH = 2 * ( maxX * BLOCK_SIZE + MESSAGE_WIDTH + BOARD_X);
	public static final int PANEL_HEIGHT = maxY * BLOCK_SIZE + MESSAGE_HEIGHT + BOARD_Y;

	private SystemMessageArea systemMsg = new SystemMessageArea(BLOCK_SIZE * 1, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE * 7,BLOCK_SIZE * 5, BLOCK_SIZE * 12);
	private MessageArea messageArea = new MessageArea(this,0, PANEL_HEIGHT - MESSAGE_HEIGHT, PANEL_WIDTH-BLOCK_SIZE*7, MESSAGE_HEIGHT);
	private JButton btnStart = new JButton("�����ϱ�");
	private JButton btnExit = new JButton("������");
	private JCheckBox checkGhost = new JCheckBox("��Ʈ���", true);
	private JCheckBox checkGrid = new JCheckBox("���� ǥ��", true);
	
	
	private JCheckBox checkEffect = new JCheckBox("ȿ����", true);					// ȿ���� checkbox (millions)
	private JCheckBox checkBGM = new JCheckBox("�������", true);						// ������� checkbox (millions)
	
	
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
	
	
	private boolean usingEffect = true;							// ȿ���� (millions)
	private boolean usingBGM = true;							// ������� (millions)
	
	
	private int removeLineCount = 0;
	private int removeLineCombo = 0;
	
	//Music ��ü millions
	public Music GameMusic; // �����ϴ� ������ ����
	public Music GameEndSound;// ���� ���� �� ȿ����


	public TetrisBoard(Tetris tetris, GameClient client) {
		this.tetris = tetris;
		this.client = client;
		this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));// �⺻ũ��
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.setLayout(null);
		this.setFocusable(true);

		btnStart.setBounds(PANEL_WIDTH - BLOCK_SIZE * 7, PANEL_HEIGHT - messageArea.getHeight(), BLOCK_SIZE * 7,
				messageArea.getHeight() / 2);
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
		
			
			
		}); // ��ư ȿ���� millions
		
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
		
		// ��ư ȿ���� millions
		
		checkGhost.setBounds(PANEL_WIDTH - BLOCK_SIZE * 12 + 35, 5, 95, 20);		//��Ʈ��� checkbox �������� �̵�.(millions)
		checkGhost.setBackground(new Color(255, 255, 255));
		checkGhost.setForeground(Color.GRAY);
		checkGhost.setFont(new Font("����", Font.BOLD, 13));
		checkGhost.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				usingGhost = checkGhost.isSelected();
				TetrisBoard.this.setRequestFocusEnabled(true);
				TetrisBoard.this.repaint();
			}
		});
		checkGrid.setBounds(PANEL_WIDTH - BLOCK_SIZE * 12 + 35, 25, 95, 20);		//���ڸ�� checkbox �������� �̵�.(millions)
		checkGrid.setBackground(new Color(255, 255, 255));
		checkGrid.setForeground(Color.GRAY);
		checkGrid.setFont(new Font("����", Font.BOLD, 13));
		checkGrid.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				usingGrid = checkGrid.isSelected();
				TetrisBoard.this.setRequestFocusEnabled(true);
				TetrisBoard.this.repaint();
			}
		});
		
		checkEffect.setBounds(PANEL_WIDTH - BLOCK_SIZE * 7 + 35, 5, 95, 20);		//ȿ���� checkbox ��ġ �� ������(millions)
		checkEffect.setBackground(new Color(255, 255, 255));
		checkEffect.setForeground(Color.GRAY);
		checkEffect.setFont(new Font("����", Font.BOLD, 13));
		checkEffect.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				//usingEffect = checkEffect.isSelected();
				TetrisBoard.this.setRequestFocusEnabled(true);
				TetrisBoard.this.repaint();
			}
		});
		
		checkBGM.setBounds(PANEL_WIDTH - BLOCK_SIZE * 7 + 35, 25, 95, 20);			//������� checkbox ��ġ �� ������(millions)
		checkBGM.setBackground(new Color(255, 255, 255));
		checkBGM.setForeground(Color.GRAY);
		checkBGM.setFont(new Font("����", Font.BOLD, 13));
		checkBGM.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				//usingBGM = checkBGM.isSelected();
				TetrisBoard.this.setRequestFocusEnabled(true);
				TetrisBoard.this.repaint();
			}
		});
		
		
		comboSpeed.setBounds(PANEL_WIDTH - BLOCK_SIZE * 13, 5, 45, 20); 			// �ӵ� ���� ǥ�� �������� �̵�.(millions)
		this.add(comboSpeed);

		this.add(systemMsg);
		this.add(messageArea);
		this.add(btnStart);
		this.add(btnExit);
		this.add(checkGhost);
		this.add(checkGrid);
		
		this.add(checkEffect);														//ȿ����(millions)
		this.add(checkBGM);															//�������(millions)
	}

	public void startNetworking(String ip, int port, String nickName) {
		this.ip = ip;
		this.port = port;
		this.nickName = nickName;
		this.repaint();
	}

	/**
	 * TODO : ���ӽ��� ������ �����Ѵ�.
	 */

	public void gameStart(int speed) {
		comboSpeed.setSelectedItem(new Integer(speed));
		// ���� ���� �����带 ������Ų��.
		if (th != null) {
			try {
				isPlay = false;
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// �ʼ���
		map = new Block[maxY][maxX];
		blockList = new ArrayList<Block>();
		nextBlocks = new ArrayList<TetrisBlock>();

		// ��������
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

		// ������ ����
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

		// IP ���
		g.drawString("ip : " + ip + "     port : " + port, 20, 20);

		// NickName ���
		g.drawString("�г��� : " + nickName, 20, 40);

		// �ӵ�
		Font font = g.getFont();
		g.setFont(new Font("����", Font.BOLD, 13));
		g.drawString("�ӵ�", PANEL_WIDTH - BLOCK_SIZE * 15, 20);
		g.setFont(font);

		g.setColor(Color.lightGray);
		g.fillRect(BOARD_X, BOARD_Y, maxX*BLOCK_SIZE, maxY*BLOCK_SIZE);
		// ����
		g.fillRect(BLOCK_SIZE*minX ,BOARD_Y + BLOCK_SIZE, BLOCK_SIZE*5,BLOCK_SIZE*5);
		// ������ ��
		g.fillRect(BOARD_X + maxX*BLOCK_SIZE+ BLOCK_SIZE*minX, BOARD_Y + BLOCK_SIZE, BLOCK_SIZE*5,BLOCK_SIZE*5);
		// ������ �Ʒ�
		g.fillRect(BOARD_X + maxX*BLOCK_SIZE + BLOCK_SIZE*minX, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE*7, BLOCK_SIZE*5,BLOCK_SIZE*12);

		// HOLD NEXT ���
		g.setFont(new Font(font.getFontName(), font.getStyle(), 20));
		g.drawString("H O L D", BLOCK_SIZE*minX + 15, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE*5 + 20);
		g.drawString("N E X T", BOARD_X + maxX*BLOCK_SIZE+ BLOCK_SIZE*minX + 15, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE*5 + 20);
		g.setFont(font);

		// �׸��� ǥ��
		if (usingGrid) {
			g.setColor(Color.WHITE);
			for(int i=1;i<maxY;i++) 
				g.drawLine(BOARD_X, BOARD_Y+BLOCK_SIZE*i, BOARD_X + maxX*BLOCK_SIZE, BOARD_Y + BLOCK_SIZE*i);
			// ��� ������
			for(int i=1;i<maxX;i++) 
				g.drawLine(BOARD_X + BLOCK_SIZE*i, BOARD_Y, BOARD_X + BLOCK_SIZE*i, BOARD_Y + maxY*BLOCK_SIZE);
			// ���� ������
			for(int i=1;i<5;i++) 
				g.drawLine(BLOCK_SIZE*minX ,BOARD_Y + BLOCK_SIZE*(i+minX), BLOCK_SIZE*(minX+5),BOARD_Y + BLOCK_SIZE*(i+minX));
			// ���� ������
			for(int i=1;i<5;i++) 
				g.drawLine(BLOCK_SIZE*(i+minX) ,BOARD_Y + BLOCK_SIZE*minX, BLOCK_SIZE*(i+minX),BOARD_Y + BLOCK_SIZE*(minX+5));
			// ������ �� ������
			for(int i=1;i<5;i++) 
				g.drawLine(BOARD_X + BLOCK_SIZE*minX + maxX*BLOCK_SIZE, BOARD_Y + BLOCK_SIZE*(i+minX), 
						BOARD_X + maxX*BLOCK_SIZE + BLOCK_SIZE*(minX+5), BOARD_Y + BLOCK_SIZE*(i+minX));
			// ������ �� ������
			for(int i=1;i<5;i++) 
				g.drawLine(BOARD_X + maxX*BLOCK_SIZE + BLOCK_SIZE*(i+minX), BOARD_Y + BLOCK_SIZE*minX, 
						BOARD_X + maxX*BLOCK_SIZE + BLOCK_SIZE*(i+minX), BOARD_Y + BLOCK_SIZE*(minX+5));	
		
		}
		
		g.drawLine(this.getWidth()/2, BOARD_Y, this.getWidth()/2, BOARD_Y+maxY*BLOCK_SIZE);
		
		// <<2p ȭ��>>
				// � ��� �κ�
				g.setColor(Color.BLACK);
				// ���
				g.fillRect(3*BOARD_X+maxX*BLOCK_SIZE, BOARD_Y, maxX*BLOCK_SIZE, maxY*BLOCK_SIZE);
				// ����
				g.fillRect(2*BOARD_X+maxX*BLOCK_SIZE+BLOCK_SIZE*minX,BOARD_Y + BLOCK_SIZE, BLOCK_SIZE*5,BLOCK_SIZE*5);
				// ������ ��
				g.fillRect(3*BOARD_X + 2*maxX*BLOCK_SIZE+BLOCK_SIZE*minX, BOARD_Y + BLOCK_SIZE, BLOCK_SIZE*5,BLOCK_SIZE*5);
				// ������ �Ʒ�
				g.fillRect(3*BOARD_X + 2*maxX*BLOCK_SIZE+BLOCK_SIZE*minX, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE*7, BLOCK_SIZE*5,BLOCK_SIZE*12);
				
				//HOLD  NEXT ���
				g.setFont(new Font(font.getFontName(),font.getStyle(),20));
				g.setColor(Color.BLACK);
				g.drawString("H O L D", 2*BOARD_X+maxX*BLOCK_SIZE+BLOCK_SIZE*minX + 15, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE*5 + 20);
				g.drawString("N E X T", 3*BOARD_X + 2*maxX*BLOCK_SIZE+ BLOCK_SIZE*minX + 15, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE*5 + 20);
				g.setFont(font);
				
		
		//�׸��� ǥ��
		if(usingGrid){
			g.setColor(Color.darkGray);
			// ��� ������
			for(int i=1;i<maxY;i++) 
				g.drawLine(3*BOARD_X+maxX*BLOCK_SIZE, BOARD_Y+BLOCK_SIZE*i, 3*BOARD_X + 2*maxX*BLOCK_SIZE, BOARD_Y + BLOCK_SIZE*i);
			// ��� ������
			for(int i=1;i<maxX;i++) 
				g.drawLine(3*BOARD_X+maxX*BLOCK_SIZE + BLOCK_SIZE*i, BOARD_Y, 3*BOARD_X+maxX*BLOCK_SIZE + BLOCK_SIZE*i, BOARD_Y + maxY*BLOCK_SIZE);
			// ���� ������
			for(int i=1;i<5;i++) 
				g.drawLine(2*BOARD_X+maxX*BLOCK_SIZE+BLOCK_SIZE*minX, BOARD_Y + BLOCK_SIZE*(i+minX), 2*BOARD_X+maxX*BLOCK_SIZE+BLOCK_SIZE*(minX+5),BOARD_Y + BLOCK_SIZE*(i+minX));
			// ���� ������
			for(int i=1;i<5;i++) 
				g.drawLine(2*BOARD_X+maxX*BLOCK_SIZE+BLOCK_SIZE*(i+minX),BOARD_Y + BLOCK_SIZE*minX, 2*BOARD_X+maxX*BLOCK_SIZE+BLOCK_SIZE*(i+minX),BOARD_Y + BLOCK_SIZE*(minX+5));
			// ������ �� ������
			for(int i=1;i<5;i++) 
				g.drawLine(3*BOARD_X + 2*maxX*BLOCK_SIZE+BLOCK_SIZE*minX, BOARD_Y + BLOCK_SIZE*(i+minX), 
						3*BOARD_X + 2*maxX*BLOCK_SIZE+BLOCK_SIZE*(minX+5), BOARD_Y + BLOCK_SIZE*(i+minX));
			// ������ �� ������
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
		
		// ȿ���� ���
		if(usingEffect) {
			
		}
		
		// BGM ���
		if(usingBGM) {
			
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
	 * ��(���̱�, ��)�� ���Ϸ� �̵��Ѵ�.
	 * 
	 * @param lineNumber
	 * @param num        -1 or 1
	 */
	public void dropBoard(int lineNumber, int num) {

		// ���� ����Ʈ����.
		this.dropMap(lineNumber, num);

		// ��ǥ�ٲ��ֱ�(1��ŭ����)
		this.changeTetrisBlockLine(lineNumber, num);

		// �ٽ� üũ�ϱ�
		this.checkMap();

		// ��Ʈ �ٽ� �Ѹ���
		this.showGhost();
	}

	/**
	 * lineNumber�� ���� ���ε��� ��� numĭ�� ������.
	 * 
	 * @param lineNumber
	 * @param num        ĭ�� -1,1
	 */
	private void dropMap(int lineNumber, int num) {
		if (num == 1) {
			// ���پ� ������
			for (int i = lineNumber; i > 0; i--) {
				for (int j = 0; j < map[i].length; j++) {
					map[i][j] = map[i - 1][j];
				}
			}

			// �� ������ null�� �����
			for (int j = 0; j < map[0].length; j++) {
				map[0][j] = null;
			}
		} else if (num == -1) {
			// ���پ� �ø���
			for (int i = 1; i <= lineNumber; i++) {
				for (int j = 0; j < map[i].length; j++) {
					map[i - 1][j] = map[i][j];
				}
			}

			// removeLine�� null�� �����
			for (int j = 0; j < map[0].length; j++) {
				map[lineNumber][j] = null;
			}
		}
	}

	/**
	 * lineNumber�� ���� ���ε��� ��� num��ŭ �̵���Ų��.
	 * 
	 * @param lineNumber
	 * @param num        �̵��� ����
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
	 * ��Ʈ���� ���� ������Ų��.
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

		// drawList �߰�
		for (Block block : shap.getBlock()) {
			blockList.add(block);
		}

		// check
		isCombo = checkMap();

		if (isCombo)
			removeLineCombo++;
		else
			removeLineCombo = 0;

		// �ݹ�޼ҵ�
		this.getFixBlockCallBack(blockList, removeLineCombo, removeLineCount);

		// ���� ��Ʈ���� ���� �����´�.
		this.nextTetrisBlock();

		// Ȧ�尡�ɻ��·� ������ش�.
		isHold = false;
	}// fixingTetrisBlock()

	/**
	 * 
	 * @return true-����⼺��, false-��������
	 */
	private boolean checkMap() {
		boolean isCombo = false;
		int count = 0;
		Block mainBlock;

		for (int i = 0; i < blockList.size(); i++) {
			mainBlock = blockList.get(i);

			// map�� �߰�
			if (mainBlock.getY() < 0 || mainBlock.getY() >= maxY)
				continue;

			if (mainBlock.getY() < maxY && mainBlock.getX() < maxX)
				map[mainBlock.getY()][mainBlock.getX()] = mainBlock;

			// ���� �� á�� ���. ������ �����Ѵ�.
			if (mainBlock.getY() == 1 && mainBlock.getX() > 2 && mainBlock.getX() < 7) {
				this.gameEndCallBack();
				GameMusic.close(); // �������� ����
				GameEndPopUp(); // ���� ���� �� �˾� �̺�Ʈ �߻� , millions
				break;
			}

			// 1�ٰ��� üũ
			count = 0;
			for (int j = 0; j < maxX; j++) {
				if (map[mainBlock.getY()][j] != null)
					count++;

			}

			// block�� �ش� line�� �����.
			if (count == maxX) {
				removeLineCount++;
				this.removeBlockLine(mainBlock.getY());
				isCombo = true;
			}
		}
		return isCombo;
	}

	/**
	 * ��Ʈ���� �� ����Ʈ���� ��Ʈ���� ���� �޾ƿ´�.
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
	 * lineNumber ������ �����ϰ�, drawlist���� �����ϰ�, map�� �Ʒ��� ������.
	 * 
	 * @param lineNumber ��������
	 */
	private void removeBlockLine(int lineNumber) {
		new Music("Clear.mp3", false).start();
		// 1���� ������
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
	 * TODO : ���������ݺ� ������ ����Ǹ� ����Ǵ� �޼ҵ�
	 */
	public void gameEndCallBack() {
		client.gameover();
		this.isPlay = false;
	}

	/**
	 * ��Ʈ���� �����ش�.
	 */
	private void showGhost() {
		ghost = getBlockClone(shap, true);
		controllerGhost.setBlock(ghost);
		controllerGhost.moveQuickDown(shap.getPosY(), true);
	}

	/**
	 * �������� ��Ʈ���� ���� �����ϰ� ��ȯ�Ѵ�.
	 * 
	 * @return ��Ʈ���� ��
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
	 * tetrisBlock�� ���� ������� ��Ʈ�� ������� ��ȯ�Ѵ�.
	 * 
	 * @param tetrisBlock ��Ʈ�� ������� ������ ��
	 * @return ��Ʈ�� ������� ��ȯ
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
	 * TODO : �ݹ�޼ҵ� ��Ʈ���� ���� ������ �� �ڵ� ȣ�� �ȴ�.
	 * 
	 * @param removeCombo   ���� �޺� ��
	 * @param removeMaxLine �ѹ��� ���� �ټ�
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
	 * ���� Ȧ���Ų��.
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
	 * ���� �ؿ� �ٿ� ���� �����Ѵ�.
	 * 
	 * @param numOfLine
	 */
	boolean stop = false;

	public void addBlockLine(int numOfLine) {
		stop = true;
		// �����Ⱑ ���� ������ ����Ѵ�.
		// �����⸦ ��� ������ �� �ٽ� �����Ѵ�.
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
			// ���� �������� ���� ��ġ�� ���� ���� �ø���.
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
	// popup �̺�Ʈ �Լ� , millions
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
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			messageArea.requestFocus();
		}
		if(!isPlay) return;
		if(e.getKeyCode() == button.left_key){
			controller.moveLeft();
			controllerGhost.moveLeft();
			new Music("Left.mp3", false).start();
			
		}else if(e.getKeyCode() == button.right_key){
			controller.moveRight();
			controllerGhost.moveRight();
			new Music("Right.mp3", false).start();


		}else if(e.getKeyCode() == button.down_key){
			controller.moveDown();
			new Music("Down.mp3", false).start();
		

		}else if(e.getKeyCode() == button.up_key){
			controller.nextRotationLeft();
			controllerGhost.nextRotationLeft();
			new Music("Rotation.mp3", false).start();
		
		}else if(e.getKeyCode() == button.space_key){
			controller.moveQuickDown(shap.getPosY(), true);
			this.fixingTetrisBlock();
			new Music("Space.mp3", false).start();
			
		}else if(e.getKeyCode() == button.shift_key){ 
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
	
	// ���� ���� Ű�� ���� millions
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnStart) {
			
			if(GameMusic != null && GameMusic.isAlive()) {
				GameMusic.close();
				GameMusic = new Music("GameMusic.mp3", true );
				GameMusic.start();
				
			}else {
				GameMusic = new Music("GameMusic.mp3", true );
				GameMusic.start();
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