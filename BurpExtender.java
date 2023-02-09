package burp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.print.attribute.HashAttributeSet;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import com.sun.xml.internal.txw2.Document;

//ͨ��ʵ��ITab�Ľӿ������table
public class BurpExtender implements IBurpExtender,ITab,IContextMenuFactory,IHttpListener,IExtensionStateListener{
	//�������κεط�ʹ��
	public IBurpExtenderCallbacks cb;
	public IExtensionHelpers hp;
	public JSplitPane jsplitpane;
	public JTabbedPane jtabbedpane;
	public JTable table;
	public PrintWriter stdout;
	public JList<String> domainlist;
	public JList<String> APIlist;
	public JList<String> infolist;
	public JTextPane API_Text=new JTextPane();
	public Boolean OnorOff;
	public List<JMenuItem> listMenuItems;
	public String jm="off YitaiqiFilter";
	public JMenuItem jmOff;
	public String REGEX=".{10}[\"\'`](/[a-zA-Z0-9/=_{}\\?&!]+(\\.jspx|\\.jsp|\\.html|\\.php|\\.do|\\.aspx|\\.action|\\.json)*)[\"\'`].{160}";
	public HashMap<String, List> domain=new HashMap< String, List>();
	public List APIListtxt=new ArrayList<String>();
	//public String CachePath=System.getProperty("user.dir")+"/YitaiqiJSFilter/dataCace/";
	public String CachePath=System.getProperty("java.io.tmpdir")+"/YitaiqiJSFilter/dataCace/";
	//public String CachePath="D://"+"/YitaiqiJSFilter/dataCace/";
	public SimpleAttributeSet attrSet;
	public JScrollPane jpanAPI_but;
	public JTextPane Info_Text=new JTextPane();
	public HashMap<String, String> InfoRegMap=new HashMap< String, String>();
	public HashMap<String, List> InfoTexMap=new HashMap< String, List>();
	public HashMap<String, String> InfoandTexMap=new HashMap< String, String>();
	public ArrayList<String> infoisHas=new ArrayList<String>();
	public JTextField SearchField = new JTextField(16);
	public JButton SearchButton = new JButton("����");
	public JScrollPane jpaninfo;
	
	//Itab�Ľӿڣ����ر�ǩ������
	@Override
	public String getTabCaption() {
		// TODO Auto-generated method stub
		return "YitaiqiFilter";
	}
	//Itab�Ľӿڣ�����table��gui����
	@Override
	public Component getUiComponent() {
		// TODO Auto-generated method stub
		return jsplitpane;
	}
	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
		// TODO Auto-generated method stub
		//���ò������
		OnorOff=true;
		callbacks.setExtensionName("yitaiqiFilterJs");
		this.cb=callbacks;
		this.hp=callbacks.getHelpers();
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        try {
			this.stdout.println(changeCharset("����:yitaiqi"));
			this.stdout.println(changeCharset("���ں�:�ر���ǿ��ϸ�"));
	        this.stdout.println(changeCharset("###################################################################"));
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        
        callbacks.registerContextMenuFactory(this);
        callbacks.registerHttpListener(this);
        callbacks.registerExtensionStateListener(this);
        try {
			LoadConfig();
			LoadReg();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//����һ��Swing���̣߳�������ͨ��java��swt���������Ҫ���ſ���档
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				//�ָ����
				jsplitpane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
				jsplitpane.setDividerLocation(0.5);
				jsplitpane.setOneTouchExpandable(true);
			    //�б�+������
			    //����������ɫ
				attrSet = new SimpleAttributeSet();
				StyleConstants.setForeground(attrSet, Color.red);
				//StyleConstants.setFontSize(attrSet, fontSize);
			    domainlist = new JList<String>();
		        // ����һ����ѡ��С
		        // ����ɼ�ϵĶ�ѡ
		        domainlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		        // ����ѡ�����
		        domainlist.addListSelectionListener(new ListSelectionListener() {
					
					@Override
					public void valueChanged(ListSelectionEvent arg0) {
						// TODO Auto-generated method stub
						try {
							String c= domainlist.getSelectedValue();
							String tableText=jtabbedpane.getTitleAt(jtabbedpane.getSelectedIndex());
							if (tableText=="API�ӿ�") {
							String[] tmparray=(String[])domain.get(c).toArray(new String[0]);
							Arrays.sort(tmparray);
							APIlist.setListData(tmparray);
							}else if(tableText=="������Ϣ") {
							Info_Text.setText(null);
							infolist.setListData((String[])InfoTexMap.get(c).toArray(new String[0]));
						

						}
						}catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		        JScrollPane jScrollPane1 = new JScrollPane(domainlist);
                jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                //API�ϱߵ�ѡ����
                APIlist = new JList<String>();
                JScrollPane jpanAPI_top= new JScrollPane(APIlist);
                jpanAPI_top.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                jpanAPI_top.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                //APIlist˫��
                APIlist.addMouseListener(new MouseAdapter() {
                	public void mouseClicked(MouseEvent e) {
                		if(e.getClickCount()>=2) {
                			stdout.println("���Ƴɹ�");
                			setSysClipboardText(APIlist.getSelectedValue());
                		}
                	}
                
                
                });
                //APIlist����
                APIlist.addListSelectionListener(new ListSelectionListener() {
					
					@Override
					public void valueChanged(ListSelectionEvent arg0) {
						// TODO Auto-generated method stub
						String c= domainlist.getSelectedValue();
						//stdout.println(CachePath+domainlist.getSelectedValue()+"\\"+"thisApi.txt");
						File f=new File(CachePath+domainlist.getSelectedValue()+"\\"+"thisApi.txt");
						API_Text.setText(null);
						String tempString = null;
						javax.swing.text.Document dc= API_Text.getDocument();
						try {
							BufferedReader reader = new BufferedReader(new FileReader(f));
							 while((tempString=reader.readLine())!=null) {
								 if(tempString.startsWith(APIlist.getSelectedValue())) {
									 tempString=changeCharset(tempString);
									 //stdout.println(tempString);
									 String url="js�ű���ַ:\n"+tempString.split("###")[1];
									 //stdout.println("aaaaaaaaaaaaaaaaaaaaaaaaa!");
									 String api=tempString.split("###")[0];
									 String context=tempString.split("###")[2];
									 API_Text.setDocument(insaDocument(dc, context,api, url));
									 jpanAPI_but.updateUI();
									 break;
								 }
							 }
							 reader.close();
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
                //API�±ߵ�ѡ����
                //API_Text=new JTextPane();
                //API_Text.setEnabled(false);
                jpanAPI_but= new JScrollPane(API_Text);
                jpanAPI_but.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                jpanAPI_but.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                //ѡ��������Щ����Ե���Ϣ
                jpaninfo= new JScrollPane(Info_Text);
                jpaninfo.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                jpaninfo.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                //ѡ������infolist
                infolist = new JList<String>();
                JScrollPane jpaninfo_list= new JScrollPane(infolist);
                jpaninfo_list.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                jpaninfo_list.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                infolist.addListSelectionListener(new ListSelectionListener() {
					
					@Override
					public void valueChanged(ListSelectionEvent arg0) {
						Info_Text.setText(null);
						File f=new File(CachePath+domainlist.getSelectedValue()+"\\"+"thisinfo.txt");
						String c= domainlist.getSelectedValue();
						javax.swing.text.Document e =Info_Text.getDocument();
						BufferedReader reader;
						try {
							reader = new BufferedReader(new FileReader(f));
						
						String tempString="";
						 while((tempString=reader.readLine())!=null) {
							 if(tempString.startsWith(infolist.getSelectedValue())) {
								 tempString=changeCharset(tempString);
								 String url="js�ű���ַ:\n"+tempString.split("###")[3];
								 String api=tempString.split("###")[1];
								 String context=tempString.split("###")[2];
								 Info_Text.setDocument(insaDocument(e,  context,api, url));
								 e.insertString(e.getLength(), "###########################################################################\n", null);
							 }
						 }
						 reader.close();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						}
						
						
					
				});
                //ѡ�������ұߵ�����pane
                JSplitPane infosplitpane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true);
                
                JTextField ccx = new JTextField(16);
                ccx.setEditable(false);
                //JScrollPane zz=new JScrollPane();
                //zz.add(ccx);
                //zz.add(infolist);
                //zz.add(infolist);
                infosplitpane.setLeftComponent(jpaninfo_list);
                infosplitpane.setRightComponent(jpaninfo);
                //�ҷ�APIѡ�������
                JSplitPane APIsplitpane=new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);
                APIsplitpane.setDividerLocation(0.5);
                APIsplitpane.setOneTouchExpandable(true);
                //API�������������
                stdout.println("222!");
                APIsplitpane.setTopComponent(jpanAPI_top);
                APIsplitpane.setBottomComponent(jpanAPI_but);
                //ѡ�
                jtabbedpane=new JTabbedPane();
                jtabbedpane.scrollRectToVisible(new Rectangle(500, 70));
                jtabbedpane.addTab("API�ӿ�", APIsplitpane);
                jtabbedpane.addTab("������Ϣ", infosplitpane);
                //Table����
                jtabbedpane.addChangeListener(new ChangeListener() {
					
					@Override
					public void stateChanged(ChangeEvent arg0) {
						// TODO Auto-generated method stub
						String c= domainlist.getSelectedValue();
						String tableText=jtabbedpane.getTitleAt(jtabbedpane.getSelectedIndex());
						if (tableText=="API�ӿ�") {
						APIlist.setListData((String[])domain.get(c).toArray(new String[0]));
						}else if(tableText=="������Ϣ") {
						Info_Text.setText(null);
						try {
							ArrayList<String> vv=new ArrayList<String>();
							infosplitpane.setDividerLocation(0.3);
							infolist.setListData((String[])InfoTexMap.get(c).toArray(new String[0]));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						}
					}
				});
                //������ť����
                SearchButton.addActionListener((e)->{
                	String stc=SearchField.getText();
                	ArrayList<String> cz=new ArrayList();
                	for (Map.Entry czs:domain.entrySet()) {
                		if(stc==""||czs.getKey().toString().indexOf(stc)!=-1) {
                			cz.add(czs.getKey().toString());
                		}
                			
                	}
                	String[] domain2=(String[])cz.toArray(new String[0]);
                	Arrays.sort(domain2);
        			domainlist.setListData(domain2);
                });
				//��װ��
                //��ߵĶ���
                JSplitPane leftpane=new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);
                leftpane.setDividerLocation(0.5);
                leftpane.setOneTouchExpandable(true);
                JPanel jpz=new JPanel();
                jpz.add(SearchField);
                jpz.add(SearchButton);
                leftpane.setTopComponent(jpz);
                leftpane.setBottomComponent(jScrollPane1);
				jsplitpane.setLeftComponent(leftpane);
				jsplitpane.setRightComponent(jtabbedpane);
				
				cb.customizeUiComponent(jsplitpane);
				//���ñ�ǩ
				cb.addSuiteTab(BurpExtender.this);
				
				
			}
		});
		
	}
	@Override
	public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
		// TODO Auto-generated method stub
		listMenuItems = new ArrayList<JMenuItem>();
		
		jmOff=new JMenuItem(jm);
		jmOff.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(OnorOff==true)
				{
					OnorOff=false;
					jm="On YitaiqiFilter";
				}else {
					OnorOff=true;
					jm="off YitaiqiFilter";
				}
					
		
			}
		});
		//���㴴���Ĳ˵�����list��
	    listMenuItems.add(jmOff);
	    //�������list
	    return listMenuItems;

	}
	public static void setSysClipboardText(String writeMe) {  
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();  
        Transferable tText = new StringSelection(writeMe);  
        clip.setContents(tText, null);
        
    }
	@Override
	public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
		String Typehander="";
		if (messageInfo == null || messageIsRequest || !OnorOff) {
			return;
		}
		String Hander=new String(messageInfo.getResponse());
		String[] handerlist=Hander.split("\r\n\r\n");
		Typehander=handerlist[0].toLowerCase();
		if(Typehander.indexOf("html")!=-1 ||(CheckResponseType(hp.bytesToString(messageInfo.getResponse()))&&Typehander.indexOf("avascrip")!=-1)) {

			IHttpService is=messageInfo.getHttpService();
			
			Pattern p = Pattern.compile(REGEX,Pattern.DOTALL);
			Matcher m = p.matcher(Hander);
			String uri=ReadJsPath(hp.bytesToString(messageInfo.getRequest()));
			this.stdout.println("url is"+uri);

			if(is.getHost()+"��"+is.getPort()!=null&&!domain.keySet().contains(is.getHost()+"��"+is.getPort())) {
				List cc=new ArrayList(domain.keySet());
				cc.add(new String(is.getHost()+"��"+is.getPort()));
				String[] domain2=(String[])cc.toArray(new String[0]);
				domainlist.setListData(domain2);
				if(!domain.keySet().contains(is.getHost()+"��"+is.getPort())) {
					domain.put(is.getHost()+"��"+is.getPort(), new ArrayList<String>());
				}
				
			}
			int findStart=0;
			while(m.find(findStart)) {
				
				this.stdout.println("startis"+Integer.toString(findStart) );
				if(domain.get(is.getHost()+"��"+is.getPort()).contains(m.group(1)) || m.group(1).length()<=4 ||CheckAIPEndSwith(m.group(1))) {
					findStart=m.end()-160;
					continue;
				}
				domain.get(is.getHost()+"��"+is.getPort()).add(m.group(1));
				String data=""+m.group(1)+"###"+uri+"###"+m.group();
				data=data.replaceAll("\n", "");
				data=data.replaceAll("\r", "")+"\n";
				//this.stdout.println(data);
				findStart=m.end()-160;
				try {
					writeToFile("thisApi", is.getHost()+"��"+is.getPort(), data, false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.stdout.println("�ļ�д�����");
				}
				
				this.stdout.println("endis"+Integer.toString(m.end()) );
			}
			//��������Ϣ
			String ValueS="";
			//����֮ǰ������
			if(!InfoTexMap.keySet().contains(is.getHost()+"��"+is.getPort())) {
				InfoTexMap.put(is.getHost()+"��"+is.getPort(),new ArrayList<String>());
			}
			for (Map.Entry entry : InfoRegMap.entrySet()) {
			String key =".{20}"+entry.getKey()+".{20}";
			
			Pattern p2 = Pattern.compile(key,Pattern.DOTALL);
			Matcher m2 = p2.matcher(Hander);
			//����
			while(m2.find()) {
				//if(ValueS.indexOf(ws)==-1) {
				//} 
				try {
					if (infoisHas.contains(is.getHost()+"��"+is.getPort()+m2.group(1))){
						continue;
					}
					if(!InfoTexMap.get(is.getHost()+"��"+is.getPort()).contains(entry.getValue())) {
						InfoTexMap.get(is.getHost()+"��"+is.getPort()).add(entry.getValue());
					}
					infoisHas.add(is.getHost()+"��"+is.getPort()+m2.group(1));
					String data= entry.getValue()+"###"+m2.group(1)+"###"+m2.group()+"###"+uri;
					data=data.replaceAll("\n", "");
					data=data.replaceAll("\r", "")+"\n";
					//this.stdout.println(data);
					writeToFile("thisinfo", is.getHost()+"��"+is.getPort(),data, false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.stdout.println("�ļ�д�����");
				}
			}
			}
		}
		
	}
	public void  writeToFile(String filename,String host,String data,Boolean isJs) throws IOException {
		
		File file=new File(CachePath+host+"\\"+filename+".txt");
		File fileParent = file.getParentFile();
		if(!fileParent.exists()) {
			fileParent.mkdirs();
		}
		if(!file.exists()) {
			file.createNewFile();
		}
		FileWriter fileWritter = new FileWriter(CachePath+host+"\\"+filename+".txt",true);
		try {
			fileWritter.write(data);
			
		    fileWritter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			stdout.println("�ļ���������");
			
		}
		fileWritter.close();
		
	}
	public void LoadConfig() throws IOException {
		File file=new File(CachePath+"../Config.txt");
		File fileParent = file.getParentFile();
		if(!fileParent.exists()) {
			fileParent.mkdirs();
		}
		stdout.println(file.getPath());
		if(!file.exists()) {
			file.createNewFile();
			stdout.println("�����ļ����ɳɹ�");
			String data="([\'\"][0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}[\'\"])###ip\n"
					+ "([\'\"]1[3|4|5|7|8|9][0-9]{9}[\'\"])###�ֻ���\n";
			FileWriter fileWritter = new FileWriter(file);
			fileWritter.write(data);
			fileWritter.close();
			stdout.println("��ʼ����д�����");
		}
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String tempString = null;
		while((tempString=reader.readLine())!=null) {
			InfoRegMap.put(tempString.split("###")[0], tempString.split("###")[1]);
		}
		reader.close();
		stdout.println("����װ�����");
		
		
	}
	public void LoadReg() throws IOException 
	{
		File file=new File(CachePath+"../Regex.txt");
		File fileParent = file.getParentFile();
		if(!fileParent.exists()) {
			fileParent.mkdirs();
		}
		stdout.println(file.getPath());
		if(!file.exists()) {
			file.createNewFile();
			stdout.println("ƥ������ļ����ɳɹ�");
			String data="[\"\'`](/[a-zA-Z0-9/=_{}\\?&!]+(\\.jspx|\\.jsp|\\.html|\\.php|\\.do|\\.aspx|\\.action|\\.json)*)[\"\'`]";
			FileWriter fileWritter = new FileWriter(file);
			fileWritter.write(data);
			fileWritter.close();
			stdout.println("��ʼ����д�����");
		}
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String tempString = null;
		while((tempString=reader.readLine())!=null) {
			
			if(tempString.replace("\r", "").replace("\n", "")!="")
			{
				
				String temp=".{10}"+tempString.replace("\r", "").replace("\n", "")+".{160}";
				REGEX=temp;
			}
			
		}
		reader.close();
		stdout.println("�ӿ�ƥ�����װ�����");
		
		
		
	}
	public String ReadJsPath(String s) {
		String cz=s.split("HTTP")[0];
		String reg="^[h|H]ost:(.*?)$";
		Pattern p = Pattern.compile(reg,Pattern.MULTILINE);
		Matcher m = p.matcher(s);
		if(cz.indexOf(".js")!=-1) {
			String path=s.split("\\.js")[0].replace("GET", "");
			path=path.replace("\n", "");
			if(m.find()) {
				return ("http://"+m.group(1)+path+".js").replaceAll(" ", "");
			}
			
		}else {
			String path=cz.replace("GET", "").replace("POST", "").replace("\n", "");
			if(m.find()) {
				return ("http://"+m.group(1)+path).replaceAll(" ", "");
			}
			
		}
		return " ";
		
	}
	public Boolean StringInList(String[] s,String c) {
		for(String z:s) {
			if (z==c) {
				return true;
			}
		}
		
		return false;
		
	}
	//�ж��Ƿ��Էǽӿں�׺��β
	public Boolean CheckAIPEndSwith(String s) {
		//����Ƿ�·����׺
		String[] c= {".jpg",".png",".js",".css",".jpeg",".gif"};
		for(String w:c) {
			if(s.endsWith(w)) {
				return true;
			}
		}
		
		return false;
	}
	//�ж��Ƿ��Ǹ�js�ļ���ĳЩ�ӿڷ�������Ҳ��javascript
	//����������ֽӿں�js�ļ���Ϊ�˷�ֹ���������ӿ���Я����url���ӣ�ͨ��һЩjs�Ĺؼ������жϡ�
	public Boolean CheckResponseType(String s) {
		//String cz=s.split("HTTP")[0];
		//����һ������������ÿ��⵽����һ��������+1
		int checkNumber=0;
		String[] checkPoints= {"function ","if","return ","catch","for"};
		for(String checkPoint : checkPoints)
		{
			
			if(s.indexOf(checkPoint)!=-1)
			{
				checkNumber++;
				
			}
		}
		//�������3�����Ϲ�����ȷ��Ϊjs�ļ����ݡ�
		if(checkNumber>=3)
			return true;
		return false;
		
	}
	//�����������
	 public String changeCharset(String str)  
			   throws UnsupportedEncodingException {  
			  if (str != null) {  
			   //��Ĭ���ַ���������ַ�����  
			   byte[] bs = str.getBytes("utf-8");  
			   //���µ��ַ����������ַ���  
			   return new String(bs, "utf-8");  
			  }  
			  return null;  
			 }
	 //tzΪȫ�����ݣ�tz2ΪҪ�����Ĺؼ��֣�urlΪapi��url
	 public javax.swing.text.Document insaDocument(javax.swing.text.Document dc,String tz,String tz2,String url) {
		 String api=tz2;
		 String context=tz;
		 String leftT=context.split(api)[0].replace(";", ";\n");
		 leftT=leftT.replace("{", "\n{\n");
		 leftT=leftT.replace("}", "\n}\n");
		 leftT=leftT.replace(",", ",\n");
		 String righT=context.split(api)[1].replace(";", ";\n");
		 righT=righT.replace("{", "\n{\n");
		 righT=righT.replace("}", "\n}\n");
		 righT=righT.replace(",", ",\n");
		 try {
		 dc.insertString(dc.getLength(), url+"\n\n\n", null);
		 dc.insertString(dc.getLength(), "�ؼ���������:\n", null);
		 String indentation="";
		 String[] zb=leftT.split("\n");
		 for(String cs:zb) {
			 if(cs.indexOf("{")!=-1) {
				 indentation+="  ";
			 }else if(cs.indexOf("}")!=-1&&cs.length()>=2) {
				 indentation=indentation.substring(0, indentation.length()-2);
			 }
			 if(cs!=zb[zb.length-1]) {
				 dc.insertString(dc.getLength(),   indentation+cs+"\n", null);
			 }else {
				 dc.insertString(dc.getLength(), indentation+cs, null);
			 }
			
		 }
		 dc.insertString(dc.getLength(), api,attrSet);
		 for(String cs:righT.split("\n")) {
			 if(cs.indexOf("{")!=-1) {
				 indentation+="  ";
			 }else if(cs.indexOf("}")!=-1&&cs.length()>=2) {
				 indentation=indentation.substring(0, indentation.length()-2);
			 }
			 dc.insertString(dc.getLength(), indentation+cs+"\n", null);
		 }
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return dc;
	 }
	@Override
	public void extensionUnloaded() {
		// TODO Auto-generated method stub
		CacheFileunLoad(CachePath);
		
		
	}
	public void CacheFileunLoad(String path) {
		File fil=new File(path);
		File[] filelist=fil.listFiles();
		if(filelist!=null) {
			for(File f:filelist) 
			{

				if(f.isDirectory()) {
					//stdout.println(f.getPath());
					CacheFileunLoad(f.getPath());
					f.delete();
				}else {
					f.delete();
					stdout.println("�����ļ������.");
				}
				
			}
		}
	}

}