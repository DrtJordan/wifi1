package com.ysu.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.ysu.dao.WifiDao;
import com.ysu.dao.macDao;
import com.ysu.model.WifiInfoBean;
import com.ysu.model.macInfoBean;

//读取源数据，过滤，存储，生成日志
public class localToHbase {
	
	public static void main(String[] args) {
		
	}

	public static void pickwords(){
		File file = new File(Constants.PATH_INPUT);
		File file2 = new File(Constants.PATH_OUTPUT);
		try {
			if (!file2.exists()) {
				file2.createNewFile();
			}
			FileWriter fw = new FileWriter(file2, true);
			BufferedWriter bw = new BufferedWriter(fw);
			BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				// 在这切分
				String[] tmpStr = s.split(" ");
				WifiInfoBean wifiInfoBean=new WifiInfoBean();
				
				
				
				// 如果是空行，则跳过
				if(tmpStr.length>23){
				//封装到wifiInfoBean
				String signal="";
				signal=tmpStr[8].replaceAll("-", "");
				signal=signal.replaceAll("dB", "");
				wifiInfoBean.setLogtime(tmpStr[0]);
				wifiInfoBean.setSignal(Integer.valueOf(signal));
				wifiInfoBean.setMac(tmpStr[12].substring(6));
				wifiInfoBean.setWifiname(tmpStr[23]);
				// 保存到hbase
				WifiDao wifiDao=new WifiDao();
				wifiDao.insert(wifiInfoBean);
				
				// 保存到本地文件，格式为2017-10-01.log
				String content = wifiInfoBean.getLogtime() + " " + wifiInfoBean.getSignal() + " " 
				                 + wifiInfoBean.getMac() + " " + wifiInfoBean.getWifiname() + "\n";
				bw.write(content);
				}
			}
			bw.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//导入mac和厂商对应的hbase，读入文件oui.csv
	public static void pickMac(){
		File file = new File(Constants.PATH_MAC_INPUT);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				// 在这切分
				String[] tmpStr = s.split(",");
				macInfoBean macInfoBean = new macInfoBean();
				// 如果是空行，则跳过
				if(tmpStr.length>3){
				//封装到macInfoBean
				String productor="";
				macInfoBean.setMac(tmpStr[1].substring(0, 1)+":" + tmpStr[1].substring(2, 3)+":"+tmpStr[1].substring(4, 5));
				macInfoBean.setProductor(tmpStr[2]);
				// 保存到hbase
				macDao.insert(macInfoBean);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
