import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class Server extends JFrame {

    ServerSocket server ;
    Socket socket;
    BufferedReader br ;
    PrintWriter pw ;

    //Constructor

    // components for gui
    private JLabel heading = new JLabel();
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Arial", Font.PLAIN, 25);

    public Server (){
        try {
            server = new ServerSocket(7776);
            System.out.println("Server is ready to accept connention");
            System.out.println("Server is waiting");
            socket = server.accept();

            // for reading
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // for writing
            pw = new PrintWriter(socket.getOutputStream());

            //Creating gui 
            createGUI();
            handleEvent();

            startReading();
            // startWriting();


        } catch (Exception e) {
            // e.printStackTrace();
        }
    }


    
    // GUI code
    private void createGUI(){

        //title at top
        this.setTitle("Server END");
        // width n height of gui
        this.setSize(500,700);
        // for centre gui at window
        this.setLocationRelativeTo(null);
        //cross for exit
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //code for components

        //font
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        //heading adjustment
        
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.CENTER);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setText("Server Area");
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        //messageArea
        messageArea.setEditable(false);

        //frames layout
        this.setLayout(new BorderLayout()); // has 5 com->N-C-S, E-W
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        //for visibility of gui
        this.setVisible(true);
    }

    private void handleEvent(){
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode()==10){
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me : " +contentToSend + "\n");
                    pw.println(contentToSend);
                    pw.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }

    
    public void startReading(){
        // This thread will read data.
        Runnable r1 =()->{
            System.out.println("Reader get started");

            try {


            while (true) { 

                String msg = br.readLine();
                if(msg.equals("exit")){
                    System.out.println("Client has left the chat!");
                    JOptionPane.showMessageDialog(this, "Client terminated the chat!");
                    messageInput.setEnabled(false);
                    socket.close();
                    break;
                }

                messageArea.append("Client : " +msg +"\n");
            }
                
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("Connection closed");
            }
        };
        new Thread(r1).start();
    }

    public void startWriting(){
        // This thread will take user data n send it to client.
        Runnable r2 =()->{
            System.out.println("Writer started");

            try { 

            while(!socket.isClosed()){
                    
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    pw.println(content);
                    pw.flush();

                    if(content.equals("exit")){
                        socket.close();
                        break;
                    }
                    
            }
            System.out.println("Connection closed");
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("Connection closed");
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is server !!");
        new Server(); // Start the server
    }
}