/*
 * 文件名：		CheckDialog.java
 * 类名：		CheckDialog
 * 创建日期：	2013-07-11
 * 最近修改：	2013-07-19
 * 作者：		徐犇
 */
package check;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

/**
 * 执行md5、sha-1、crc32等编码计算的对话框，可以对文件进行运算，也可对文本进行
 * @author ben
 * 
 */
@SuppressWarnings("serial")
public final class CheckDialog extends JDialog {

	/**
	 * 内部使用的缓冲区
	 */
	private byte[] buffer = new byte[1024];

	/**
	 * 输出信息域
	 */
	private JTextArea outputArea = new JTextArea();
	
	/**
	 * 用于显示当前所选文件的文本域
	 */
	private JTextField fileField = new JTextField("");
	
	/**
	 * 是否进行MD5运算的标记
	 */
	private JCheckBox md5check = new JCheckBox("MD5");
	
	/**
	 * 是否进行SHA-1运算的标记
	 */
	private JCheckBox sha1check = new JCheckBox("SHA-1");
	
	/**
	 * 是否进行CRC32运算的标记
	 */
	private JCheckBox crc32check = new JCheckBox("CRC32"); 
	
	/**
	 * 是否转成大写形式的标记
	 */
	private boolean uppercase = false;
	
	private JPanel getNorthPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout(5, 0));
		JLabel fil = new JLabel("文件:");
		ret.add(BorderLayout.WEST, fil);
		fileField.setEditable(false);
		fileField.setBackground(Color.WHITE);
		ret.add(BorderLayout.CENTER, fileField);

		/*
		 * 浏览文件的按钮
		 */
		JButton buttonScan = new JButton("浏览...");
		buttonScan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onButtonScan();
			}
		});
		
		ret.add(BorderLayout.EAST, buttonScan);
		
		return ret;
	}
	
	private JPanel getCenterPanel() {
		outputArea.setEditable(false);
		
		JPanel ret = new JPanel();
		ret.setLayout(new GridLayout(1, 1));
		ret.add(new JScrollPane(outputArea));
		return ret;
	}

	private JPanel getSouthWestPanel() {
		JPanel ret = new JPanel();
		JRadioButton low = new JRadioButton("小写显示");
		low.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				uppercase = false;
			}
		});
		ret.add(low);
		JRadioButton up = new JRadioButton("大写显示");
		up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				uppercase = true;
			}
		});
		ret.add(up);		
		ButtonGroup bg = new ButtonGroup();
		bg.add(low);
		bg.add(up);
		low.setSelected(true);
		return ret;
	}
	
	private JPanel  getSouthCenterPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new GridLayout(1, 1));
//		ret.setLayout(new BorderLayout(0, 0));
		
//		JLabel label = new JLabel("算法:");
//		ret.add(label, BorderLayout.WEST);
		
		JPanel center = new JPanel();
//		center.setLayout(new GridLayout(1, 3));
		center.add(md5check);
		center.add(sha1check);
		center.add(crc32check);
		
		ret.add(center);
		
//		ret.add(center, BorderLayout.CENTER);
		
		return ret;
	}
	
	private JPanel getSouthPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout(0, 0));
//		ret.setLayout(new GridLayout(1, 2));
		
		ret.add(getSouthWestPanel(), BorderLayout.WEST);
		ret.add(getSouthCenterPanel(), BorderLayout.CENTER);
		
		JPanel right = new JPanel();
		right.setLayout(new GridLayout(1, 2));
		JButton comput = new JButton("运行");
		comput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				onButtonComput();
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		right.add(comput);
		JButton clear = new JButton("清空");
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onButtonClear();
			}
		});
		right.add(clear);
		ret.add(right, BorderLayout.EAST);
		
		return ret;
	}

	private CheckDialog(JFrame frame) {
		super(frame, true);

		/*
		 * 图形面板
		 */
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(BorderLayout.NORTH, getNorthPanel());
		panel.add(BorderLayout.CENTER, getCenterPanel());
		panel.add(BorderLayout.SOUTH, getSouthPanel());
		Container con = this.getContentPane();
		con.add(panel);
		
		/*
		 * 通过得到屏幕尺寸，计算得到坐标，使对话框在屏幕上居中显示
		 */
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final int width = 500;
		final int height = 309;
		final int left = (screen.width - width) / 2;
		final int top = (screen.height - height) / 2;
		this.setTitle("编码校验对话框");
		this.setLocation(left, top);
		this.setSize(width, height);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
	private boolean onButtonScan() {
		/*
		 * 用JFileChooser设置文件打开对话框，由于只要选择文件夹，
		 * 设置文件选中的模式为文件即JFileChooser.FILES_ONLY
		 */
		String filePath = new String();
		try {
			JFileChooser fileChooser = new JFileChooser(".");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int n = fileChooser.showOpenDialog(this);
			if (n == JFileChooser.APPROVE_OPTION) {
				filePath = fileChooser.getSelectedFile().getPath();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		fileField.setText(filePath);		
		return true;
	}
	
	private void onButtonComput() {
		
//		long t = System.currentTimeMillis();
//		doOnce();
		/*
		 * 经过测试
		 * 缓冲区设为102400，同时运行平均时间：104798 ms.
		 * 缓冲区设为1024，同时运行平均时间：122364 ms.
		 * 缓冲区设为1024，独立运行平均时间累计：150791 ms.
		 * 区别不大，为编程方便，采用分别独立运行的方法
		 */
		String str = doSeperate();
		if(uppercase) {
			str = str.toUpperCase();
		}else {
			str = str.toLowerCase();
		}
		outputArea.append(str);
//		t = System.currentTimeMillis() - t;
//		System.out.println(t);
	}
	
	private void onButtonClear() {
		outputArea.setText("");
	}
	
	private String doSeperate() {
		File file = new File(fileField.getText().trim());
		StringBuilder sb = new StringBuilder("文件:\t");
		
		sb.append(fileField.getText().trim());
		sb.append('\n');
		
		BigInteger bint;
		
		if (md5check.isSelected()) {
			bint = getFileMD5(file);
			sb.append("MD5:\t");
			sb.append(bint.toString(16));
			sb.append('\n');
		}

		if (sha1check.isSelected()) {
			bint = getFileSHA1(file);
			sb.append("SHA-1:\t");
			sb.append(bint.toString(16));
			sb.append('\n');
		}

		if (crc32check.isSelected()) {
			bint = getFileCRC32(file);
			sb.append("CRC32:\t");
			sb.append(bint.toString(16));
			sb.append('\n');
		}
		
		sb.append("\n\n\n");
		return sb.toString();
	}
	
	@SuppressWarnings("unused")
	private String doOnce() {
		File file = new File(fileField.getText().trim());
		StringBuilder sb = new StringBuilder("文件:\t");
		sb.append(fileField.getText().trim());
		sb.append('\n');
		BigInteger bimd5, bisha1, bicrc32; 
		
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
			CRC32 crc32 = new CRC32();
			
			FileInputStream fis = new FileInputStream(file);
			md5.reset();
			sha1.reset();
			crc32.reset();
			
			int len;
			while ((len = fis.read(buffer)) != -1) {
				md5.update(buffer, 0, len);
				sha1.update(buffer, 0, len);
				crc32.update(buffer, 0, len);
			}
			fis.close();
			bimd5 = new BigInteger(1, md5.digest());
			bisha1 = new BigInteger(1, sha1.digest());
			bicrc32 = BigInteger.valueOf(crc32.getValue());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		sb.append("MD5:\t");
		sb.append(bimd5.toString(16));
		sb.append('\n');
		
		sb.append("SHA-1:\t");
		sb.append(bisha1.toString(16));
		sb.append('\n');
		
		sb.append("CRC32:\t");
		sb.append(bicrc32.toString(16));
		sb.append('\n');
		
		return sb.toString();
	}
	
	private BigInteger getTextDigest(String text, String algo) throws NoSuchAlgorithmException {
		if(text == null) {
			return null;
		}
		MessageDigest md = MessageDigest.getInstance(algo);
		md.reset();
		md.update(text.getBytes());
		return new BigInteger(1, md.digest());
	}
	
	
	/**
	 * 返回一个文件的摘要值
	 * 
	 * @param f
	 *            所需处理的文件
	 * @param algo
	 *            使用的算法名称
	 * @return 摘要值大整数表示形式
	 * @throws NoSuchAlgorithmException 
	 */
	private BigInteger getFileDigest(File f, String algo) throws NoSuchAlgorithmException {
		if (f == null || !f.exists() || !f.isFile() || !f.canRead()) {
			return null;
		}
		try {
			MessageDigest md = MessageDigest.getInstance(algo);
			FileInputStream fis = new FileInputStream(f);
			md.reset();
			int len;
			while ((len = fis.read(buffer)) != -1) {
				md.update(buffer, 0, len);
			}
			fis.close();
			BigInteger bint = new BigInteger(1, md.digest());
			return bint;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BigInteger getTextMD5(String text) {
		try {
			return getTextDigest(text, "MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public BigInteger getTextSHA1(String text) {
		try {
			return getTextDigest(text, "SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public BigInteger getFileMD5(File f) {
		try {
			return getFileDigest(f, "MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public BigInteger getFileSHA1(File f) {
		try {
			return getFileDigest(f, "SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public BigInteger getTextCRC32(String text) {
		if(text == null) {
			return null;
		}
		CRC32 crc32 = new CRC32();
		crc32.reset();
		crc32.update(text.getBytes());
		return BigInteger.valueOf(crc32.getValue());
	}
	
	public BigInteger getFileCRC32(File f) {
		if (f == null || !f.exists() || !f.isFile() || !f.canRead()) {
			return null;
		}
		CRC32 crc32 = new CRC32();
		try {
			FileInputStream fis = new FileInputStream(f);
			crc32.reset();
			int len;
			while ((len = fis.read(buffer)) != -1) {
				crc32.update(buffer, 0, len);
			}
			fis.close();
			BigInteger bint = BigInteger.valueOf(crc32.getValue());
			return bint;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void showFileCheckDialog(JFrame frame) {
		new CheckDialog(frame);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// File f = new File("G:\\mathworks r2011b.iso");
		// File f = new File("H:\\CPlusPlus.java");
		showFileCheckDialog(null);
	}

}
