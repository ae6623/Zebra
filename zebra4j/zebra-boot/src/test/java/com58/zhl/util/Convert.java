package com58.zhl.util;

public class Convert {
	
	public static String toHexString(byte b){
		StringBuilder sb=new StringBuilder();
		int n=b<<24>>>24;	//因为位运算时都会转换为int型，因此需按照int型的去除符号位的方式来判断
		if(n<16){
			sb.append("0");
			sb.append(getHexChar((byte)(n%16)));
		}else{
			sb.append(getHexChar((byte)(n/16))).append(getHexChar((byte)(n%16)));
		}
		return sb.toString();
	}
	
	private static char getHexChar(byte b){
		switch(b){
		case 10:return 'A';
		case 11:return 'B';
		case 12:return 'C';
		case 13:return 'D';
		case 14:return 'E';
		case 15:return 'F';
		default:return ((char)(b%10+48));
		}
	}

	public static void main(String args[]){
		byte b=-66;
		int n=b<<24>>>24;
		System.out.println(Integer.toBinaryString(n)+"==="+n+"==="+(byte)b);
	}
	
}
