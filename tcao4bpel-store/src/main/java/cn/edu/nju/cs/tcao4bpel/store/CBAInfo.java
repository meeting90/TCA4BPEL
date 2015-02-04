package cn.edu.nju.cs.tcao4bpel.store;

import java.io.File;

import javax.xml.namespace.QName;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * CBAInfo.java
 */
public final class CBAInfo {
	final QName aspectName;
	final File cba;
	public CBAInfo(QName aspectName, File cba) {
		this.aspectName =aspectName;
		this.cba = cba;
	}
}
