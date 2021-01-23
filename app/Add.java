/**
 * 曲を追加するモード
 *
 * ラベル01 タイトル名
 * ラベル02 アーティスト 4 人(まで) 
 * チェックボックス01 21 種類
 * ボタン01 追加
 * ボタン02 入力リセット
 *
 */
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.io.*;

class Add extends JFrame implements ActionListener{ // JFrame クラスのサブクラスを作ることで独自のメソッドも実現する
     /**
     * MyConst
     */
    static final int NA = 4; // アーティスト数
    static final int NM = 21; // ムード数

    /**
     * フィールド変数
     */
    // (ActionEvent 関数でも使えるように、クラスのすぐ下で宣言を行う)
    JPanel disp = new JPanel();             // 全てのcomponent はここに入る
    // タイトル
    JPanel pT = new JPanel();               // ラベルとフィールドを一まとめに
    JLabel lTitle = new JLabel("タイトル");  // タイトルのラベル
    JTextField tTitle = new JTextField(20); // タイトルのフィールド
    // アーティスト
    JPanel pA[] =     new JPanel[NA];     // ラベルとフィールドを一まとめに
    JLabel lA[] =     new JLabel[NA];     // アーティストのラベル
    JTextField tA[] = new JTextField[NA]; // アーティストのテキストフィールド
    // ムード
    JCheckBox mood[] = new JCheckBox[NM]; // ムードのチェックボックス
    // その他
    JLabel success = new JLabel();              // 追加完了をお知らせ
    JButton get = new JButton("追加する");       // 追加するボタン
    JButton reset = new JButton("リセットする");  // 入力をリセットする
    DataBox db = new DataBox();                 // 入力データを一時保存

    /**
     * コンストラクタ
     * @param title // フレームのタイトルを取得
     */
    Add(String title){
        /**
         * フレーム　の設定
         */
        setTitle(title);   // タイトルを設定
        setSize(600, 800); // 幅、高さを設定
        setLocationRelativeTo(null); // 表示位置を画面の中央の設定
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        // バツボタンで終了
        disp.setLayout(new BoxLayout(disp, BoxLayout.Y_AXIS)); // BoxLayout に変更

        // タイトル のパネル
        pT.setLayout(new BoxLayout(pT, BoxLayout.LINE_AXIS)); // BoxLayout に変更
        pT.add(lTitle); pT.add(tTitle); // パネルにラベルとフィールドを追加
        // アーティスト のパネル
        for(int i=0; i<NA; i++){
            pA[i] = new JPanel(); // 新しいパネルを作成(宣言済み)
            pA[i].setLayout(new BoxLayout(pA[i], BoxLayout.LINE_AXIS)); // BoxLayout に変更
            lA[i] = new JLabel("アーティスト" + (i+1)); // ラベル作成
            tA[i] = new JTextField(20);         // テキストフィールド作成
            pA[i].add(lA[i]); pA[i].add(tA[i]); // パネルにラベルとフィールドを追加
        }
        disp.add(pT);            // タイトル　　のパネルを全体のパネルに追加
        for(int i=0; i<NA; i++){ // アーティストのパネルを全体のパネルに追加
            disp.add(pA[i]);
        }

        // ムード　のパネル
        JLabel lM = new JLabel("どんな気分の時に聞きたい曲ですか？");
        disp.add(lM); // 説明ラベルを追加する
	// CheckBox を一つずつ定義する
        mood[0] = new JCheckBox("Confident(自信)");
        mood[1] = new JCheckBox("Satisfied(満足)");
        mood[2] = new JCheckBox("Cheerful(元気、陽気、noticeably happy)");
        mood[3] = new JCheckBox("Enthusiastic(熱狂的)");
        mood[4] = new JCheckBox("Optimistic(楽観的)");
        mood[5] = new JCheckBox("Irritated(イライラ)");
        mood[6] = new JCheckBox("Hateful(不快、妬み)");
        mood[7] = new JCheckBox("Apprehensive(不安)");
        mood[8] = new JCheckBox("Afraid(恐怖)");
        mood[9] = new JCheckBox("Guilty(罪悪感)");
        mood[10] = new JCheckBox("Ashamed(恥ずかしい)");
        mood[11] = new JCheckBox("Lonely(孤独)");
        mood[12] = new JCheckBox("Lethargic(無気力)");
        mood[13] = new JCheckBox("Bored(退屈)");
        mood[14] = new JCheckBox("Serene(calm,穏やか)");
        mood[15] = new JCheckBox("Chill");
        mood[16] = new JCheckBox("Peaceful(平和、安泰)");
        mood[17] = new JCheckBox("Crave(渇望)");
        mood[18] = new JCheckBox("Trusting(信頼)");
        mood[19] = new JCheckBox("Appreciated(感謝)");
        mood[20] = new JCheckBox("Touched(感動)");
	// ムードのチェックボックスを追加する
        for(int i=0; i<NM; i++)
            disp.add(mood[i]);      
        // 情報取得用のボタン
        get.addActionListener(this);   disp.add(get);   // 追加する
	// リセット用のボタンをパネルに追加
	reset.addActionListener(this); disp.add(reset); // 追加する
	// 結果表示のラベルをパネルに追加
	disp.add(success);

	// フレーム(Addクラス)からペインを取得する
        Container contentPane = getContentPane();
	// ペインに対してコンポーネント(disp パネル)を追加する
        contentPane.add(disp, BorderLayout.NORTH);
    }
    
    /**
     * メイン関数
     * @param args
     * 自身のクラスのオブジェクトを作り出す
     * 本来はDisplay クラスから呼び出されるため、テスト用にのみ作成したmain 関数
     */
    public static void main(String args[]){
        Add frame = new Add("曲追加画面"); // クラスのインスタンス作成
        frame.setVisible(true); // 可視化する      
    }

    /**
     * ボタンが押された時に呼び出される関数
     * 1. get
     *    入力された情報をDataBoxクラスのオブジェクトに格納して、
     *    これをデータベースに送信する
     * 2. reset
     *    全てのテキストフィールドをクリアし、
     *    全てのチェックボックスを非選択状態にする
     */
    public void actionPerformed(ActionEvent e){
	// 1. get
	if(e.getSource() == get){
	    // メインアーティストとタイトルをを取得し、画面に表示する
	    success.setText(tA[0].getText() + " - " + tTitle.getText() ); 
	    // DataBox のオブジェクトにデータを一時保存
	    db.title = tTitle.getText();
	    for (int i=0; i<NA; i++){
		db.artist[i] = tA[i].getText();
	    }
	    for(int i=0; i<NM; i++){
		// チェックボックスが押されていたらtrue
		db.mood[i] = mood[i].isSelected();
	    }
	    /* ファイルに保存する場合の関数
	      try {
	      // FileWriterクラスのオブジェクトを生成する
	      FileWriter file = new FileWriter("Database.txt", true);
	      // PrintWriterクラスのオブジェクトを生成する
	      PrintWriter pw = new PrintWriter(new BufferedWriter(file));
            
	      //ファイルに追記する
	      pw.print(b.title);
	      pw.print(", ");
	      for(int i=0; i<NA; i++){
	      pw.print(b.artist[i] + ", ");
	      }
	      for(int i=0; i<NM; i++){
	      pw.print(b.mood[i] + ", ");
	      }

	      pw.println("");
            
	      //ファイルを閉じる
	      pw.close();
	      System.out.println("書き込み完了しました");
	      } catch (IOException e2) {
	      e2.printStackTrace();
	      System.out.println("書き込みできませんでした");
	      }
	    */
	    // データベースと接続
	    Connection conn = null; // データベースとの接続用
	    String servername = "localhost"; // サーバネーム
	    String databasename = "songbank"; // データベース名
	    String user = "root"; // ユーザ名
	    String password = ""; // パスワード
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
		// テキストデータに「'」があるとSQL の構文の特性上エラーが起きるため
		// 「''」に変換する作業を行う
		db.title = db.title.replaceAll("'","''");
		System.out.println(db.title); // 確認用	    
		for(int i=0; i<NA; i++){
		    db.artist[i] = db.artist[i].replaceAll("'","''");
		}	    
		// sql 文を作成
		// 詳細はrhe.hatenadiary.com に記載
		String sql = "INSERT INTO jpop VALUES (NULL,'" + db.title;
		// Artist シリーズ
		for(int i=0; i<4; i++)
		    sql = sql + "', '" + db.artist[i];
		sql = sql + "'"; // 'アーティスト4' の右側を閉じてあげる
		// ムードシリーズ
		for(int i=0; i<NM; i++)
		    sql = sql + ", " + db.mood[i];
		sql = sql + ");"; // 「1, 0, ..., 1); 」の); をつけてあげる	    
		System.out.println(sql); // sql文の完成形を確認             
	    } catch (SQLException e3) { // SQLとの通信上のエラー
		System.out.println("Connection Failed. : " + e3.toString());
		throw new RuntimeException(e3);
	    }
	}

	if (e.getSource() == reset){ // リセットボタンが押されたとき
	    tTitle.setText(""); // タイトルクリア
	    for(int i=0; i<NA; i++)
		tA[i].setText(""); // アーティスト全てクリア
	    for(int i=0; i<NM; i++)
		mood[i].setSelected(false); // チェックを全て外す
	}
    }
}


/**
 * データ保存用のクラス
 */
class DataBox {
    /**
     * MyConst
     */
    static final int NA = 4;
    static final int NM = 21;

    String title;
    String artist[] = new String[NA];
    Boolean mood[] = new Boolean[NM]; // nMoodに

    DataBox(){}
}
