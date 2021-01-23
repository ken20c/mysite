import java.awt.*;
import java.awt.event.*;
 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

// import static Constant.*;

public class Display extends Frame implements ActionListener{
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400; 
    
    /**
     * MyConst
     */
    static final int NA = 4;
    static final int NM = 21;

    // 「ボタン」のオブジェクトを宣言
    Button btAdd, btSch, btOpe;
    // 「ラベル」のオブジェクトを宣言
    Label lb1;

    // インスタンスが作成されているか判断
    boolean menu1 = true;
    boolean menu2 = true;
    boolean menu3 = true;

    // 曲数を格納する変数
    int numMusic, numMusic2;
  
    public static void main(String [] args) {
    Display ft = new Display(); 
    }
    // ■ コンストラクタ
    Display() {
    super("今の気分に合うものを探せる曲検索アプリ");
    // BorderLayout を使用　左右、上下の間隔を数値で指定
    this.setLayout(new BorderLayout(5, 5));
    this.setSize(WIDTH, HEIGHT); 

    Panel p_center = new Panel();
    p_center.setLayout(new GridLayout(1, 3));
    
    btAdd = new Button("曲を追加");   p_center.add(btAdd);
    btSch = new Button("曲を検索");   p_center.add(btSch);  
    btOpe = new Button("曲を管理");   p_center.add(btOpe);

    btAdd.addActionListener(this); // ActionListener に登録
    btSch.addActionListener(this); // ActionListener に登録
    btOpe.addActionListener(this); // ActionListener に登録

    add(p_center, "Center"); // ボタンをフレームに追加
    // ペインから取得しなくても自動的にやってくれる

    Connection conn = null; // データベースとの接続用
    String servername = "localhost"; // サーバネーム
    String databasename = "songbank"; // データベース名
    String user = "root"; // ユーザ名
    String password = ""; // パスワード
    String url = "jdbc:mysql://" + servername + "/" + databasename + "?serverTimezone=JST"; // データベースのURL

          
    /**　■■■■■ データベースから曲数を取得する ■■■■■
     * prepareStatement(String sql)
     * パラメータ付きSQL文をデータベースに送るための
     * PreparedStatementオブジェクトを生成
     */
    try {
	System.out.println("Connected...."); //接続中
	// 特定のデータベースに接続する
	conn = DriverManager.getConnection(url, user, password);
	// SQL文をデータベースに送るためのStatementオブジェクトを生成
	Statement stmt = conn.createStatement();
	// sqlへ送信する文
	String sql = "SELECT COUNT(*) FROM jpop";
	sql = sql + ";";
        
	/**
	 * ResultSetオブジェクトは、カーソルがデータの現在の行を指し示すよう維持する
	 * 初期状態では、カーソルは最初の行の先頭に配置
	 * nextメソッドにより、カーソルは次の行に移動
	 * nextは、ResultSetオブジェクトにそれ以上行がない場合にfalseを返すので、
	 * whileループに使用して結果セットを反復処理することができる。
	 */
	ResultSet rs = stmt.executeQuery(sql);
	System.out.println(sql);
	rs.next(); // 0行目を指しているのでnext() 関数で送る
	System.out.println(rs.getInt(1)); // 曲数を出力
        numMusic = rs.getInt(1); // 曲数を変数に保存

	// DISTINCT() でタイトルが重複しているものを省く
	sql = "SELECT COUNT(DISTINCT(title)) FROM jpop;";
	rs = stmt.executeQuery(sql);
	rs.next();
	System.out.println(rs.getInt(1));
        numMusic2 = rs.getInt(1);
	
    } catch (SQLException e2) {
	System.out.println("Connection Failed. : " + e2.toString());
	throw new RuntimeException(e2);
    }
    // ラベルで収録曲数を表示する    
    lb1 = new Label("収録曲数 : " + numMusic + " (" + numMusic2 + ")");
    this.add(lb1, "North"); // 曲数のラベルを右上に追加
   
    this.setVisible(true);
    }   
    
    // ボタンが押されたときに以下のメソッドが呼ばれる
    public void actionPerformed(ActionEvent e) {
        
        //System.out.println("param=" + e.paramString());
        // Add ボタンが押され、Add メニューがまだ開いていない
        if(e.getSource() == btAdd && menu1){ 
            menu1 = false; // Add を開いたことを伝える
            Add add = new Add("新規楽曲追加"); // Add フレーム呼び出し
            add.setVisible(true);
        }
        if(e.getSource() == btSch && menu2){
            menu2 = false; // Sch を開いたことを伝える
            Search sch = new Search("曲検索画面"); // Search フレーム呼び出し
            sch.setVisible(true);
        }
    }
}
