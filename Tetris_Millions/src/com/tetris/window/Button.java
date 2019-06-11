package com.tetris.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class Button {
   
   JLabel keyLabel;
   JTextField keysetNumTxt;
   JFrame frame, keyFrame;
   JPanel centerPanel;
   JButton eventBtn;
   JButton[] btn = new JButton[6];
   Container contentPane;
   String keyString = null;
   private static int left_key = 37, right_key=39, down_key=40, up_key=38, space_key=32, shift_key=16, keyCode, pk=0;
   // 생성자
   public Button() {}
 
   public int getLeft_key() {
      return left_key;
   }
   public int getRight_key() {
   return right_key;
}
   public int getDown_key() {
   return down_key;
   }
   public int getUp_key() {
      return up_key;
   }
   public int getSpace_key() {
      return space_key;
   }
   public int getShift_key() {
      return shift_key;
   }
   public void setLeft_key(int left_key) {
      this.left_key = left_key;
   }
   public void setRight_key(int right_key) {
      this.right_key = right_key;
   }
   public void setDown_key(int down_key) {
      this.down_key = down_key;
   }
   public void setUp_key(int up_key) {
      this.up_key = up_key;
   }
   public void setSpace_key(int space_key) {
      this.space_key = space_key;
   }
   public void setShift_key(int shift_key) {
      this.shift_key = shift_key;
   }
   void FrameShow() {
    
    // Frame
    frame = new JFrame("키 설정");
     frame.setPreferredSize(new Dimension(350, 250));
     frame.setLocation(350, 250);
     frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // 문제가 생기 시 DISPOSE_ON_CLOSE로 해볼 것.
     frame.pack();
     frame.setVisible(true);
     

     centerPanel = new JPanel();
     contentPane = frame.getContentPane();

     // 패널에 버튼 6개 추가
     centerPanel.setLayout(new GridLayout(3, 2));
     for (int i = 0; i < 6; i++) {
      btn[i] = new JButton();

      centerPanel.add(btn[i]);
      BtnArrayListener bingoBtnClickListener = new BtnArrayListener();
      btn[i].addActionListener(bingoBtnClickListener);
     }
     
     // 버튼 text  
     btn[0].setText(" Left ");
     btn[1].setText(" Right ");
     btn[2].setText(" Down ");
     btn[3].setText(" Rotation ");
     btn[4].setText(" Drop ");
     btn[5].setText(" Hold ");

     btn[0].setName("b0");
     btn[1].setName("b1");
     btn[2].setName("b2");
     btn[3].setName("b3");
     btn[4].setName("b4");
     btn[5].setName("b5");
     
     
     // Frame Layout

     frame.getContentPane().add(BorderLayout.CENTER, centerPanel);
    }
 

 class TextClickListener implements MouseListener {
     public void mouseEntered(MouseEvent e) {
     }
   
     public void mouseExited(MouseEvent e) {
     }
   
     public void mousePressed(MouseEvent e) {
     }
   
     public void mouseReleased(MouseEvent e) {
      JTextField text = (JTextField) e.getSource();
      text.setText("");
     }
   
     public void mouseClicked(MouseEvent e) {
     }

 }

 // 6개 중 하나를 클릭했을 때 리스너

 class BtnArrayListener implements ActionListener {
    
    public void actionPerformed(ActionEvent event) {
       eventBtn = (JButton) event.getSource();   
      
       keyFrame = new JFrame("버튼변경");
       JPanel panel1 = new JPanel();
       JPanel panel2 = new JPanel();
   
       Container contentPane1 = keyFrame.getContentPane();
       contentPane1.setLayout(new BorderLayout());
    
   
       // 프레임 생성
       keyFrame.setPreferredSize(new Dimension(400, 150));
       keyFrame.setLocation(700, 250);
       keyFrame.pack();
      
       // 텍스트필드 생성
       keysetNumTxt = new JTextField("", 5);
       TextClickListener textClickListener = new TextClickListener();
       keysetNumTxt.addMouseListener(textClickListener);
   
       KeyPressed keyPressed = new KeyPressed();
       keysetNumTxt.addKeyListener(keyPressed); // 텍스트필드에 키가 입력되었을 때 리스너 호출
      
       if ("b0".equals( eventBtn.getName() ) ) {
          keyFrame.setVisible(true);
          // 레이블 생성
          keyLabel = new JLabel("Left 키를 입력하세요.");
          pk=0;
          
       } else if("b1".equals( eventBtn.getName() ) ) {
          keyFrame.setVisible(true);
          // 레이블 생성
          keyLabel = new JLabel("Right 키를 입력하세요.");
          pk=1;
         
       } else if ("b2".equals( eventBtn.getName() ) ) {
          keyFrame.setVisible(true);
          // 레이블 생성
          keyLabel = new JLabel("Down 키를 입력하세요.");
          pk=2;
         
       } else if ("b3".equals( eventBtn.getName() ) ) {
          keyFrame.setVisible(true);
          // 레이블 생성
          keyLabel = new JLabel("Rotation 키를 입력하세요.");
          pk=3;
         
       } else if ("b4".equals( eventBtn.getName() ) ) {
          keyFrame.setVisible(true);
          // 레이블 생성
          keyLabel = new JLabel("Drop 키를 입력하세요.");
          pk=4;
         
       } else if ("b5".equals( eventBtn.getName() ) ) {
          keyFrame.setVisible(true);
          // 레이블 생성
          keyLabel = new JLabel("Hold 키를 입력하세요.");
          pk=5;
       }
      
      // 패널 추가
      
      panel1.add(keyLabel);
      panel2.add(keysetNumTxt);
   
      // 프레임 레이아웃
   
      keyFrame.getContentPane().add(BorderLayout.NORTH, panel1);
      keyFrame.getContentPane().add(BorderLayout.CENTER, panel2);
     }
 }

 class BtnClickListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
       eventBtn = (JButton) event.getSource();
    }
 }
 
 // 버튼에서 불러온 프레임에 키입력시 리스너

 class KeyPressed implements KeyListener{
    public void keyPressed(KeyEvent e) {
       BtnArrayListener btnArray = new  BtnArrayListener();

         
//       keyString = ""; // 입력 후 텍스트 전역변수 초기화
       System.out.println("=======");
       keyString = e.getKeyText(e.getKeyCode());
       keyCode = e.getKeyCode();
       System.out.println(e.getKeyCode());
       if (e.getKeyCode() != 10) {
          if (pk == 0) {
             if(keyCode == right_key) {
                warning_Right();
             } else if(keyCode == up_key) {
                warning_Up();
             } else if(keyCode == down_key) {
                warning_Down();
             } else if(keyCode == space_key) {
                warning_Drop();
             } else if(keyCode == shift_key) {
                warning_Hold();
             } else {
                setLeft_key(keyCode);
                btn[0].setText("Left : "+ keyString);
                keyFrame.setVisible(false); // 입력창 종료
                }
             
          } else if (pk == 1){
             if (keyCode == left_key) {
                warning_Left();                
             } else if(keyCode == up_key) {
                warning_Up();
             } else if(keyCode == down_key) {
                warning_Down();
             } else if(keyCode == space_key) {
                warning_Drop();
             } else if(keyCode == shift_key) {
                warning_Hold();
             } else {
                setRight_key(keyCode);
                btn[1].setText("Right : "+ keyString);
                keyFrame.setVisible(false); // 입력창 종료   
                }
             
          } else if (pk == 2){
             if (keyCode == left_key) {
                warning_Left();                
             } else if(keyCode == right_key) {
                warning_Right();
             } else if(keyCode == up_key) {
                warning_Up();
             } else if(keyCode == space_key) {
                warning_Drop();
             } else if(keyCode == shift_key) {
                warning_Hold();
             } else {
                setDown_key(keyCode);
                btn[2].setText("Down : "+ keyString);
                keyFrame.setVisible(false); // 입력창 종료   
                }
             
          } else if (pk == 3){
             if (keyCode == left_key) {
                warning_Left();                
             } else if(keyCode == right_key) {
                warning_Right();
             } else if(keyCode == down_key) {
                warning_Down();
             } else if(keyCode == space_key) {
                warning_Drop();
             } else if(keyCode == shift_key) {
                warning_Hold();
             } else {
                setUp_key(keyCode);
                btn[3].setText("Rotation : "+ keyString);
                keyFrame.setVisible(false); // 입력창 종료   
                }
             
          } else if (pk == 4){
             if (keyCode == left_key) {
                warning_Left();                
             } else if(keyCode == right_key) {
                warning_Right();
             } else if(keyCode == up_key) {
                warning_Up();
             } else if(keyCode == down_key) {
                warning_Down();
             } else if(keyCode == shift_key) {
                warning_Hold();
             } else {
                setSpace_key(keyCode);
                btn[4].setText("Drop : "+ keyString);
                keyFrame.setVisible(false); // 입력창 종료   
                }
             
          } else if (pk == 5){
             if (keyCode == left_key) {
                warning_Left();                
             } else if(keyCode == right_key) {
                warning_Right();
             } else if(keyCode == up_key) {
                warning_Up();
             } else if(keyCode == down_key) {
                warning_Down();
             } else if(keyCode == space_key) {
                warning_Drop();
             } else {
                setShift_key(keyCode);
                btn[5].setText("Hold : "+ keyString);
                keyFrame.setVisible(false); // 입력창 종료   
                }
          } else {keyFrame.setVisible(false);
          }
      }
    }
    
   public void warning_Left() {
      JOptionPane.showMessageDialog(null, "left_key 와 중복됩니다. 다른 키를 입력해주세요.", "중복된 key", JOptionPane.ERROR_MESSAGE);
   }
   public void warning_Right() {
      JOptionPane.showMessageDialog(null, "right_key 와 중복됩니다. 다른 키를 입력해주세요.", "중복된 key", JOptionPane.ERROR_MESSAGE);
   }
   public void warning_Up() {
      JOptionPane.showMessageDialog(null, "up_key 와 중복됩니다. 다른 키를 입력해주세요.", "중복된 key", JOptionPane.ERROR_MESSAGE);
   }
   public void warning_Down() {
      JOptionPane.showMessageDialog(null, "down_key 와 중복됩니다. 다른 키를 입력해주세요.", "중복된 key", JOptionPane.ERROR_MESSAGE);
   }
   public void warning_Drop() {
      JOptionPane.showMessageDialog(null, "drop_key 와 중복됩니다. 다른 키를 입력해주세요.", "중복된 key", JOptionPane.ERROR_MESSAGE);
   }
   public void warning_Hold() {
      JOptionPane.showMessageDialog(null, "hold_key 와 중복됩니다. 다른 키를 입력해주세요.", "중복된 key", JOptionPane.ERROR_MESSAGE);
   }

   @Override
   public void keyReleased(KeyEvent e) {
      // TODO Auto-generated method stub
   }
   
   @Override
   public void keyTyped(KeyEvent e) {
      // TODO Auto-generated method stub
   }
  }
 }