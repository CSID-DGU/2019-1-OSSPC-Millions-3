package com.tetris.main;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.tetris.window.Tetris;

public class TetrisOpening extends JFrame {
   
   private Image screenImage;
   private Graphics screenGraphic;
   
   private ImageIcon exitButtonEnteredImage = new ImageIcon(TetrisMain.class.getResource("../../../Images/exitButtonEntered.png"));
   private ImageIcon exitButtonBasicImage = new ImageIcon(TetrisMain.class.getResource("../../../Images/exitButtonBasic.png"));
   private ImageIcon startButtonEnteredImage = new ImageIcon(TetrisMain.class.getResource("../../../Images/startButtonEntered.png"));
   private ImageIcon startButtonBasicImage = new ImageIcon(TetrisMain.class.getResource("../../../Images/startButtonBasic.png"));
   private ImageIcon quitButtonEnteredImage = new ImageIcon(TetrisMain.class.getResource("../../../Images/quitButtonEntered.png"));
   private ImageIcon quitButtonBasicImage = new ImageIcon(TetrisMain.class.getResource("../../../Images/quitButtonBasic.png"));
   
   private Image background = new ImageIcon(TetrisMain.class.getResource("../../../Images/Introbackground.jpg")).getImage();
   private JLabel menuBar = new JLabel(new ImageIcon(TetrisMain.class.getResource("../../../Images/menuBar.png")));
   
   private JButton exitButton = new JButton(exitButtonBasicImage);
   private JButton startButton = new JButton(startButtonBasicImage);
   private JButton quitButton = new JButton(quitButtonBasicImage);
   
   private int mouseX, mouseY;
   private boolean isMainScreen = false;

   public TetrisOpening() {
      setUndecorated(true);
      setTitle("Tetris");
      setSize(TetrisMain.SCREEN_WIDTH, TetrisMain.SCREEN_HEIGHT);
      setResizable(false);
      setLocationRelativeTo(null);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true);
      setBackground(new Color(0, 0, 0, 0));
      setLayout(null);
      
      
      exitButton.setBounds(450, 0, 30, 30);
      exitButton.setBorderPainted(false);
      exitButton.setContentAreaFilled(false);
      exitButton.setFocusPainted(false);
      exitButton.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent e) {
            exitButton.setIcon(exitButtonEnteredImage);
            exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
         }
         @Override
         public void mouseExited(MouseEvent e) {
            exitButton.setIcon(exitButtonBasicImage);
            exitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         }
         @Override
         public void mousePressed(MouseEvent e) {
            
            try { 
               Thread.sleep(1000);
            } catch(InterruptedException ex) {
               ex.printStackTrace();
            }
            System.exit(0);
         }
      });
      
      add(exitButton);
      
      
      startButton.setBounds(40, 200, 400, 100);
      startButton.setBorderPainted(false);
      startButton.setContentAreaFilled(false);
      startButton.setFocusPainted(false);
      startButton.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent e) {
            startButton.setIcon(startButtonEnteredImage);
            startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
         }
         @Override
         public void mouseExited(MouseEvent e) {
            startButton.setIcon(startButtonBasicImage);
            startButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         }
         @Override
         public void mousePressed(MouseEvent e) {
            startButton.setVisible(false);
            quitButton.setVisible(false);
            //background 감추기
            isMainScreen = true;
            //테트리스 게임화면 호출
            new Tetris();
         }
      });
      
      add(startButton);
      
      
      quitButton.setBounds(40, 330, 400, 100);
      quitButton.setBorderPainted(false);
      quitButton.setContentAreaFilled(false);
      quitButton.setFocusPainted(false);
      quitButton.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent e) {
            quitButton.setIcon(quitButtonEnteredImage);
            quitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
         }
         @Override
         public void mouseExited(MouseEvent e) {
            quitButton.setIcon(quitButtonBasicImage);
            quitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         }
         @Override
         public void mousePressed(MouseEvent e) {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException ex) {
               ex.printStackTrace();
            }
            System.exit(0);
            
         }
      });
      
      add(quitButton);
      
      menuBar.setBounds(0, 0, 1280, 30);
      menuBar.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();
         }
      });
      menuBar.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseDragged(MouseEvent e) {
            int x = e.getXOnScreen();
            int y = e.getYOnScreen();
            setLocation(x - mouseX, y - mouseY);
         }
      });
      add(menuBar);
      
   
   }
   
   public void paint(Graphics g) {
      screenImage = createImage(TetrisMain.SCREEN_WIDTH, TetrisMain.SCREEN_HEIGHT);
      screenGraphic = screenImage.getGraphics();
      screenDraw(screenGraphic);
      g.drawImage(screenImage, 0, 0, null);
   }
   
   public void screenDraw(Graphics g) {
      g.drawImage(background, 0, 0, null);
      if(isMainScreen) {
         this.setVisible(false);
      }
      printComponents(g);
      this.repaint();
   }

}
 