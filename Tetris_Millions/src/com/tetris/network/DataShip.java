package com.tetris.network;

import java.io.Serializable;
import java.util.ArrayList;

import com.tetris.classes.Block;
import com.tetris.classes.TetrisBlock;

public class DataShip implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final int CLOSE_NETWORK = 0;	//������ ������ ����.
	public static final int EXIT = 1;			//���α׷�����
	public static final int SERVER_EXIT = 2;	//������ ����� ���
	public static final int PRINT_SYSTEM_OPEN_MESSAGE = 3;	//����Ʈ �̸� �޾ƿ���.
	public static final int PRINT_SYSTEM_ADDMEMBER_MESSAGE = 4;	//�� ���� ������ ���.
	public static final int GAME_START = 5;
	public static final int GAME_OVER = 6;
	public static final int ADD_BLOCK = 7;
	public static final int SET_INDEX = 8;
	public static final int PRINT_MESSAGE = 9;
	public static final int PRINT_SYSTEM_MESSAGE=10;
	public static final int GAME_WIN=11;
	public static final int DRAW_BLOCK_SHAP=12;//HK
	public static final int DRAW_BLOCK_DEPOSIT=13;//HK
	public static final int ENEMY_SCORE = 14; //millions
	
	
	private int cmd = -1;
	private String name;
	private String ip;
	private String msg;
	private int numOfBlock;
	private int index;
	private int rank;
	private boolean isPlay;
	private int totalAdd;
	private int speed;
	private TetrisBlock shap;//HK
	private ArrayList<Block> blockList2;//HK
	private int player = -1;// �÷��̾ �����ϱ� ���� ���� HK
	private int EnemyScore;  // millions
	
	

	public DataShip(){}
	public DataShip(int cmd){this.cmd = cmd;}

	
	public ArrayList<Block> getDeposit() {return blockList2;}//HK
	public void setDeposit(ArrayList<Block> blockList2) {this.blockList2=blockList2;}//HK
	public int getPlayer() {return player;}//HK
	public void setPlayer(int player) {this.player = player;}//HK
	public TetrisBlock getShap( ) {return shap; }//HK
	public void setShap(TetrisBlock shap) {this.shap = shap;}//HK
	public int getEnemyScore() {return EnemyScore;} //millions
	public void setEnemyScore(int EnemyScore) {this.EnemyScore = EnemyScore;} //millions
	public void setCommand(int type){this.cmd = type;};
	public int getCommand(){return cmd;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getIp() {return ip;}
	public void setIp(String ip) {this.ip = ip;}
	public String getMsg() {return msg;}
	public void setMsg(String msg) {this.msg = msg;}
	public int getNumOfBlock() {return numOfBlock;}
	public void setNumOfBlock(int numOfBlock) {this.numOfBlock = numOfBlock;}
	public int getIndex() {return index;}
	public void setIndex(int index) {this.index = index;}
	public int getRank() {return rank;}
	public void setRank(int rank) {this.rank = rank;}
	public boolean isPlay() {return isPlay;}
	public void setPlay(boolean isPlay) {this.isPlay = isPlay;}
	public int getTotalAdd() {return totalAdd;}
	public void setTotalAdd(int totalAdd) {this.totalAdd = totalAdd;}
	public int getSpeed() {return speed;}
	public void setSpeed(int speed) {this.speed = speed;}
}

