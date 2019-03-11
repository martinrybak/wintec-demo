package cn.wintec.CpuCardRead;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import cn.wintec.wtandroidjar.ComIO;
import cn.wintec.wtandroidjar.Rfid;

public class CpuCardReader {

	protected String devicePath; 					
	protected ComIO.Baudrate baudrate; 	
	protected Rfid rfid; 									

	public CpuCardReader() {
		devicePath = "/dev/ttySAC1";							
		baudrate = ComIO.Baudrate.BAUD_115200;		
		rfid = new Rfid(devicePath, baudrate); 	
	}

	
	public boolean formatCard() {
		boolean rel = false;
		rel = cpuCosOpen();
		if (!rel)
			return rel;
		rel = cpuCosCardReset();
		if (!rel)
			return rel;
		rel = cpuCosExternalAuth();
		if (!rel)
			return rel;
		rel = cpuCosEraseMF();
		if (!rel)
			return rel;
		rel = cpuCosInitKeys();
		if (!rel)
			return rel;
		rel = cpuCosCreateEFUserInfo((byte) 0x15);
		if (!rel)
			return rel;
		rel = cpuCosCreateEFUserInfo((byte) 0x16);
		if (!rel)
			return rel;
		rel = cpuCosCreateEFUserInfo((byte) 0x17);
		if (!rel)
			return rel;

		byte[] tmpDate = new byte[20];
		for (int i = 0; i < 8; i++) {
			tmpDate[i] = 0x30;
		}
		rel = cpuCosWriteEFtranSummary(tmpDate, 20);
		if (!rel)
			return rel;
		rel = cpuCosClose();
		return rel;
	}


	public boolean writeCardUserInfo(UserInfo userInfo)
			throws UnsupportedEncodingException {
		boolean rel = false;
		byte[] lcUserinfo = new byte[120];
		byte[] lcCardType;
		byte[] lcCardSerial = new byte[20];
		byte[] lcUserID;
		byte[] lcUserName;
		byte[] lcCertSerial;
		byte[] lcUserType;
		byte[] lcPhoneNum;
		for (int i = 0; i < 120; i++) {
			lcUserinfo[i] = 0x20;
		}

		lcCardType = userInfo.cardType.getBytes();

		System.arraycopy(userInfo.cardSerial.getBytes(), 0, lcCardSerial, 7, 13);
		lcCardSerial = ASCII_To_BCD(lcCardSerial, 20);

		lcUserID = userInfo.userID.getBytes();
		lcUserName = userInfo.userName.getBytes("GBK");
		lcCertSerial = userInfo.certSerial.getBytes();
		lcUserType = userInfo.userType.getBytes();
		lcPhoneNum = userInfo.phoneNum.getBytes();
		
		if (lcCardType.length != 2)
			return false;
		if (lcCardSerial.length != 10)
			return false;
		if (lcUserID.length != 13)
			return false;
		if (lcUserName.length > 30 || lcUserName.length == 0)
			return false;
		if (lcCertSerial.length > 18 || lcCertSerial.length == 0)
			return false;
		if (lcPhoneNum.length > 13 || lcPhoneNum.length == 0)
			return false;
		if (lcUserType.length != 2)
			return false;

		System.arraycopy(lcCardType, 0, lcUserinfo, 0, 2);
		System.arraycopy(lcCardSerial, 0, lcUserinfo, 2, 10);
		System.arraycopy(lcUserID, 0, lcUserinfo, 12, 13);
		System.arraycopy(lcUserName, 0, lcUserinfo, 25, lcUserName.length);
		System.arraycopy(lcCertSerial, 0, lcUserinfo, 55, lcCertSerial.length);
		System.arraycopy(lcPhoneNum, 0, lcUserinfo, 73, lcPhoneNum.length);
		System.arraycopy(lcUserType, 0, lcUserinfo, 86, 2);

		rel = cpuCosOpen(); 
		if (!rel)
			return rel;
		rel = cpuCosCardReset(); 
		if (!rel)
			return rel;
		rel = cpuCosExternalAuth(); 
		if (!rel)
			return rel;
		rel = cpuCosWriteEFUserInfo(lcUserinfo, lcUserinfo.length); 
		if (!rel)
			return rel;
		rel = cpuCosClose();
		return rel;
	}

	public UserInfo readCardUserInfo() {
		UserInfo userInfo = new UserInfo();
		boolean rel = false;
		byte[] lcUserinfo = new byte[122];

		rel = cpuCosOpen(); 
		if (!rel)
			return null;
		rel = cpuCosCardReset(); 
		if (!rel)
			return null;
		rel = cpuCosExternalAuth(); 
		if (!rel)
			return null;
		int len = cpuCosReadEFUserInfo(lcUserinfo); 
		if (len != 122)
			return null;

		byte[] lcCardType = new byte[2];
		byte[] lcCardSerial = new byte[10];
		byte[] lcUserID = new byte[13];
		byte[] lcUserName = new byte[30];
		byte[] lcCertSerial = new byte[18];
		byte[] lcUserType = new byte[2];
		byte[] lcPhoneNum = new byte[13];

		System.arraycopy(lcUserinfo, 0, lcCardType, 0, 2);
		System.arraycopy(lcUserinfo, 2, lcCardSerial, 0, 10);
		System.arraycopy(lcUserinfo, 12, lcUserID, 0, 13);
		System.arraycopy(lcUserinfo, 25, lcUserName, 0, 30);
		System.arraycopy(lcUserinfo, 55, lcCertSerial, 0, 18);
		System.arraycopy(lcUserinfo, 73, lcPhoneNum, 0, 13);
		System.arraycopy(lcUserinfo, 86, lcUserType, 0, 2);

		userInfo.cardType = new String(lcCardType);
		String cardserialstr = bcd2Str(lcCardSerial);
		userInfo.cardSerial = cardserialstr.substring(7);
		userInfo.userID = new String(lcUserID);
		try {
			userInfo.userName = new String(lcUserName, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		userInfo.certSerial = new String(lcCertSerial);
		userInfo.phoneNum = new String(lcPhoneNum);
		userInfo.userType = new String(lcUserType);

		rel = cpuCosClose(); 
		if (!rel)
			return null;
		return userInfo;
	}

	public String readSummary(String transDate, byte[] transCount) {
		boolean rel = false;
		byte[] summary = new byte[20];
		rel = cpuCosOpen();
		if (!rel)
			return null;
		rel = cpuCosCardReset(); 
		if (!rel)
			return null;
		rel = cpuCosExternalAuth(); 
		if (!rel)
			return null;
		int len = cpuCosReadEFtranSummary(summary);
		if (len != 20) {
			return null;
		}

		byte[] lcDate = new byte[8];
		System.arraycopy(summary, 0, lcDate, 0, 8);
		transDate = new String(lcDate);
		transCount[0] = summary[8];
		rel = cpuCosClose();
		return transDate;
	}

	public boolean appendTranDetail(TradeInfo trade)
			throws UnsupportedEncodingException {
		boolean rel = false;
		byte[] trandetail = new byte[200];
		byte[] tranCode;
		byte[] batchCode;
		byte[] salerID;
		byte[] salerName;
		byte[] farmName;
		byte[] nodeID;
		byte[] nodeName;
		byte[] goodsCode;
		byte[] areaCode;
		byte[] goodsCount;
		byte[] goodsWeight;
		byte[] goodsPrice;
		byte[] vehicleLicence;

		for (int i = 0; i < 200; i++) {
			trandetail[i] = 0x20;
		}

		tranCode = trade.tranCode.getBytes(); 					
		batchCode = trade.batchCode.getBytes(); 				
		salerID = trade.salerID.getBytes(); 							
		salerName = trade.salerName.getBytes("GBK"); 	
		farmName = trade.farmName.getBytes("GBK"); 	
		nodeID = trade.nodeID.getBytes(); 	
		nodeName = trade.nodeName.getBytes("GBK"); 	
		goodsCode = trade.goodsCode.getBytes();	
		areaCode = trade.areaCode.getBytes(); 
		goodsCount = intToByteArray1(trade.goodsCount); 
		goodsWeight = intToByteArray1((int) (trade.goodsWeight * 100));
		int price = (int) (trade.goodsPrice * 100);
		goodsPrice = String.valueOf(price).getBytes(); 
		vehicleLicence = trade.vehicleLicence.getBytes(); 

		if (tranCode.length != 20)
			return false;
		if (batchCode.length > 16 || batchCode.length == 0)
			return false;
		if (salerID.length != 13)
			return false;
		if (salerName.length > 30 || salerName.length == 0)
			return false;
		if (farmName.length > 40)
			return false;
		if (nodeID.length != 9 && nodeID.length != 0)
			return false;
		if (nodeName.length > 30)
			return false;
		if (goodsCode.length > 8 || goodsCode.length == 0)
			return false;
		if (areaCode.length != 6)
			return false;
		if (vehicleLicence.length != 8 && vehicleLicence.length != 0)
			return false;
		System.arraycopy(tranCode, 0, trandetail, 0, 20);
		System.arraycopy(batchCode, 0, trandetail, 20, 16);
		System.arraycopy(salerID, 0, trandetail, 36, 13);
		System.arraycopy(salerName, 0, trandetail, 49, salerName.length);
		System.arraycopy(farmName, 0, trandetail, 79, farmName.length);
		System.arraycopy(nodeID, 0, trandetail, 119, 9);
		System.arraycopy(nodeName, 0, trandetail, 128, nodeName.length);
		System.arraycopy(goodsCode, 0, trandetail, 158, goodsCode.length);
		System.arraycopy(areaCode, 0, trandetail, 166, 6);
		System.arraycopy(goodsCount, 0, trandetail, 172, 4);
		System.arraycopy(goodsWeight, 0, trandetail, 176, 4);
		System.arraycopy(goodsPrice, 0, trandetail, 180, goodsPrice.length);
		System.arraycopy(vehicleLicence, 0, trandetail, 188, 8);
		trandetail[196] = 0x30;

		rel = cpuCosOpen();
		if (!rel)
			return rel;
		rel = cpuCosCardReset(); 
		if (!rel)
			return rel;
		rel = cpuCosExternalAuth(); 
		if (!rel)
			return rel;
		rel = cpuCosAppendEFtranDetail(trade.transDate, trandetail,
				trandetail.length);
		if (!rel)
			return rel;
		rel = cpuCosClose();
		return rel;
	}

	public boolean setTranDetailState(byte index, byte state) {
		boolean rel = false;
		rel = cpuCosOpen();
		if (!rel)
			return rel;
		rel = cpuCosCardReset(); 
		if (!rel)
			return rel;
		rel = cpuCosExternalAuth();
		if (!rel)
			return rel;
		rel = cpuCosSetStateEFtranDetail(index, state);
		if (!rel)
			return rel;
		rel = cpuCosClose();
		return rel;
	}

	public TradeInfo readTranDetail(byte index) {
		TradeInfo trade = new TradeInfo();
		boolean rel = false;
		int len;
		byte[] trandetail = new byte[200];

		if (index > 35 || index < 0) {
			return null;
		}
		rel = cpuCosOpen();
		if (!rel)
			return null;
		rel = cpuCosCardReset(); 
		if (!rel)
			return null;
		rel = cpuCosExternalAuth(); 
		if (!rel)
			return null;
		len = cpuCosReadEFtranDetail(index, trandetail);
		if (len != 200)
			return null;

		byte[] tranCode = new byte[20]; 		
		byte[] batchCode = new byte[16]; 		
		byte[] salerID = new byte[13]; 			
		byte[] salerName = new byte[30]; 		
		byte[] farmName = new byte[40]; 		
		byte[] nodeID = new byte[9]; 			
		byte[] nodeName = new byte[30]; 		
		byte[] goodsCode = new byte[8]; 		
		byte[] areaCode = new byte[6]; 			
		byte[] goodsCount = new byte[4];	 	
		byte[] goodsWeight = new byte[4]; 	
		byte[] goodsPrice = new byte[8]; 
		byte[] vehicleLicence = new byte[8];

		System.arraycopy(trandetail, 0, tranCode, 0, 20);
		trade.tranCode = new String(tranCode);
		System.arraycopy(trandetail, 20, batchCode, 0, 16);
		trade.batchCode = new String(batchCode);
		System.arraycopy(trandetail, 36, salerID, 0, 13);
		trade.salerID = new String(salerID);
		System.arraycopy(trandetail, 49, salerName, 0, 30);
		try {
			trade.salerName = new String(salerName, "GBK");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		System.arraycopy(trandetail, 79, farmName, 0, 40);
		try {
			trade.farmName = new String(farmName, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.arraycopy(trandetail, 119, nodeID, 0, 9);
		trade.nodeID = new String(nodeID);
		System.arraycopy(trandetail, 128, nodeName, 0, 30);
		try {
			trade.nodeName = new String(nodeName, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.arraycopy(trandetail, 158, goodsCode, 0, 8);
		trade.goodsCode = new String(goodsCode);
		System.arraycopy(trandetail, 166, areaCode, 0, 6);
		trade.areaCode = new String(areaCode);
		System.arraycopy(trandetail, 172, goodsCount, 0, 4);
		trade.goodsCount = byteArrayToInt(goodsCount, 0);
		System.arraycopy(trandetail, 176, goodsWeight, 0, 4);
		trade.goodsWeight = byteArrayToInt(goodsWeight, 0) * 0.01;
		System.arraycopy(trandetail, 180, goodsPrice, 0, 8);
		String price = new String(goodsPrice);
		trade.goodsPrice = Integer.parseInt(price.trim()) * 0.01;
		System.arraycopy(trandetail, 188, vehicleLicence, 0, 8);
		trade.vehicleLicence = new String(vehicleLicence);
		trade.status = trandetail[196];
		rel = cpuCosClose();
		if (!rel)
			return null;
		return trade;
	}

	public boolean cpuCosOpen() {
		boolean ret =  rfid.RFID_EnableAntenna();
		if(!ret)
		{
			rfid.RFID_PosCardReset();
			ret = rfid.RFID_EnableAntenna();
		}
		return ret;
	}

	public boolean cpuCosClose() {
		boolean ret =  rfid.RFID_DisableAntenna();
		if(!ret)
		{
			rfid.RFID_PosCardReset();
			ret = rfid.RFID_EnableAntenna();
		}
		return ret;
	}

	public byte SearchCards() {
		byte[] cardtype = new byte[2];
		boolean rel = false;
		rel = cpuCosOpen();
		rel = rfid.RFID_SearchCards(cardtype);
		rel = cpuCosClose();
		if (rel) {
			return cardtype[0];
		} else {
			return 0x00;
		}
	}

	public boolean cpuCosRandom(int type, byte[] ranData) {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[100];
		byte[] command_data = { 0x00, (byte) 0x84, 0x00, 0x00, 0x04 }; 
		if (type == 1)
			command_data[4] = 0x08; 
		rel = rfid.RFID_Cos_Command(command_data.length, command_data, rlen,
				receive_data); 
		System.arraycopy(receive_data, 0, ranData, 0, (type + 1) * 4); 
		return rel;
	}

	public boolean cpuCosExternalAuth() {
		boolean rel = false; 
		byte[] rlen = new byte[1]; 
		byte[] receive_data = new byte[100]; 
		byte[] plainData = new byte[8]; 
		byte[] cipherData = new byte[8]; 
		byte[] command_Data = new byte[13]; 
		byte[] ExternalAuthHead = { 0x00, (byte) 0x82, 0x00, 0x00, 0x08 };
		byte[] key = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
				(byte) 0xFF, (byte) 0xFE, (byte) 0xFD, (byte) 0xFC,
				(byte) 0xFB, (byte) 0xFA, (byte) 0xF9, (byte) 0xF8, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

		rel = cpuCosRandom(1, plainData);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (!rel)
			return rel;
		try {
			System.arraycopy(des3EncodeECB(key, plainData), 0, cipherData, 0, 8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.arraycopy(ExternalAuthHead, 0, command_Data, 0, 5);
		System.arraycopy(cipherData, 0, command_Data, 5, 8);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rel = rfid.RFID_Cos_Command(command_Data.length, command_Data, rlen,
				receive_data);
		return rel;
	}

	public boolean cpuCosSlectEFFileInfo(byte byt) {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[100];
		byte[] command_data = { 0x00, (byte) 0xA4, 0x00, 0x00, 0x02, 0x00, byt };
		rel = rfid.RFID_Cos_Command(command_data.length, command_data, rlen,
				receive_data);
		return rel;
	}

	public int cpuCosReadEFUserInfo(byte[] outData) {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		byte[] command_data = { 0x00, (byte) 0xB0, 0x00, 0x00, 0x78 };
		rel = cpuCosSlectEFFileInfo((byte) 0x15);
		if (!rel)
			return -1;
		rel = rfid.RFID_Cos_Command(command_data.length, command_data, rlen,
				receive_data,200);
		if (rel) {
			System.arraycopy(receive_data, 0, outData, 0, rlen[0]);
		}
		return rlen[0];
	}

	public boolean cpuCosWriteEFUserInfo(byte[] inData, int dataLen) {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		byte[] command_dataHead = { 0x00, (byte) 0xD6, 0x00, 0x00, 0x78 };
		byte[] command_data = new byte[255];
		rel = cpuCosSlectEFFileInfo((byte) 0x15);
		if (!rel)
			return rel;
		System.arraycopy(command_dataHead, 0, command_data, 0,
				command_dataHead.length);
		System.arraycopy(inData, 0, command_data, 5, dataLen);
		rel = rfid.RFID_Cos_Command(command_dataHead.length + dataLen,
				command_data, rlen, receive_data,300);
		return rel;
	}

	public boolean cpuCosAppendEFtranDetail(String strSysTime, byte[] inData,
			int dataLen) {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		byte[] command_dataHead = { 0x00, (byte) 0xE2, 0x00, 0x04, (byte) 0xC8 };
		byte[] command_data = new byte[255];

		byte[] sumData = new byte[20];
		byte[] rTime = new byte[8];
		byte count;
		int sumLen = cpuCosReadEFtranSummary(sumData);
		if (sumLen <= 0)
			return false;
		System.arraycopy(sumData, 0, rTime, 0, 8);
		count = sumData[8];
		String strFileTIme = new String(rTime);

		if (strFileTIme.equals(strSysTime)) { 
			if (count >= 35)
				return false;
			count += 1;
		} else {
			System.arraycopy(strSysTime.getBytes(), 0, rTime, 0, 8);
			count = 1;
		}
		System.arraycopy(rTime, 0, sumData, 0, 8);
		sumData[8] = count;
		rel = cpuCosWriteEFtranSummary(sumData, sumLen);
		if (!rel)
			return rel;
		rel = cpuCosSlectEFFileInfo((byte) 0x17);
		if (!rel)
			return rel;

		System.arraycopy(command_dataHead, 0, command_data, 0,
				command_dataHead.length);
		System.arraycopy(inData, 0, command_data, 5, dataLen);
		rel = rfid.RFID_Cos_Command(command_dataHead.length + dataLen,
				command_data, rlen, receive_data,500);
		if (!rel) {
			count -= 1;
			sumData[8] = count;
			cpuCosWriteEFtranSummary(sumData, sumLen);
			return false;
		}
		return rel;
	}

	public boolean cpuCosUpdateEFtranDetail(byte index, byte[] inData,
			int dataLen) {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		byte[] command_dataHead = { 0x00, (byte) 0xDC, 0x01, 0x04, (byte) 0xC8 };
		byte[] command_data = new byte[255];
		rel = cpuCosSlectEFFileInfo((byte) 0x17);
		if (!rel)
			return rel;
		System.arraycopy(command_dataHead, 0, command_data, 0,
				command_dataHead.length);
		System.arraycopy(inData, 0, command_data, 5, dataLen);
		command_data[2] = index;
		rel = rfid.RFID_Cos_Command(command_dataHead.length + dataLen,
				command_data, rlen, receive_data);
		return rel;
	}

	public boolean cpuCosSetStateEFtranDetail(byte index, byte state) {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		byte[] command_dataHead = { 0x00, (byte) 0xDC, 0x01, 0x04, (byte) 0xC8 };
		byte[] command_data = new byte[255];
		rel = cpuCosSlectEFFileInfo((byte) 0x17);
		if (!rel)
			return rel;
		command_data[0] = 0x00;
		command_data[1] = (byte) 0xB2;
		command_data[2] = index;
		command_data[3] = 0x04;
		command_data[4] = (byte) 0xC8;
		rel = rfid.RFID_Cos_Command(5, command_data, rlen, receive_data);

		receive_data[196] = state;
		System.arraycopy(command_dataHead, 0, command_data, 0,
				command_dataHead.length);
		System.arraycopy(receive_data, 0, command_data, 5, rlen[0]);
		command_data[2] = index;
		rel = rfid.RFID_Cos_Command(command_dataHead.length + rlen[0],
				command_data, rlen, receive_data);
		return rel;
	}

	public int cpuCosReadEFtranDetail(byte index, byte[] outData) {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		byte[] command_data = { 0x00, (byte) 0xB2, 0x00, 0x04, (byte) 0xC8 };
		rel = cpuCosSlectEFFileInfo((byte) 0x17);
		if (!rel)
			return -1;
		command_data[2] = index;
		rel = rfid.RFID_Cos_Command(command_data.length, command_data, rlen,
				receive_data,500);
		if (rel) {
			System.arraycopy(receive_data, 0, outData, 0, 200);
		}
		return 0xC8;
	}

	public int cpuCosReadEFtranSummary(byte[] outData) {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		byte[] command_data = { 0x00, (byte) 0xB0, 0x00, 0x00, 0x14 };
		rel = cpuCosSlectEFFileInfo((byte) 0x16);
		if (!rel)
			return -1;
		rel = rfid.RFID_Cos_Command(command_data.length, command_data, rlen,
				receive_data);
		if (rel) {
			System.arraycopy(receive_data, 0, outData, 0, rlen[0] - 2);
		}
		return rlen[0] - 2;
	}

	public boolean cpuCosWriteEFtranSummary(byte[] inData, int dataLen) {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		byte[] command_dataHead = { 0x00, (byte) 0xD6, 0x00, 0x00, 0x14 }; 
		byte[] command_data = new byte[255];
		rel = cpuCosSlectEFFileInfo((byte) 0x16);
		if (!rel)
			return rel;
		System.arraycopy(command_dataHead, 0, command_data, 0,
				command_dataHead.length);
		System.arraycopy(inData, 0, command_data, 5, dataLen);
		rel = rfid.RFID_Cos_Command(command_dataHead.length + dataLen,
				command_data, rlen, receive_data);
		return rel;
	}

	public boolean cpuCosCreateEFUserInfo(byte byt) {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		byte[] command_data = { (byte) 0x80, (byte) 0xE0, 0x00, 0x15, 0x07,
				0x28, 0x00, 0x78, (byte) 0xF0, (byte) 0xF0, (byte) 0xFF,
				(byte) 0xFF }; 
		command_data[3] = byt;
		if (byt == 0x16) {
			command_data[7] = 0x14;
		} else if (byt == 0x17) {
			command_data[5] = 0x2E;
			command_data[6] = 0x23;
			command_data[7] = (byte) 0xC8;
		}
		rel = rfid.RFID_Cos_Command(command_data.length, command_data, rlen,
				receive_data);
		return rel;
	}

	public boolean cpuCosInitKeys() {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		byte[] command_data = { (byte) 0x80, (byte) 0xE0, 0x00, 0x00, 0x07,
				0x3F, 0x00, 0x50, 0x01, (byte) 0xF0, (byte) 0xFF, (byte) 0xFF }; 
		byte[] command_data1 = { (byte) 0x80, (byte) 0xD4, 0x01, 0x00, 0x15,
				0x39, (byte) 0xF0, (byte) 0xF0, (byte) 0xAA, 0x33, 0x01, 0x02,
				0x03, 0x04, 0x05, 0x06, 0x07, 0x08, (byte) 0xFF, (byte) 0xFE,
				(byte) 0xFD, (byte) 0xFC, (byte) 0xFB, (byte) 0xFA,
				(byte) 0xF9, (byte) 0xF8 }; 
		byte[] command_data2 = { (byte) 0x80, (byte) 0xD4, 0x01, 0x00, 0x0D,
				0x36, (byte) 0xF0, (byte) 0xF0, (byte) 0xFF, 0x33, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF }; 

		rel = cpuCosSelectMF(); 

		rel = rfid.RFID_Cos_Command(command_data.length, command_data, rlen,
				receive_data);
		if (!rel)
			return rel;

		rel = rfid.RFID_Cos_Command(command_data1.length, command_data1, rlen,
				receive_data);
		if (!rel)
			return rel;

		rel = rfid.RFID_Cos_Command(command_data2.length, command_data2, rlen,
				receive_data);
		if (!rel)
			return rel;

		return rel;
	}

	public boolean cpuCosEraseMF() {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		byte[] command_data = { (byte) 0x80, 0x0E, 0x00, 0x00, 0x00 }; 
		rel = cpuCosExternalAuth(); 
		if (!rel) {
			rel = cpuCosSelectMF();
			if (!rel)
				return rel;
		}
		rel = rfid.RFID_Cos_Command(command_data.length, command_data, rlen,
				receive_data);
		return rel;
	}

	public boolean cpuCosSelectMF() {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		byte[] command_data = { 0x00, (byte) 0xA4, 0x00, 0x00, 0x02, 0x3F, 0x00 }; 
		rel = rfid.RFID_Cos_Command(command_data.length, command_data, rlen,
				receive_data);
		return rel;
	}

	public boolean cpuCosCardReset() {
		boolean rel = false;
		byte[] rlen = new byte[1];
		byte[] receive_data = new byte[255];
		rel = rfid.RFID_Pro_Reset((byte) 0x52, rlen, receive_data);
		return rel;
	}

	private static String bcd2Str(byte[] bytes) {
		char[] temp = new char[bytes.length * 2];
		char val;

		for (int i = 0; i < bytes.length; i++) {
			val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

			val = (char) (bytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		return new String(temp);
	}

	private static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = asc_to_bcd(ascii[j++]);
			bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
		}
		return bcd;
	}

	private static byte asc_to_bcd(byte asc) {
		byte bcd;
		if ((asc >= '0') && (asc <= '9'))
			bcd = (byte) (asc - '0');
		else if ((asc >= 'A') && (asc <= 'F'))
			bcd = (byte) (asc - 'A' + 10);
		else if ((asc >= 'a') && (asc <= 'f'))
			bcd = (byte) (asc - 'a' + 10);
		else
			bcd = (byte) (asc - 48);
		return bcd;
	}

	private static byte[] des3EncodeECB(byte[] key, byte[] data)
			throws Exception {

		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);

		Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");

		cipher.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] bOut = cipher.doFinal(data);

		return bOut;
	}

	private static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = i * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	private static byte[] intToByteArray1(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) (i & 0xFF);
		result[1] = (byte) ((i >> 8) & 0xFF);
		result[2] = (byte) ((i >> 16) & 0xFF);
		result[3] = (byte) ((i >> 24) & 0xFF);
		return result;
	}
}