/**
 * 曲を検索するための画面のクラス
 */
import javax.swing.*;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.awt.Desktop;
import java.net.URI;

public class Search extends JFrame implements ActionListener{
    /**
     * MyConst
     */
    static final int NA = 4;  // アーティスト数
    static final int NM = 21; // ムード数
    
    /**
     * フィールド変数
     */
    JPanel disp = new JPanel();               // 全てのcomponent はここに入る
    JLabel c = new JLabel("ムードを選択");      // 説明のラベル
    JCheckBox[] CBMood = new JCheckBox[NM];   // ムードのチェックボックス
    boolean[] select = new boolean[NM];       // 選択の有無を格納する
    JButton done = new JButton("検索する");    // 検索実行ボタン
    JButton jb = new JButton("もう一度検索");   // 再検索ボタン
    JButton reset = new JButton("リセット");   // チェックを全て外す

    DataBox db[] = new DataBox[100];  // 現存の曲をロードする
    JButton[] res = new JButton[100]; // 検索結果(result)
    String[] uriStr = new String[100]; // res の動画リンク
    int num = 0; // 検索結果をカウントする

    
    String[] moodN = new String[NM];  // ムードの名前を格納する
    
    /**
     * コンストラクタ
     */
    Search(String title){
        /**
         * フレーム　の設定
         */
        setTitle(title);
        setSize(600, 600); // 幅、高さを設定
        setLocationRelativeTo(null); // 表示位置を画面の中央の設定
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // バツボタンでプログラム終了
	disp.setLayout(new BoxLayout(disp, BoxLayout.Y_AXIS)); // BoxLayout に変更

	// ムードの名前を格納(データベース側)
        moodN[0] = "Confident";
        moodN[1] = "Satisfied";
        moodN[2] = "Cheerful";
        moodN[3] = "Enthusiastic";
        moodN[4] = "Optimistic";
        moodN[5] = "Irritated";
        moodN[6] = "Hateful";
        moodN[7] = "Apprehensive";
        moodN[8] = "Afraid";
        moodN[9] = "Guilty";
        moodN[10] = "Ashamed";
        moodN[11] = "Lonely";
        moodN[12] = "Lethargic";
        moodN[13] = "Bored";
        moodN[14] = "Serene";
        moodN[15] = "Chill";
        moodN[16] = "Peaceful";
        moodN[17] = "Crave";
        moodN[18] = "Trusting";
        moodN[19] = "Appreciated";
        moodN[20] = "Touched";

	// チェックボックスを作成(ユーザ側)
        CBMood[0] = new JCheckBox("Confident(自信)");
        CBMood[1] = new JCheckBox("Satisfied(満足)");
        CBMood[2] = new JCheckBox("Cheerful(元気、陽気、noticeably happy)");
        CBMood[3] = new JCheckBox("Enthusiastic(熱狂的)");
        CBMood[4] = new JCheckBox("Optimistic(楽観的)");
        CBMood[5] = new JCheckBox("Irritated(イライラ)");
        CBMood[6] = new JCheckBox("Hateful(不快、妬み)");
        CBMood[7] = new JCheckBox("Apprehensive(不安)");
        CBMood[8] = new JCheckBox("Afraid(恐怖)");
        CBMood[9] = new JCheckBox("Guilty(罪悪感)");
        CBMood[10] = new JCheckBox("Ashamed(恥ずかしい)");
        CBMood[11] = new JCheckBox("Lonely(孤独)");
        CBMood[12] = new JCheckBox("Lethargic(無気力)");
        CBMood[13] = new JCheckBox("Bored(退屈)");
        CBMood[14] = new JCheckBox("Serene(calm,穏やか)");
        CBMood[15] = new JCheckBox("Chill");
        CBMood[16] = new JCheckBox("Peaceful(平和、安泰)");
        CBMood[17] = new JCheckBox("Crave(渇望)");
        CBMood[18] = new JCheckBox("Trusting(信頼)");
        CBMood[19] = new JCheckBox("Appreciated(感謝)");
        CBMood[20] = new JCheckBox("Touched(感動)");

	// 初めに選択画面を表示する関数を呼び出す
	selMood();
    }

    /**
     * ボタンが押された時に呼び出される関数
     *
     * 1. selMood(Select Mood Function)
     *    今の気分に当てはまるものを選択する画面を表示
     *    検索ボタンを押すとdispRes 関数を呼び出す
     * 2. dispRes (Display Result)
     *    検索結果を表示する
     *    もう一度ボタンを押すと再度selMood 関数を呼び出す
     * 3. reset
     *    チェックボックスを全て非選択にする
     *
     * 4. link
     *    YouTube の動画をブラウザで開く
     */
    public void actionPerformed(ActionEvent e){
	if(e.getSource() == this.done) // 検索ボタンを押されたら
	    dispRes(); // 検索結果を表示
	if(e.getSource() == jb) { // もう一度検索するときは
	    selMood(); // 再度ムード選択画面を描画
	    num = 0; // 検索結果ボタンが消されるのでnum も0 にする
	}
	if(e.getSource() == reset){
	    for(int i=0; i<NM; i++)
		CBMood[i].setSelected(false);
	}
	for(int i=0; i<num; i++){
	    if(e.getSource() == res[i]){
		// URI を処理するためにアプリを起動できるようにする
		Desktop dt = Desktop.getDesktop();
		if(uriStr[i] != null ) // URL が登録されていれば
		try{
		    URI uri = new URI( uriStr[i] ); // URI を構築
		    dt.browse( uri );
		}catch( Exception e3 ){
		    e3.printStackTrace();
		}
	    }
		
	}
    }

    public void selMood(){
        /**
	 * ムード選択画面の関数
	 */
	disp.removeAll();   // 一度パネルの上を空にする
        disp.repaint();     // 再描画
        disp.add(c);        // ラベルをパネルに追加

	for(int i=0; i<NM; i++) // ムードのチェックボックスを順に追加
            disp.add(CBMood[i]);
        disp.add(done);  done.addActionListener(this);  // 検索ボタン
	disp.add(reset); reset.addActionListener(this); // リセットボタン

        // 全てのコンテンツをパネルに載せる
        Container contentPane = getContentPane();
        contentPane.add(disp, BorderLayout.NORTH);
	this.setVisible(true); // 表示が乱れるため、setVisible
	/**
	 * これを行わないと、画面を切り替える前にコンテンツがなかった部分は
	 * 表示する対象から外れてしまい見切れてしまう
	 */
    }
    
    public void dispRes(){
	/**
	 * 結果を表示する関数
	 */
	disp.removeAll(); // パネル上のcomponent を全て削除
        disp.repaint();   // 再描画(全部消える)

        for(int i=0; i<NM; i++){
            select[i] = CBMood[i].isSelected();
        }
          
        Connection conn = null;           // データベースとの接続用
        String servername = "localhost";  // サーバネーム
        String databasename = "songbank"; // データベース名
        String user = "root";             // ユーザ名
        String password = "";             // パスワード
       	String url = "jdbc:mysql://" + servername + "/" + databasename + "?serverTimezone=JST"; // データベースのURL
              
        /**　■■■■■ 入力データ取得後の関数 ■■■■■
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
	    boolean times = true; // 一回だけなら演算子"AND" は省略
	    String sql = "SELECT title, artist1, url FROM jpop WHERE";
	    for(int i=0; i<NM; i++){
		if (select[i] && times){ // 1回目のチェックボックスtrue なら
		    times = false; // 1回目がでたフラグを立てて
		    sql = sql + " " + moodN[i] + " = 1"; // ムード = 1 のSQL文を追加
		}
		else if (select[i]){ // 2回目以降でチェックボックスtrue なら
		    sql = sql + " AND " + moodN[i] + " = 1"; // AND ムード = 1 のSQL文を追加
		}
	    }
	    // チェックボックスが1つも指定がなければ全表示する
	    if(times) sql = "SELECT title, artist1, url FROM jpop";
	    sql = sql + ";"; // 最後にセミコロンをつける
        
	    /**
	     * ResultSetオブジェクトは、カーソルがデータの現在の行を指し示すよう維持する
	     * 初期状態では、カーソルは最初の行の先頭に配置
	     * nextメソッドにより、カーソルは次の行に移動
	     * nextは、ResultSetオブジェクトにそれ以上行がない場合にfalseを返すので、
	     * whileループに使用して結果セットを反復処理することができる。
	     */
	    ResultSet rs = stmt.executeQuery(sql);
	    System.out.println(sql);
	    
	    num = 0;
	    while(rs.next()){ // 初めにnext()関数をしないとエラーが起きるのでこれで良い
		String title = rs.getString("title");  // database からタイトル取得
		String artist = rs.getString("artist1"); // database からアーティスト取得
		uriStr[num] = rs.getString("url"); // URI に渡すURL
		
		System.out.println("ヒットした曲は：" + title); // タイトルの確認
		res[num] = new JButton(artist + " - " + title); // ボタンを作成
		res[num].setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)); // 横幅を最大限伸ばす
		disp.add(res[num]); // 作成したボタンをパネルに追加する
		res[num].addActionListener(this);
		num++; // ヒットした曲数をカウントする
	    }
	    JLabel jl = new JLabel("全 " + num + " 件ヒットしました");
	    jb.addActionListener(this);
	    disp.add(jl); disp.add(jb);
	    
	    Container contentPane = this.getContentPane(); // フレームから取得
	    contentPane.add(disp, BorderLayout.NORTH); // disp を追加する
	    //JScrollPane scrollpane = new JScrollPane(disp);
	    //getContentPane().add(scrollpane, BorderLayout.CENTER);
	    this.setVisible(true); // 可視化するために必ず必要。フレームに乗せたのでフレームを可視化する必要がある。
        
	   
        } catch (SQLException e2) {
            System.out.println("Connection Failed. : " + e2.toString());
            throw new RuntimeException(e2);
        }
    }
    // Display クラスから呼び出すのが正規ルートであるため、このメイン関数は試験用    
    public static void main(String args[]){
        Search sch = new Search("曲検索画面");
        sch.setVisible(true);
    }
}
